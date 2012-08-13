package action;

import teampg.grid2d.point.AbsPos;

public abstract class TargettedAction extends RunningAction {
	public final AbsPos target;

	public TargettedAction(AbsPos target) {
		super();
		this.target = target;
	}
}
