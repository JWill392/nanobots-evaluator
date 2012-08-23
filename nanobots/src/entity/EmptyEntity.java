package entity;

import replay.ReplayProto.Replay.Entity.Type;

public class EmptyEntity extends Entity {
	public EmptyEntity() {
		data = null;
	}

	@Override
	public String toString() {
		return "EmptyEntity []";
	}

	@Override
	public Type getType() {
		return null;
	}
}
