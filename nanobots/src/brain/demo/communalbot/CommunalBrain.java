package brain.demo.communalbot;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Collections2;
import replay.ReplayProto.Replay.Entity.BotState;
import replay.ReplayProto.Replay.Entity.ReceivedMessage;
import teampg.grid2d.point.AbsPos;
import teampg.grid2d.point.Pos2D;
import teampg.grid2d.point.RelPos;
import teampg.util.Util;

import entity.bot.Memory;
import entity.bot.Message;
import game.Settings;
import action.ActionCmd;
import action.AttackCmd;
import action.BirthCmd;
import action.BroadcastCmd;
import action.ConceiveCmd;
import action.HarvestCmd;
import action.MoveCmd;
import action.TransferCmd;
import brain.BotBrain;
import brain.BrainUtil;
import brain.Vision;
import brain.demo.communalbot.memory.BreederMemory;
import brain.demo.communalbot.memory.CommunalMemory;
import brain.demo.communalbot.memory.FeederMemory;
import brain.demo.communalbot.memory.FighterMemory;
import brain.demo.communalbot.memory.UndecidedMemory;
import brain.demo.communalbot.memory.CommunalMemory.BotRole;

public class CommunalBrain extends BotBrain {
	private static final Message GESTATING_SIGNAL = new Message(0b1);

	@Override
	protected ActionCmd brainDecideAction() throws Exception {
		/*
		 * TODO priority system, rather than just ordering?  Yeah.  Compute priorities first, then decide which to do.
		 */
		CommunalMemory cMem;
		ActionCmd ret;

		switch (CommunalMemory.getRole(mem)) {
		case UNDECIDED:
			cMem = new UndecidedMemory(mem);
			ret = doUndecided((UndecidedMemory) cMem);
			break;
		case BREEDER:
			cMem = new BreederMemory(mem);
			ret = doBreeder((BreederMemory) cMem);
			break;
		case FEEDER:
			cMem = new FeederMemory(mem);
			ret = doFeeder((FeederMemory) cMem);
			break;
		case FIGHTER:
			cMem = new FighterMemory(mem);
			ret = doFighter((FighterMemory) cMem);
			break;
		default:
			throw new IllegalStateException();
		}

		mem = cMem.getData();
		return ret;
	}

	private ActionCmd doUndecided(UndecidedMemory cMem) {

		//TODO broken at the moment, because the initial breeders will always spawn in the same location, meaning they'll each spawn the same type, every birth.
		BotRole choice;
		if (position.hashCode() % 3 == 0) {
			choice = BotRole.FIGHTER;
		} else if (position.hashCode() % 3 == 1) {
			choice = BotRole.FEEDER;
		} else {
			choice = BotRole.BREEDER;
		}

		cMem.decide(choice);

		switch (choice) {
		case BREEDER:
			return doBreeder(new BreederMemory(new Memory()));
		case FEEDER:
			return doFeeder(new FeederMemory(new Memory()));
		case FIGHTER:
			return doFighter(new FighterMemory(new Memory()));
		default:
			throw new IllegalStateException();
		}
		/*
		 * TODO broadcast choice, and change it based on other's choices.
		 */

		//return new BroadcastCmd(new Message(choice.ordinal()));
	}

	private ActionCmd doBreeder(BreederMemory cMem) {
		// Not pregnant?  Get pregnant.
		if (state != BotState.GESTATING &&
				energy > Settings.getBotMaxEnergy()) {
			return new ConceiveCmd();
		}

		// ready to birth?  Do.
		if (state == BotState.GESTATING && elapsedGestation >= Settings.getGestationDuration()) {
			List<AbsPos> emptyCells = vision.getPositions(Vision.EMPTY);
			if (!emptyCells.isEmpty()) {
				return new BirthCmd(emptyCells.get(0), new Memory());
			}
		}

		// Ask for feeding
		return new BroadcastCmd(GESTATING_SIGNAL);
	}

	private ActionCmd doFeeder(FeederMemory cMem) {

		// Eat
		List<AbsPos> foodPos = vision.getPositions(Vision.FOOD);
		if (energy < Settings.getBotMaxEnergy() && !foodPos.isEmpty()) {
			AbsPos bestFoodPos = foodPos.get(0);
			RelPos relFoodPos = RelPos.offsetVector(position, bestFoodPos);

			if (relFoodPos.squareMagnitude() == 1) {
				return new HarvestCmd(bestFoodPos);
			}

			AbsPos moveTowardsFood = BrainUtil.getMoveTowards(position, bestFoodPos);

			if (vision.get(moveTowardsFood) == Vision.EMPTY) {
				return new MoveCmd(moveTowardsFood);
			}
		}

		// Hear a gestating bot asking for help?
		Collection<ReceivedMessage> allHelpMessages =
				Collections2.filter(msgs, BrainUtil.byMessageBodyEquals(GESTATING_SIGNAL.getAll()));

		if (!allHelpMessages.isEmpty()) {
			AbsPos closestPregPos = replay.Util.of(Collections.min(allHelpMessages,
						BrainUtil.byProximity(position)).getOrigin());

			if (energy > Settings.getGestationUpkeep() * 2) {
				if (Pos2D.diagDistance(closestPregPos, position) <= Settings
						.getActionRange(TransferCmd.class)) {
					return new TransferCmd(closestPregPos, energy - 1);
				}

				AbsPos moveTowardsPreg = BrainUtil.getMoveTowards(position, closestPregPos);
				return new MoveCmd(moveTowardsPreg);
			}
		}

		// Random walk
		return new MoveCmd(Pos2D.offset(position,
				Util.choice(RelPos.UP, RelPos.RIGHT, RelPos.DOWN, RelPos.LEFT)));
	}

	private ActionCmd doFighter(FighterMemory cMem) {
		// Kill
		List<AbsPos> enemyPos = vision.getPositions(Vision.ENEMY_BOT);
		if (!enemyPos.isEmpty()) {
			AbsPos bestEnemyPos = enemyPos.get(0);
			RelPos relEnemyPos = RelPos.offsetVector(position, bestEnemyPos);

			if (relEnemyPos.squareMagnitude() == 1) {
				return new AttackCmd(bestEnemyPos);
			}

			AbsPos moveTowardsEnemy = BrainUtil.getMoveTowards(position, bestEnemyPos);
			if (vision.get(moveTowardsEnemy) == Vision.EMPTY) {
				return new MoveCmd(moveTowardsEnemy);
			}

		}

		// Random walk
		return new MoveCmd(Pos2D.offset(position,
				Util.choice(RelPos.UP, RelPos.RIGHT, RelPos.DOWN, RelPos.LEFT)));
	}
}
