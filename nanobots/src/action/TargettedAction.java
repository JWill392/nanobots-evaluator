package action;

import java.util.Iterator;
import entity.BotEntity;
import game.Settings;
import game.world.World;
import replay.ReplayProto.Replay;
import teampg.grid2d.point.AbsPos;
import teampg.grid2d.point.Pos2D;

public abstract class TargettedAction extends RunningAction {
	public TargettedAction(AbsPos target) {
		super();

		data.setTarget(replay.Util.of(target));
	}

	public boolean validRange(AbsPos origin) {
		return (Pos2D.diagDistance(origin, getTarget()) <= Settings.getActionRange(this.getClass()));
	}

	public AbsPos getTarget() {
		return replay.Util.of(data.getTarget());
	}

	/**
	 * Removes out of range actions.  Calls RunningAction.filterBasicInvalid.
	 */
	static void filterBasicInvalid(World world, Iterable<BotEntity> actors) {
		RunningAction.filterBasicInvalid(world, actors);

		for (Iterator<BotEntity> iter = actors.iterator(); iter.hasNext();) {
			BotEntity bot = iter.next();

			TargettedAction action = (TargettedAction) bot.getRunningAction();
			AbsPos actorPos = world.getBotPosition(bot.getID());

			// out of range
			if (!action.validRange(actorPos)) {
				action.fail(Replay.Action.Outcome.ILLEGAL_TARGET);
				iter.remove();
				continue;
			}
		}
	}
}
