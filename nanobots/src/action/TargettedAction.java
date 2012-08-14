package action;

import game.Settings;
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
}
