package entity.bot;

import util.ArrayBitString;
import util.BitStringWriter;
import game.Settings;

public class Memory implements BitStringWriter {
	private ArrayBitString contents;

	public static Memory newInstance(Memory toCopy) {
		Memory newBM = new Memory();

		ArrayBitString copiedBitString = ArrayBitString.newInstance(toCopy.contents);

		newBM.contents = copiedBitString;

		return newBM;
	}

	public Memory() {
		contents = new ArrayBitString(Settings.getMemorySize());
	}

	public void load(Memory toCopy) {
		contents = ArrayBitString.newInstance(toCopy.contents);
	}

	@Override
	public void storeBit(int index, boolean inBit) {
		contents.storeBit(index, inBit);
	}

	@Override
	public void storeBits(int startIndex, boolean[] inBits) {
		contents.storeBits(startIndex, inBits);
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
		Memory other = (Memory) what;

		return contents.equals(other.contents);
	}

}
