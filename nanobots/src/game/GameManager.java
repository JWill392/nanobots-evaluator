package game;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import teampg.grid2d.GridInterface.Entry;
import teampg.grid2d.point.AbsPos;
import teampg.grid2d.point.Pos2D;
import teampg.grid2d.point.RelPos;

import entity.BotEntity;
import entity.Entity;
import entity.FoodEntity;
import entity.bot.Memory;
import entity.bot.Message;
import entity.bot.MessageSignal;



import action.ActionCmd;
import action.TargettedAction;
import action.cmd.Attack;
import action.cmd.ContinuePreviousAction;
import action.cmd.Harvest;
import action.cmd.Reproduce;
import action.cmd.Transmit;
import action.cmd.Wait;
import brain.BotBrain;
import brain.BrainCommand;
import brain.BrainInfo;


public class GameManager {
	private final World world;
	private final List<Team> teams;
	private int winCandidates;

	public GameManager(File map) {
		world = World.load(map);
		teams = new ArrayList<>();
	}

	public GameManager(World map) {
		world = map;
		teams = new ArrayList<>();
	}

	public List<BotEntity> getBotsByAction(Team onTeam,
			Class<? extends ActionCmd> type) {
		List<BotEntity> bots = new ArrayList<>();

		for (BotEntity teamBot : world.getTeamBots(onTeam)) {

			if (teamBot.getAction().getClass() == type) {
				bots.add(teamBot);
			}
		}

		return bots;
	}

	private void killBot(BotEntity bot) {
		int botID = bot.getID();

		world.destroy(world.getBotPosition(botID));
	}

	public void addTeam(BotBrain brain, String name) {
		Team theTeam = Team.getNewTeam(brain, name);

		// should never add a given team twice
		assert (!teams.contains(theTeam));

		teams.add(theTeam);
	}

	public boolean winnerExists() {
		if (winCandidates == 0) {
			return true;
		}

		return false;
	}

	public String getWinnerName() {
		for (Team t : teams) {
			if (t.hasLost() == false) {
				return t.getName();
			}
		}

		// should always be a winner
		assert (false);
		return "No Winner";
	}

	// TODO-DESIGN so... if getNextTeamID is only ever called to give input for
	// doTurn, why the heck is it public?
	public Team getNextPlayingTeam() {
		//TODO
		return null;
	}

	/**
	 * Get actions for each bot on a team, validates them, and executes the
	 * valid ones.
	 *
	 * @param teamID
	 *            Team whose turn it is
	 * @return TurnActions instance listing valid and invalid actions.
	 */
	public void doTurn(Team team) {

		/*
		 * TODO-PROBLEM tick() here would tick twice after a team died; we don't
		 * want this to happen. Need to separate tick() and cleanup of dead
		 * bots. Perhaps each action should kill bots.
		 *
		 * TODO-TEST While you're at it, add a test case to detect this
		 * potential bug.
		 */



		// get requested action for each bot
		getActions(t);

		// Validate then execute each catagory of actions in turn
		for (Class<? extends ActionCmd> actionType : Settings
				.getActionExecutionOrder()) {
			validateActions(t, actionType);
			executeActions(t, actionType);
		}

		// TODO build status report for UI
		// must be done before tick, since that clears messages

		// clear message queue
		tick();

		// check if lost
		checkTeamLost(t);
	}

	/**
	 * Removes dead entities. Executes tick() on MapManager and each
	 * DynamicEntity
	 */
	private void tick() {
		world.tick();
	}

	// TODO change name to getBrainCommands
	private void getActions(Team t) {
		// TODO-OPTIMIZE get each bot's action in parallel
		for (BotEntity bot : world.getTeamBots(t)) {
			BrainInfo info = world.getBotInfo(bot.getID());
			BrainCommand brainCommand = t.decideAction(info);

			// if Brain wants to change memory, change
			Memory toSetMemory = brainCommand.getMemory();
			if (toSetMemory != null) {
				bot.setMemory(toSetMemory);
			}

			// if Brain wants to change action, change
			ActionCmd toSetAction = brainCommand.getAction();
			if (toSetAction != null) {
				bot.setAction(toSetAction);
			}
		}
	}

