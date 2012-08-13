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
			fail();
		} catch (IndexOutOfBoundsException e) {
		}
	}

}
