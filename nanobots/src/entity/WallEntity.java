package entity;

import replay.ReplayProto.Replay;
import replay.ReplayProto.Replay.Entity.Type;

public class WallEntity extends Entity {
	private static final Replay.Entity.Type TYPE = Type.WALL;
	@Override
	public Type getType() {
		return TYPE;
	}

	public WallEntity() {
	}
}
