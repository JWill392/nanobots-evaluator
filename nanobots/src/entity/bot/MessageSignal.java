package entity.bot;

import teampg.grid2d.point.AbsPos;

public class MessageSignal {
	public final Message contents;
	public final AbsPos origin;

	public MessageSignal(Message data, AbsPos origin) {
		contents = data;
		this.origin = origin;
	}
}
