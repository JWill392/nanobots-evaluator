package entity.bot;

import game.Settings;

public class MessageSignal {
	private Message contents;
	private int signalStrength;

	public MessageSignal(Message inData, int inSignalStrength) {
		assert (inSignalStrength <= Settings.getMessageRange());

		contents = inData;
		signalStrength = inSignalStrength;
	}

	public static MessageSignal newInstance(MessageSignal toCopy) {
		return toCopy;  // read only, so no need to make new instance
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
