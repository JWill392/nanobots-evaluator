package action.cmd;

import teampg.grid2d.point.RelPos;

public abstract class TargettedAction extends ActionCmd {
	private final RelPos target;

	public TargettedAction(RelPos relTarget) {
		super();
		target = relTarget;
	}

	public RelPos getRelTarget() {
		return target;
	}

}
