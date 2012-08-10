package tests;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import entity.BotEntity;
import entity.Entity;
import entity.FoodEntity;
import entity.bot.Message;
import game.GameManager;
import game.World;
import game.Settings;


import org.junit.Before;
import org.junit.Test;

import ca.camosun.jwill392.datatypes.grid2d.point.AbsPos;
import ca.camosun.jwill392.datatypes.grid2d.point.RelPos;


import action.cmd.ActionCmd;
import action.cmd.Attack;
import action.cmd.Harvest;
import action.cmd.Move;
import action.cmd.Reproduce;
import action.cmd.Transmit;
import brain.BotBrain;
import brain.BrainCommand;
import brain.BrainInfo;
import brain.Vision;

public class GameManagerTest {
	int aTeamID;
	int otherTeamID;

	Map<Entity, Integer> START_ENERGY;

	World grid;
	GameManager gm;

	BotEntity attackBot;
	BotEntity otherAttackBot;
	BotEntity harvestBot;
	BotEntity msgBot;
	FoodEntity theFood;

	@Before
	public void setUp() throws Exception {
		Settings.load();
		Settings.setMessageLength(3);
		Settings.lock();

		START_ENERGY = new HashMap<Entity, Integer>();
		grid = new World(4, 3);
		gm = new GameManager(grid);

		/**************
		 * TEAM SETUP *
		 **************/

		// add TEAM A
		BotBrain aBrain = new BotBrain("A Team Brain") {

			@Override
			protected BrainCommand brainDecideAction(BrainInfo info)
					throws Exception {
				Vision vis = info.getVision();

				// if enough energy to reproduce, do, down 1 square
				RelPos adjDown = new RelPos(0, -1);
				if (info.getEnergy() >= 75) {
					return new BrainCommand(new Reproduce(adjDown), null);
				}

				// if food to right, eat it
				RelPos adjRight = new RelPos(1, 0);
				if (vis.get(adjRight) == Vision.FOOD) {
					return new BrainCommand(new Harvest(adjRight), null);
				}

				// if enemy to upper right, attack it
				RelPos adjUpRight = new RelPos(1, 1);
				if (vis.get(adjUpRight) == Vision.ENEMY_BOT) {
					return new BrainCommand(new Attack(adjUpRight), null);
				}

				// else send message
				boolean[] msgContents =
					{
							true, true, false
					};
				Message toSend = new Message(msgContents);
				return new BrainCommand(new Transmit(toSend), null);
			}
		};

		aTeamID = gm.addTeam(aBrain, "A Team");

		// add TEAM OTHER
		BotBrain otherBrain = new BotBrain("Other Team Brain") {

			@Override
			protected BrainCommand brainDecideAction(BrainInfo info)
					throws Exception {
				Vision vis = info.getVision();

				// if enemy at down-left, attack
				RelPos adjDownLeft = new RelPos(-1, -1);
				if (vis.get(adjDownLeft) == Vision.ENEMY_BOT) {
					return new BrainCommand(new Attack(adjDownLeft), null);
				}

				// else move down-left
				return new BrainCommand(new Move(adjDownLeft), null);
			}
		};
		otherTeamID = gm.addTeam(otherBrain, "Other Team");

		/**************
		 * GRID SETUP *
		 **************/

		final int START_ENERGY_HARVESTING_BOT = 74;
		final int START_ENERGY_ATTACK_OTHER_BOT = 50;
		final int START_ENERGY_MSG_BOT = 50;
		final int START_ENERGY_ATTACK_BOT = 10;
		final int START_ENERGY_FOOD = 100;

		harvestBot = Entity.getNewBot(START_ENERGY_HARVESTING_BOT, aTeamID);
		otherAttackBot = Entity.getNewBot(START_ENERGY_ATTACK_OTHER_BOT,
				otherTeamID);
		msgBot = Entity.getNewBot(START_ENERGY_MSG_BOT, aTeamID);
		attackBot = Entity.getNewBot(START_ENERGY_ATTACK_BOT, aTeamID);
		theFood = Entity.getNewFood(START_ENERGY_FOOD);

		START_ENERGY.put(harvestBot, START_ENERGY_HARVESTING_BOT);
		START_ENERGY.put(attackBot, START_ENERGY_ATTACK_BOT);
		START_ENERGY.put(otherAttackBot, START_ENERGY_ATTACK_OTHER_BOT);
		START_ENERGY.put(msgBot, START_ENERGY_MSG_BOT);
		START_ENERGY.put(theFood, START_ENERGY_FOOD);

		/*   Stuff in grid (A is bot on A Team, O is bot on Other Team)
		 *   0   1   2   3    x
		 * +---+---+---+---+
		 * | A | f |   |   |
		 * +---+---+---+---+
		 * |   |   |   | O |
		 * +---+---+---+---+
		 * | A |   | A |   |
		 * +---+---+---+---+
		 * 
		 * 
		 */
		// added entities by row (y)
		gm.addBot(harvestBot, new AbsPos(0, 0));
		grid.addNewEntity(new AbsPos(1, 0), theFood);
		grid.addNewEntity(new AbsPos(2, 0), Entity.getNewWall());

		gm.addBot(otherAttackBot, new AbsPos(3, 1));

		gm.addBot(msgBot, new AbsPos(0, 2));
		gm.addBot(attackBot, new AbsPos(2, 2));

		/*-******************************************************
		 * TURN 1 - A Team
		 * ------------------------------------
		 * EXPECTED ACTIONS
		 * 
		 * Bot 0,0 HARVEST food to right
		 * Bot 0,2 TRANSMIT {true, true, false}
		 * Bot 2,2 ATTACK enemy to up-right
		 * 
		 * OUTCOME
		 * --------------------------
		 * 
		 *  Bot 0,0 energy is decreased by HARVEST cost,
		 *  				  increased by HARVEST_ENERGY
		 *  Bot 0,2 energy is decreased by TRANSMIT cost
		 *  Bot 2,2 energy is decreased by ATTACK cost
		 *  
		 *  Food 1,0 energy is decreased by HARVEST_ENERGY
		 *  Enemy Bot 3,1 energy is decreased by ATTACK damage
		 *  
		 ********************************************************
		 *  TURN 2 - Other Team
		 *  ----------------------------
		 *  EXPECTED ACTIONS 
		 *  
		 *  Bot 3,1 ATTACK enemy to down-left
		 *  
		 *  OUTCOME
		 *  -----------------------------
		 *  Bot 3,1 energy is decreased by ATTACK cost
		 *  
		 *  Enemy Bot 2,2 energy is decreased by ATTACK damage
		 *******************************************************
		 *  TURN 3 - A TEAM
		 *  --------------------------
		 *  EXPECTED ACTIONS
		 *  
		 *  Bot 0,0 REPRODUCE down
		 *  Transmitted MESSAGE recieved {true, true, false}
		 *
		 *******************************************************/
	}