	/**
	 * Finds absolute target of some targetted action
	 *
	 * @param action
	 *            Action to find target of.
	 * @return Point of absolute position of action's target
	 */
	private AbsPos getAbsoluteTarget(BotEntity targettingBot) {
		TargettedAction act = (TargettedAction) targettingBot.getAction();

		RelPos offset = act.getRelTarget();
		AbsPos origin = world.getBotPosition(targettingBot.getID());

		AbsPos target = AbsPos.offset(origin, offset);

		return target;
	}

	/**
	 * A team has lost if it has zero bots at end of turn. If has lost, update
	 * team hasLost flag, and decrement winCandidates.
	 *
	 * @param t
	 *            Team to check win status of
	 * @return True if team has lost.
	 */
	private boolean checkTeamLost(Team t) {
		if (world.getTeamBots(t).isEmpty()) {
			t.setLost();
			winCandidates--;
			return true;
		}

		return false;
	}

	/*********************
	 * ACTION VALIDATION *
	 *********************/
	private void invalidateAction(BotEntity b) {
		b.setAction(new ContinuePreviousAction());
		// TODO add to register of rejected actions, perhaps with reason?
	}

	// TODO-DESIGN associate invalid actions with reasons for invalidity
	private void validateActions(Team t, Class<? extends ActionCmd> actionType) {
		List<BotEntity> actorBots = getBotsByAction(t, actionType);

		// individual common checks
		for (BotEntity actor : actorBots) {

			// Check bots have enough energy
			int requiredEnergy = Settings.getActionCost(actionType);
			int availableEnergy = actor.getEnergy();

			if (availableEnergy < requiredEnergy) {
				invalidateAction(actor);
				continue;
			}

			// Check targetted actions are within range
			if (actionType.equals(TargettedAction.class)) {
				@SuppressWarnings("unchecked")
				Class<? extends TargettedAction> targettedType = (Class<? extends TargettedAction>) actionType;
				TargettedAction targAct = (TargettedAction) actor.getAction();

				RelPos target = targAct.getRelTarget();
				int maxAllowed = Settings.getActionRange(targettedType);

				if (!Pos2D.inSquareRange(target, maxAllowed)) {
					invalidateAction(actor);
					continue;
				}

			}
		}

		// group-specific checks

		// MOVE
		if (actionType.equals(Move.class)) {
			validateMoveActions(actorBots);

			// REPRODUCE
		} else if (actionType.equals(Reproduce.class)) {
			validateReproduceActions(actorBots);

			// HARVEST
		} else if (actionType.equals(Harvest.class)) {
			validateHarvestActions(actorBots);

			// ATTACK
		} else if (actionType.equals(Attack.class)) {
			validateAttackActions(actorBots);

			// TRANSMIT
		} else if (actionType.equals(Transmit.class)) {
			validateTransmitActions(actorBots);

			// WAIT
		} else if (actionType.equals(Wait.class)) {

		} else if (actionType.equals(ContinuePreviousAction.class)) {

		} else {
			assert (false);
		}
	}

	private void validateTransmitActions(List<BotEntity> actorBots) {
	}

	private void validateAttackActions(List<BotEntity> actorBots) {
		for (BotEntity bot : actorBots) {
			// make sure target is a bot
			if (!actionTargetIsEntityType(bot, Entity.BOT)) {
				invalidateAction(bot);
			}
		}
	}

	private void validateHarvestActions(List<BotEntity> actorBots) {
		// TODO validate harvest
		/*
		 * NOTE consider case where 2 bots are harvesting 1 food (that doesn't
		 * have enough energy left to give everyone 1 harvestEnergy). Does
		 * someone get it first? Does it fail? Does everyone get an equal
		 * reduced share?
		 *
		 * Last two would require storing harvest requests.
		 */
	}

