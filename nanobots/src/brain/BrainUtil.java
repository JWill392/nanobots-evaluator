package brain;

import teampg.grid2d.point.AbsPos;
import teampg.grid2d.point.Pos2D;
import teampg.grid2d.point.RelPos;

public class BrainUtil {
	public static AbsPos getMoveTowards(AbsPos from, RelPos to) {
		return Pos2D.offset(from, to.unitVector());
	}
}