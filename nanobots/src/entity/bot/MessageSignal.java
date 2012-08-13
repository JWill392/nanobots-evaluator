package entity.bot;

import game.Settings;

public class MessageSignal {
	private final Message contents;
	private final int signalStrength;

	public MessageSignal(Message inData, int inSignalStrength) {
		assert (inSignalStrength <= Settings.getMessageRange());

		contents = inData;
		signalStrength = inSignalStrength;
	}

	public MessageSignal(int messageData, int signalStrength) {
		this(new Message(messageData), signalStrength);
	}

	public Message getMessage() {
		return contents;
	}

	public int getSignalStrength() {
		return signalStrength;
	}

	@Override
	public boolean equals(Object what) {
		MessageSignal other = (MessageSignal) what;

		if (signalStrength != other.signalStrength) {
			return false;
		}

		return contents.equals(other.contents);
	}
}
