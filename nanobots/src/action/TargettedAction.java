package action;

import java.util.Iterator;
import java.util.List;

import entity.BotEntity;
import game.Settings;
import game.world.World;
import teampg.grid2d.point.AbsPos;
import teampg.grid2d.point.Pos2D;

public abstract class TargettedAction extends RunningAction {
	public final AbsPos target;

	public TargettedAction(AbsPos target) {
		super();
		this.target = target;
	}

	public boolean validRange(AbsPos origin) {
		return (Pos2D.diagDistance(origin, target) <= Settings.getActionRange(this.getClass()));
	}

	/**
	 * Removes out of range actions
	 */
	@Override
	public void executeAll(World world, List<BotEntity> actors) {
		super.executeAll(world, actors);

		for (Iterator<BotEntity> iter = actors.iterator(); iter.hasNext();) {
			BotEntity bot = iter.next();

			TargettedAction action = bot.getRunningAction(this.getClass());
			AbsPos actorPos = world.getBotPosition(bot.getID());

			// out of range
			if (!action.validRange(actorPos)) {
				bot.removeRunningAction(action);
				iter.remove();
				continue;
			}
		}
	}
}
