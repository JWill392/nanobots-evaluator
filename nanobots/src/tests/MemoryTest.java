package tests;

import static org.junit.Assert.*;

import entity.bot.Memory;
import game.Settings;

import org.junit.Before;
import org.junit.Test;


public class MemoryTest {
	Memory aMem;

	@Before
	public void setUp() throws Exception {
		Settings.load();
		Settings.setMemorySize(3);
		Settings.lock();
		aMem = new Memory();
	}

	@Test
	public void testConstructed() {
		assertTrue(aMem.getBit(0) == false);
		assertTrue(aMem.getBit(3 - 1) == false);

		try {
			// memory is correct length?
			aMem.getBit(3);
			assertTrue(false);
		} catch (IndexOutOfBoundsException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testNewInstance() {
		aMem.storeBit(1, true);
		Memory aCopy = Memory.newInstance(aMem);

		assertTrue(aMem.equals(aCopy));
		assertTrue(!(aMem == aCopy));

		aMem.storeBit(2, true);

		assertTrue(!aMem.equals(aCopy));
		assertTrue(!(aMem == aCopy));
	}

	@Test
	public void testStoreBitOnce() {
		assertTrue(aMem.getBit(0) == false);
		aMem.storeBit(0, true);
		assertTrue(aMem.getBit(0) == true);
	}

	@Test
	public void testBitString() {
		fail("TODO test remaining BitString methods");
	}

	@Test
	public void testToArray() {
		fail("todo test");
	}

	// TODO-TEST test remaining BitString methods

	@Test
	public void testTooBigInputException() {
		fail("Not yet implemented");
	}

	@Test
	public void testEquals() {
		fail("Not yet implemented");
	}

}
