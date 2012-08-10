package tests;

import static org.junit.Assert.*;
import entity.bot.Message;
import entity.bot.MessageSignal;
import game.Settings;

import org.junit.Before;
import org.junit.Test;


public class MessageSignalTest {
	Message aMsg;
	MessageSignal aMsgSgnl;

	@Before
	public void setUp() throws Exception {
		Settings.load();
		Settings.setMessageRange(10);
		Settings.setMessageLength(3);
		Settings.lock();

		boolean[] msgContents =
			{
					true, false, true
			};
		aMsg = new Message(msgContents);

		aMsgSgnl = new MessageSignal(aMsg, 5);
	}

	@Test
	public void testConstruct() {
		assertTrue(aMsgSgnl.getSignalStrength() == 5);
		assertTrue(aMsgSgnl.getMessage().equals(aMsg));
		// messages are read only, so doesn't matter if copied by reference
	}

	@Test
	public void testEquals() {
		boolean[] someMsgData =
			{
					true, false, false
			};
		boolean[] otherMsgData =
			{
					false, true, false
			};

		// diff by strength
		MessageSignal someMsg = new MessageSignal(new Message(someMsgData), 5);
		MessageSignal diffByRange = new MessageSignal(new Message(someMsgData),
				3);
		assertTrue(someMsg.equals(diffByRange) == false);

		// diff by contents
		MessageSignal diffByData = new MessageSignal(new Message(otherMsgData),
				5);
		assertTrue(someMsg.equals(diffByData) == false);

		// same
		MessageSignal someSameMsg = new MessageSignal(new Message(someMsgData),
				5);
		assertTrue(someMsg.equals(someSameMsg));
	}
}
