package entity.bot;

import teampg.datatypes.BitStr;
import teampg.datatypes.BitStrInterface;
import game.Settings;

/**
 * A BitStr with length set to Settings.getMemorySize()
 * @author JWill
 */
public class Memory implements BitStrInterface {
	private final BitStr contents;

	public Memory() {
		contents = new BitStr(Settings.getMemorySize());
	}

	private Memory(BitStr contents) {
		this.contents = (BitStr) contents.clone();
	}

	@Override
	public boolean getBit(int index) {
		return contents.getBit(index);
	}

	@Override
	public int getBits(int startBit, int bitCount) {
		return contents.getBits(startBit, bitCount);
	}

	@Override
	public int size() {
		return contents.size();
	}

	@Override
	public void setBit(int index, boolean inBit) {
		contents.setBit(index, inBit);
	}

	@Override
	public void setBits(int startBit, int bitCount, int value) {
		contents.setBits(startBit, bitCount, value);
	}

	@Override
	public void fill(int value) {
		contents.fill(value);
	}

	@Override
	public int getAll() {
		return contents.getAll();
	}

	@Override
	public Object clone() {
		return new Memory(contents);
	}

	@Override
	public boolean equals(Object what) {
		Memory other = (Memory) what;
		return contents.equals(other.contents);
	}
}
