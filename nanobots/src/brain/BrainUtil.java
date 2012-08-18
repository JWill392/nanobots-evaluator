package brain;


import teampg.grid2d.point.AbsPos;
import teampg.grid2d.point.Pos2D;
import teampg.grid2d.point.RelPos;

public class BrainUtil {
	public static AbsPos getMoveTowards(AbsPos from, AbsPos to) {
		return Pos2D.offset(from, RelPos.offsetVector(from, to).unitVector());
	}
}
