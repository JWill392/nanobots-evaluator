package entity.bot;

import replay.ReplayProto.Replay;
import teampg.grid2d.point.AbsPos;

public class MessageSignal {
	public final Message contents;
	public final AbsPos origin;
	private final Replay.Entity.ReceivedMessage data;

	public MessageSignal(Message msgBody, AbsPos origin) {
		contents = msgBody;
		this.origin = origin;
		data = Replay.Entity.ReceivedMessage.newBuilder()
				.setBody(contents.getAll())
				.setOrigin(replay.Util.of(origin))
				.build();
	}

	public Replay.Entity.ReceivedMessage getData() {
		return data;
	}
}
