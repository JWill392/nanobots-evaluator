package entity.bot;

import util.ArrayBitString;
import util.BitStringOnlyReader;
import game.Settings;

public class Message implements BitStringOnlyReader {
	private final ArrayBitString contents;

	public Message(boolean[] msgContents) {
		if (msgContents.length != Settings.getMessageLength()) {
			// TODO: throw a GameRule -> StructureIllegalArgumentException
			assert (false);
		}

		contents = new ArrayBitString(msgContents);
	}

	public static Message newInstance(Message toCopy) {
		return toCopy;  // read only, so no need to make new instance
	}

	@Override
	public boolean getBit(int index) {
		return contents.getBit(index);
	}

	@Override
	public boolean[] getBits(int startIndex, int bitCount) {
		return contents.getBits(startIndex, bitCount);
	}

	@Override
	public boolean equals(Object what) {
		Message other = (Message) what;

		return contents.equals(other.contents);
	}
}
