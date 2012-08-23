package action;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import teampg.grid2d.point.AbsPos;
import teampg.grid2d.point.Pos2D;
import teampg.grid2d.point.RelPos;
import brain.BotBrain;
import brain.Vision;

import com.google.common.collect.ImmutableList;

import entity.BotEntity;
import entity.FoodEntity;
import game.Game;
import game.Settings;
import game.Team;
import game.world.GameMap;
import game.world.World;

public class _HarvestCmdTest {
	private static final int HARVEST_AMOUNT = 100;
	private static final int HARVEST_COST = 2;
	private static final int NEWBORN = 10;
	private static final int START_FOOD_ENERGY = 1000;
	Team testTeam;
	World world;
	Game game;

	@Before
	public void setUp() throws Exception {
		Settings.load();
		Settings.setNewbornEnergy(NEWBORN);
		Settings.setActionCost(HarvestCmd.class, HARVEST_COST);
		Settings.setActionRange(HarvestCmd.class, 1);
		Settings.setHarvestEnergy(HARVEST_AMOUNT);
		Settings.setFoodEnergy(START_FOOD_ENERGY);
		Settings.setBotMaxEnergy(100000);
		Settings.setVisionRadius(100);
		Settings.lock();
	}

	private final void setUpBasicTest(String mapString) {
		testTeam = new Team(new BotBrain() {

			//If near food, eat.  Else eat whatever is to left (illegal).
			@Override
			protected ActionCmd brainDecideAction() throws Exception {
				List<AbsPos> allFoodPositions = vision.getPositions(Vision.FOOD);
				if (!allFoodPositions.isEmpty()) {
					AbsPos closestFoodPos = allFoodPositions.get(0);
					return new HarvestCmd(closestFoodPos);
				}

				return new HarvestCmd(Pos2D.offset(position, RelPos.LEFT));
			}
		}, "eatTestTeam");
		ImmutableList<Team> teams = ImmutableList.of(testTeam);
		game = new Game(new GameMap("PLACEHOLDER", mapString), teams);
		world = game.getWorld();
	}

	@Test
	public final void testBasicValidTarget() {
		setUpBasicTest("0F");

		BotEntity eaterBot = (BotEntity) world.get(AbsPos.of(0, 0));
		FoodEntity food = (FoodEntity) world.get(AbsPos.of(1, 0));

		game.runNextTurn();
		assertEquals(NEWBORN - HARVEST_COST + HARVEST_AMOUNT, eaterBot.getEnergy());
		assertEquals(START_FOOD_ENERGY - HARVEST_AMOUNT, food.getEnergy());
	}

	@Test
	public final void testBasicIllegalTarget() {
		setUpBasicTest(".0");

		BotEntity reproducerBot = (BotEntity) world.get(AbsPos.of(1, 0));

		game.runNextTurn();
		assertEquals(NEWBORN, reproducerBot.getEnergy());
	}

	@Test
	public final void testGroupHarvest() {
		setUpBasicTest("0F0");

		BotEntity eaterBotA = (BotEntity) world.get(AbsPos.of(0, 0));
		BotEntity eaterBotB = (BotEntity) world.get(AbsPos.of(2, 0));
		FoodEntity food = (FoodEntity) world.get(AbsPos.of(1, 0));

		game.runNextTurn();
		assertEquals(NEWBORN - HARVEST_COST + (HARVEST_AMOUNT/2), eaterBotA.getEnergy());
		assertEquals(NEWBORN - HARVEST_COST + (HARVEST_AMOUNT/2), eaterBotB.getEnergy());
		assertEquals(START_FOOD_ENERGY - HARVEST_AMOUNT, food.getEnergy());
	}


}