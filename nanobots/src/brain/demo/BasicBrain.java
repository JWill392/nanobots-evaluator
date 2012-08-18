package brain.demo;

import java.util.List;

import teampg.grid2d.point.AbsPos;
import teampg.grid2d.point.Pos2D;
import teampg.grid2d.point.RelPos;
import teampg.util.Util;
import action.ActionCmd;
import action.HarvestCmd;
import action.MoveCmd;
import brain.BotBrain;
import brain.BrainUtil;
import brain.Vision;

public class BasicBrain extends BotBrain {
	@Override
	protected ActionCmd brainDecideAction() throws Exception {

		List<AbsPos> foodPos = vision.indexOf(Vision.FOOD);
		if (!foodPos.isEmpty()) {
			AbsPos bestFoodPos = foodPos.get(0);
			RelPos relFoodPos = RelPos.offsetVector(position, bestFoodPos);

			if (relFoodPos.squareMagnitude() == 1) {
				return new HarvestCmd(bestFoodPos);
			}

			AbsPos moveTowardsFood =
					BrainUtil.getMoveTowards(position, bestFoodPos);

			return new MoveCmd(moveTowardsFood);
		}

		return new MoveCmd(Pos2D.offset(position, Util.choice(RelPos.UP, RelPos.RIGHT, RelPos.DOWN, RelPos.LEFT)));
	}
}