	private void validateReproduceActions(List<BotEntity> actorBots) {
		for (BotEntity bot : actorBots) {
			// make sure target is empty
			if (!actionTargetIsEntityType(bot, Entity.EMPTY)) {
				invalidateAction(bot);
			}
		}
	}

	private void validateMoveActions(List<BotEntity> actorBots) {
		// TODO validate move
	}

	/**
	 * Utility method to find if target of an action is a certain Entity type
	 *
	 * @param action
	 *            Targetted action to check
	 * @param entType
	 *            Type of Entity to determine if target is
	 * @return Targetted entity is of the specified type?
	 */
	private boolean actionTargetIsEntityType(BotEntity targetter,
			Class<? extends Entity> entType) {
		AbsPos targPos = getAbsoluteTarget(targetter);

		Entity targettedEnt = world.get(targPos);

		return (targettedEnt.getClass().equals(entType));
	}

	/********************
	 * ACTION EXECUTION *
	 ********************/

	private void executeActions(Team t, Class<? extends ActionCmd> actionType) {
		// TODO-OPTIMIZE no need to check action type for every single action
		List<BotEntity> actorBots = getBotsByAction(t, actionType);

		for (BotEntity actor : actorBots) {
			int botID = actor.getID();
			ActionCmd action = actor.getAction();

			// MOVE
			if (action instanceof Move) {
				AbsPos target = getAbsoluteTarget(actor);

				world.move(botID, target);

				// REPRODUCE
			} else if (action instanceof Reproduce) {
				AbsPos target = getAbsoluteTarget(actor);

				Team actorTeam = actor.getTeam();

				BotEntity newborn = Entity.getNewBot(
						Settings.getNewbornEnergy(), actorTeam);

				world.addNewEntity(target, newborn);

				// HARVEST
			} else if (action instanceof Harvest) {
				AbsPos target = getAbsoluteTarget(actor);

				actor.addEnergy(Settings.getHarvestEnergy());

				// validated harvest actions target food
				FoodEntity targetFood = (FoodEntity) world.get(target);
				targetFood.addEnergy(-Settings.getHarvestEnergy());

				/*
				 * TODO-PROBLEM uh-oh. Can't do this here; have to do it after
				 * executing all validated actions of this type; some of the
				 * validated actions depend on this food entity existing.
				 *
				 * The simple solution is to clean up dead entities after all or
				 * each execute phase, and just let them harvest more energy
				 * from dead food. Still a pain, though.
				 */
				if (targetFood.getEnergy() < 0) {
					world.destroy(target);
				}

				// ATTACK
			} else if (action instanceof Attack) {
				AbsPos target = getAbsoluteTarget(actor);

				// validated harvest actions target bots
				BotEntity targetBot = (BotEntity) world.get(target);
				targetBot.addEnergy(-Settings.getAttackDamage());

				if (targetBot.getEnergy() < 0) {
					killBot(targetBot);
				}

				// TRANSMIT
			} else if (action instanceof Transmit) {
				AbsPos origin = world.getBotPosition(botID);
				Message toSend = ((Transmit) action).getMessage();

				Collection<Entry<BotEntity>> receivers = world.getProxBots(origin,
						Settings.getMessageRange());

				for (Entry<BotEntity> entry : receivers) {
					AbsPos destination = entry.getPosition();
					BotEntity receiver = entry.getContents();

					int dist = (int) Pos2D.diagDistance(origin, destination);
					MessageSignal receivedMsgSgnl = new MessageSignal(toSend,
							dist);

					receiver.addReceivedMessage(receivedMsgSgnl);
				}

				// WAIT
			} else if (action instanceof Wait) {

			} else if (action instanceof ContinuePreviousAction) {

			}

			actor.addEnergy(-Settings.getActionCost(action.getClass()));
		}
	}
}
