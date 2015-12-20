package replay;

import java.awt.Dimension;
import replay.ReplayProto.Replay;
import replay.ReplayProto.Replay.TurnInfo;
import teampg.grid2d.point.AbsPos;

public class Util {
	public static ReplayProto.Point of(AbsPos pos) {
		return ReplayProto.Point.newBuilder()
			.setX(pos.x)
			.setY(pos.y)
			.build();
	}
	public static ReplayProto.Dimension of(Dimension dim) {
		return ReplayProto.Dimension.newBuilder()
				.setWidth(dim.width)
				.setHeight(dim.height)
				.build();
	}
	public static AbsPos of(ReplayProto.Point pos) {
		return AbsPos.of(
				pos.getX(),
				pos.getY());
	}

	public static Dimension of(replay.ReplayProto.Dimension size) {
		return new Dimension(size.getWidth(), size.getHeight());
	}

	public static Replay.Entity getEntFromTurn(TurnInfo turn, int eid) {
		for (Replay.Entity ent : turn.getEntsList()) {
			if (ent.getEid() == eid) {
				return ent;
			}
		}

		return null;
	}
}
