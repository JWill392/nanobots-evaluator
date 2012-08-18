package tests;

import static org.junit.Assert.*;

import entity.bot.Memory;
import game.Settings;

import org.junit.Before;
import org.junit.Test;

import teampg.datatypes.BitStr;

public class _MemoryTest {

	private static final int INVALID = 0b1111;
	private static final int VALID = 0b101;
	private static final int MEM_SIZE = 3;

	@Before
	public void setUp() throws Exception {
		Settings.load();
		Settings.setMemorySize(MEM_SIZE);
		Settings.lock();
	}

	@Test
	public void testConstructors() {
		{
			Memory blankConstructor = new Memory();

			assertEquals(MEM_SIZE, blankConstructor.size());
		}

		{
			Memory bitstrConstructor = new Memory(new BitStr(MEM_SIZE, VALID));
			assertEquals(VALID, bitstrConstructor.getAll());

			try {
				bitstrConstructor = new Memory(new BitStr(MEM_SIZE + 1, INVALID));
				fail("should have throw exception");
			} catch (IllegalArgumentException e) {
			}
		}

		{
			Memory valueConstructor = new Memory(VALID);
			assertEquals(VALID, valueConstructor.getAll());
		}
	}

}
