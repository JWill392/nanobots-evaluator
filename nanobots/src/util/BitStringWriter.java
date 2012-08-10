package util;

public interface BitStringWriter extends BitStringOnlyReader {
	public void storeBit(int index, boolean inBit);

	public void storeBits(int startBit, boolean[] inBits);
}
