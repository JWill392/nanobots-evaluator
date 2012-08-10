package tests;

import static org.junit.Assert.*;
import entity.bot.Message;
import game.Settings;

import org.junit.Before;
import org.junit.Test;


public class MessageTest {

	Message aMsg;

	@Before
	public void setUp() throws Exception {
		Settings.load();
		Settings.setMessageLength(3);
		Settings.lock();
		boolean[] aMsgContents =
			{
					true, false, true
			};
		aMsg = new Message(aMsgContents);
	}

	@Test
	public void testConstructed() {
		assertTrue(aMsg.getBit(0) == true);
		assertTrue(aMsg.getBit(1) == false);
		assertTrue(aMsg.getBit(2) == true);

		try {
			// memory is correct length?
			aMsg.getBit(3);
			assertTrue(false);
		} catch (IndexOutOfBoundsException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testTooBigInputException() {
		/*
		 * Trying to create a message with a longer array than size of message,
		 * should throw a game rules exception
		 */
		fail("Not yet implemented");
	}
}
