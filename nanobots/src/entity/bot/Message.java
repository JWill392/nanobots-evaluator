package entity.bot;

import static com.google.common.base.Preconditions.checkArgument;
import teampg.datatypes.BitStr;
import teampg.datatypes.ReadBitStr;
import game.Settings;

public class Message implements ReadBitStr {
	private final ReadBitStr contents;

	public Message(ReadBitStr contents) {
		checkArgument(contents.size() == Settings.getMessageLength());

		this.contents = (ReadBitStr) contents.clone();
	}

	public Message(int messageData) {
		checkArgument(BitStr.countBits(messageData) <= Settings.getMessageLength());

		contents = new BitStr(Settings.getMessageLength(), messageData);
	}

	@Override
	public boolean getBit(int index) {
		return contents.getBit(index);
	}

	@Override
	public int getBits(int startIndex, int bitCount) {
		return contents.getBits(startIndex, bitCount);
	}

	@Override
	public boolean equals(Object what) {
		Message other = (Message) what;

		return contents.equals(other.contents);
	}

	@Override
	public int size() {
		return contents.size();
	}

	@Override
	public int getAll() {
		return contents.getAll();
	}

	@Override
	public Object clone() {
		return new Message(contents);
	}
}
