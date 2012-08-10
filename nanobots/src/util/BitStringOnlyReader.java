package util;

public interface BitStringOnlyReader {
	public boolean getBit(int index);

	public boolean[] getBits(int startBit, int bitCount);

	@Override
	public boolean equals(Object what);
}
