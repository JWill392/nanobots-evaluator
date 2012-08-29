package brain.demo;

import entity.bot.Memory;
import entity.bot.Message;
import game.Settings;

import java.util.List;

import replay.ReplayProto.Replay.Entity.BotState;
import teampg.grid2d.point.AbsPos;
import teampg.grid2d.point.Pos2D;
import teampg.grid2d.point.RelPos;
import teampg.util.Util;
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

public class BasicBrain extends BotBrain {
	private static final Message GESTATING_SIGNAL = new Message(1);

	@Override
	protected ActionCmd brainDecideAction() throws Exception {
		/*
		 * TODO priority system, rather than just ordering?  Yeah.  Compute priorities first, then decide which to do.
		 */

		// Pregnant?
		if (state == BotState.GESTATING) {
			// ready to birth?  Do.
			if (elapsedGestation >= Settings.getGestationDuration()) {
				List<AbsPos> emptyCells = vision.getPositions(Vision.EMPTY);
				if (!emptyCells.isEmpty()) {
					return new BirthCmd(emptyCells.get(0), new Memory());
				}
			}
			// Ask for feeding
			return new BroadcastCmd(GESTATING_SIGNAL);
		}

		// Hear a gestating bot asking for help?
		int pregMsg = msgs.indexOf(GESTATING_SIGNAL);
		if (pregMsg != -1) {
			AbsPos pregPos = msgs.get(pregMsg).origin;
			if (Pos2D.squareDistance(pregPos, position) <= Settings.getActionRange(TransferCmd.class)) {
				return new TransferCmd(pregPos, Settings.getGestationUpkeep());
			}

			AbsPos moveTowardsPreg = BrainUtil.getMoveTowards(position, pregPos);
			return new MoveCmd(moveTowardsPreg);
		}

		// Reproduce
		if (energy >= Settings.getActionCost(ConceiveCmd.class)) {
			List<AbsPos> enemyCells = vision.getPositions(Vision.ENEMY_BOT);
			List<AbsPos> foodCells = vision.getPositions(Vision.FOOD);
			List<AbsPos> allyCells = vision.getPositions(Vision.FRIENDLY_BOT);

			if (enemyCells.isEmpty() && !foodCells.isEmpty() && !allyCells.isEmpty()) {
				return new ConceiveCmd();
			}
		}

		// Kill
		List<AbsPos> enemyPos = vision.getPositions(Vision.ENEMY_BOT);
		if (!enemyPos.isEmpty()) {
			AbsPos bestEnemyPos = enemyPos.get(0);
			RelPos relEnemyPos = RelPos.offsetVector(position, bestEnemyPos);

			if (relEnemyPos.squareMagnitude() == 1) {
				return new AttackCmd(bestEnemyPos);
			}

			AbsPos moveTowardsEnemy =
					BrainUtil.getMoveTowards(position, bestEnemyPos);
			if (vision.get(moveTowardsEnemy) == Vision.EMPTY) {
				return new MoveCmd(moveTowardsEnemy);
			}

		}


		// Eat
		List<AbsPos> foodPos = vision.getPositions(Vision.FOOD);
		if (!foodPos.isEmpty()) {
			AbsPos bestFoodPos = foodPos.get(0);
			RelPos relFoodPos = RelPos.offsetVector(position, bestFoodPos);

			if (relFoodPos.squareMagnitude() == 1) {
				return new HarvestCmd(bestFoodPos);
			}

			AbsPos moveTowardsFood =
					BrainUtil.getMoveTowards(position, bestFoodPos);

			if (vision.get(moveTowardsFood) == Vision.EMPTY) {
				return new MoveCmd(moveTowardsFood);
			}
		}

		// Random walk
		return new MoveCmd(Pos2D.offset(position, Util.choice(RelPos.UP, RelPos.RIGHT, RelPos.DOWN, RelPos.LEFT)));
	}
}