package action.cmd;

import entity.bot.Message;

public class Transmit extends ActionCmd {
	private final Message msg;

	public Transmit(Message toSend) {
		super();
		msg = toSend;
	}

	public Message getMessage() {
		return Message.newInstance(msg);
	}
}
