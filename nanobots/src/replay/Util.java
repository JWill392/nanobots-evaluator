package replay;

import java.awt.Dimension;

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
}
