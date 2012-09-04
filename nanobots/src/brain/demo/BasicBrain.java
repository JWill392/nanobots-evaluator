package brain.demo;

import game.Settings;

import java.util.List;

import teampg.grid2d.point.AbsPos;
import teampg.grid2d.point.Pos2D;
import teampg.grid2d.point.RelPos;
import teampg.util.Util;
import action.ActionCmd;
import action.AttackCmd;
import action.HarvestCmd;
import action.MoveCmd;
import brain.BotBrain;
import brain.BrainUtil;
import brain.Vision;

public class BasicBrain extends BotBrain {

	@Override
	protected ActionCmd brainDecideAction() throws Exception {

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
		if (energy < Settings.getBotMaxEnergy() && !foodPos.isEmpty()) {
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