	@Test
	public void testAddTeam() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetWinnerName() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetNextTeamID() {
		fail("Not yet implemented");
	}

	// TODO-TEST somewhere bot memory saving and loading

	/***********
	 * ACTIONS *
	 ***********/

	// TODO-TEST order in which actions are executed
	// TODO-TEST invalid actions

	@Test
	public void testDoValidWait() {
		fail("Not yet implemented");
	}

	@Test
	public void testDoValidMove() {
		fail("Not yet implemented");
	}

	@Test
	public void testDoValidReproduce() {
		fail("Not yet implemented");
	}

	@Test
	public void testDoValidTransmit() {
		fail("Not yet implemented");
	}

/*	@Test
	public void testDoValidHarvest() {
		gm.doTurn(aTeamID);

		// EXPECTED ACTION
		Iterable<Harvest> harvestActions = actions.getValidActions(Harvest.class);
		assertTrue(Util.lengthOfIterable(harvestActions) == 1);
		// expected bot did action
		assertTrue(Util.actionIsInIterable(harvestActions, harvestBot));

		int currentEnergy = harvestBot.getEnergy();
		
		int gainedEnergy = Settings.getHarvestEnergy();
		int expectedEnergy = getExpectedEnergy(harvestBot, Harvest.class) + gainedEnergy;

		// System.out.println("current: " + currentEnergy + " start: " +
		// startEnergy + " cost: " + actionCost + " gained: " + gainedEnergy +
		// " expected: " + expectedEnergy);

		// EXPECTED RESULT
		assertTrue(currentEnergy == expectedEnergy);

		// target was affected
		Harvest act = Util.getActionFromIterable(harvestActions, harvestBot);
		AbsPos absActTarget = AbsPos.offset(grid.getBotPosition(harvestBot.getID()),
				act.getRelTarget());

		FoodEntity theFood = (FoodEntity) grid.get(absActTarget);
		assertTrue(theFood.getEnergy() == (START_ENERGY.get(theFood) - Settings
				.getHarvestEnergy()));
	}*/
	
