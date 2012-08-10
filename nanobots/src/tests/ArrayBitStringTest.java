package tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import util.ArrayBitString;



public class ArrayBitStringTest {
	ArrayBitString aBS;

	@Before
	public void setUp() throws Exception {
		aBS = new ArrayBitString(3);
	}

	@Test
	public void testConstruct() {
		boolean[] expectedInitialValues =
			{
					false, false, false
			};
		boolean[] initialValues = aBS.getBits(0, 3);

		assertTrue(boolArrayEqual(initialValues, expectedInitialValues));

		boolean[] wrongValues =
			{
					true, false, false
			};
		assertFalse(boolArrayEqual(initialValues, wrongValues));
	}

	@Test
	public void testGetLength() {
		assertTrue(aBS.getLength() == 3);
	}

	@Test
	public void testStoreBitOnce() {
		aBS.storeBit(0, true);
		assertTrue(aBS.getBit(0) == true);

		aBS.storeBit(1, true);
		assertTrue(aBS.getBit(1) == true);

		aBS.storeBit(2, true);
		assertTrue(aBS.getBit(2) == true);
	}

	@Test
	public void testStoreBitMany() {
		aBS.storeBit(0, true);
		aBS.storeBit(0, false);
		assertTrue(aBS.getBit(0) == false);
		aBS.storeBit(0, true);
		assertTrue(aBS.getBit(0) == true);

		aBS.storeBit(1, true);
		aBS.storeBit(1, false);
		assertTrue(aBS.getBit(1) == false);
		aBS.storeBit(1, true);
		assertTrue(aBS.getBit(1) == true);

		aBS.storeBit(2, true);
		aBS.storeBit(2, false);
		assertTrue(aBS.getBit(2) == false);
		aBS.storeBit(2, true);
		assertTrue(aBS.getBit(2) == true);
	}

	@Test
	public void testStoreAllBits() {
		boolean[] startAndEndOn =
			{
					true, false, true
			};
		boolean[] middleOn =
			{
					false, true, false
			};
		boolean[] allOn =
			{
					true, true, true
			};
		boolean[] allOff =
			{
					false, false, false
			};

		aBS.storeBits(0, startAndEndOn);
		boolean[] changedValues = aBS.getBits(0, 3);
		assertTrue(boolArrayEqual(changedValues, startAndEndOn));

		aBS.storeBits(0, middleOn);
		changedValues = aBS.getBits(0, 3);
		assertTrue(boolArrayEqual(changedValues, middleOn));

		aBS.storeBits(0, allOn);
		changedValues = aBS.getBits(0, 3);
		assertTrue(boolArrayEqual(changedValues, allOn));

		aBS.storeBits(0, allOff);
		changedValues = aBS.getBits(0, 3);
		assertTrue(boolArrayEqual(changedValues, allOff));
	}

	@Test
	public void testStoreSomeBits() {
		boolean[] endOn =
			{
					false, true
			};
		boolean[] allOn =
			{
					true, true
			};
		boolean[] allOff =
			{
					false, false
			};

		aBS.storeBits(1, allOn);
		boolean[] expected1 =
			{
					false, true, true
			};
		boolean[] changedValues = aBS.getBits(0, 3);
		assertTrue(boolArrayEqual(changedValues, expected1));

		aBS.storeBits(1, allOff);
		boolean[] expected2 =
			{
					false, false, false
			};
		changedValues = aBS.getBits(0, 3);
		assertTrue(boolArrayEqual(changedValues, expected2));

		aBS.storeBits(0, allOn);
		boolean[] expected3 =
			{
					true, true, false
			};
		changedValues = aBS.getBits(0, 3);
		assertTrue(boolArrayEqual(changedValues, expected3));

		aBS.storeBits(0, endOn);
		boolean[] expected4 =
			{
					false, true, false
			};
		changedValues = aBS.getBits(0, 3);
		assertTrue(boolArrayEqual(changedValues, expected4));
	}

	@Test
	public void testEqualsBitString() {
		ArrayBitString aLongBS = new ArrayBitString(5);
		assertTrue(!aLongBS.equals(aBS));

		ArrayBitString aDifferentValueBS = new ArrayBitString(3);
		aDifferentValueBS.storeBit(1, true);
		assertTrue(!aDifferentValueBS.equals(aBS));

		ArrayBitString aSameAsDiffBS = new ArrayBitString(3);
		aSameAsDiffBS.storeBit(1, true);
		assertTrue(aDifferentValueBS.equals(aSameAsDiffBS));

	}

	@Test
	public void testNewInstance() {
		aBS.storeBit(1, true);
		ArrayBitString newABS = ArrayBitString.newInstance(aBS);

		assertTrue(aBS.equals(newABS));
		assertTrue(!(aBS == newABS));

		aBS.storeBit(2, true);

		assertTrue(!aBS.equals(newABS));
	}

	/* Determines if two boolean arrays are equal, based on values 
	 */
	private boolean boolArrayEqual(boolean[] one, boolean[] two) {
		int oneLength = one.length;
		int twoLength = two.length;

		if (oneLength != twoLength) {
			return false;
		}

		for (int i = 0; i < oneLength; i++) {
			boolean oneValAtPos = one[i];
			boolean twoValAtPos = two[i];

			if (oneValAtPos != twoValAtPos) {
				return false;
			}
		}

		return true;
	}

}
