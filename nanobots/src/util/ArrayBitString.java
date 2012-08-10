package util;

import java.util.Arrays;

public class ArrayBitString {
	private final boolean[] bits;

	public ArrayBitString(int numberOfBits) {
		bits = new boolean[numberOfBits];
	}

	public ArrayBitString(boolean[] inData) {
		bits = inData.clone();
	}

	public static ArrayBitString newInstance(ArrayBitString aBitString) {
		int length = aBitString.getLength();
		ArrayBitString newBS = new ArrayBitString(length);

		boolean[] data = aBitString.getBits(0, length);
		newBS.storeBits(0, data);

		return newBS;
	}

	public void storeBit(int index, boolean inBit) {
		bits[index] = Boolean.valueOf(inBit);
	}

	public void storeBits(int startBit, boolean[] inBits) {
		for (int i = 0; i < inBits.length; i++) {
			int insertBitIndex = startBit + i;
			bits[insertBitIndex] = Boolean.valueOf(inBits[i]);
		}
	}

	public boolean getBit(int index) {
		boolean valueAtIndex = Boolean.valueOf(bits[index]);
		return valueAtIndex;
	}

	public boolean[] getBits(int startBit, int bitCount) {
		boolean[] result = new boolean[bitCount];

		for (int i = 0; i < bitCount; i++) {
			boolean valueAtIndex = Boolean.valueOf(bits[i]);
			result[i] = valueAtIndex;
		}

		return result;
	}

	public int getLength() {
		int length = bits.length;
		return length;
	}

	@Override
	public boolean equals(Object what) {
		ArrayBitString other = (ArrayBitString) what;

		return Arrays.equals(bits, other.bits);
	}
}