	@Test
	public void testDoValidHarvest() {
		/*-******************************************************
		 * TURN 1 - A Team
		 * ------------------------------------
		 * EXPECTED ACTIONS
		 * -------------------------- 
		 * Bot 0,0 HARVEST food to right
		 * 
		 * OUTCOME
		 * --------------------------
		 *  Bot 0,0 energy is decreased by HARVEST cost,
		 *  				  increased by HARVEST_ENERGY
		 *  Food 1,0 energy is decreased by HARVEST_ENERGY
		 */
		
		gm.doTurn(aTeamID);
		assertTrue(harvestBot.getAction() instanceof Harvest);
		assertTrue(harvestBot.getTurnsActionExecuted() == 1);
		
		Harvest act = (Harvest) harvestBot.getAction(); 
		assertTrue(act.getRelTarget().equals(RelPos.RIGHT));
		
		int currentEnergy = harvestBot.getEnergy();
		
		int gainedEnergy = Settings.getHarvestEnergy();
		int expectedEnergy = getExpectedEnergy(harvestBot, Harvest.class) + gainedEnergy;
		
		// EXPECTED RESULT
		assertTrue(currentEnergy == expectedEnergy);
		
		// target was affected
		assertTrue(theFood.getEnergy() == (START_ENERGY.get(theFood) - Settings
				.getHarvestEnergy()));
	}

	/*@Test
	public void testDoValidAttack() {
		TurnActions actions = gm.doTurn(aTeamID);

		// EXPECTED ACTION
		Iterable<Attack> attackActions = actions.getValidActions(Attack.class);
		assertTrue(Util.lengthOfIterable(attackActions) == 1);
		// expected bot did action
		assertTrue(Util.actionIsInIterable(attackActions, attackBot));

		// EXPECTED RESULT
		assertTrue(attackBot.getEnergy() == (START_ENERGY.get(attackBot) - Settings
				.getActionCost(Attack.class)));

		// target was affected
		Attack act = Util.getActionFromIterable(attackActions, attackBot);
		AbsPos absActTarget = AbsPos.offset(grid.getBotPosition(attackBot.getID()),
				act.getRelTarget());

		BotEntity victim = (BotEntity) grid.get(absActTarget);
		assertTrue(victim.getEnergy() == (START_ENERGY.get(victim) - Settings
				.getAttackDamage()));
	}*/
	
	private int getExpectedEnergy(BotEntity bot, Class<? extends ActionCmd> actionType) {
		int startEnergy = START_ENERGY.get(bot);
		int actionCost = Settings.getActionCost(actionType);
		
		int expectedEnergy = startEnergy - actionCost;
		
		return expectedEnergy;
	}

}
