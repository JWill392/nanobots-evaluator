package tests;

import static org.junit.Assert.*;

import java.util.Arrays;

import entity.BotEntity;
import entity.Entity;
import entity.bot.*;
import game.Settings;


import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;


public class BotEntityTest {
	BotEntity aBot;

	@Before
	public void setUp() throws Exception {
		Settings.load();
		Settings.setMemorySize(3);
		Settings.setMessageLength(3);
		Settings.setBotMaxEnergy(10);
		Settings.lock();

		aBot = BotEntity.getNewBotEntity(10, 1);
	}

	@Test
	public void testNewInstance() {
		assertTrue(aBot.getEnergy() == 10);
		assertTrue(aBot.getTeamID() == 1);

		boolean[] memoryContents = aBot.getMemory().getBits(0, 3);
		boolean[] expectedMemory =
			{
					false, false, false
			};
		assertTrue(Arrays.equals(memoryContents, expectedMemory));

		ImmutableList<MessageSignal> initialValues = aBot.getReceivedMessages();
		assertTrue(initialValues.size() == 0);
	}

	@Test
	public void testSetEnergy() {
		aBot.addEnergy(-1);
		assertTrue(aBot.getEnergy() == 9);

		aBot.addEnergy(10);
		assertTrue(aBot.getEnergy() == 10);

		aBot.addEnergy(-15);
		assertTrue(aBot.getEnergy() == -5);
	}

	@Test
	public void testGetMemoryDoesNotReference() {
		Memory memCopy = aBot.getMemory();

		assertTrue(!(memCopy == aBot.getMemory()));

		assertTrue(aBot.getMemory().getBit(1) == false);
		memCopy.storeBit(1, true);
		assertTrue(aBot.getMemory().getBit(1) == false);
	}

	@Test
	public void testSetMemory() {
		Memory newMem = new Memory();
		newMem.storeBit(0, true);

		assertTrue(aBot.getMemory().getBit(0) == false);
		aBot.setMemory(newMem);
		assertTrue(aBot.getMemory().getBit(0) == true);

		// not copied by reference
		newMem.storeBit(0, false);
		assertTrue(aBot.getMemory().getBit(0) == true);

	}

	@Test
	public void testTeamsEqual() {
		BotEntity notOnABotsTeam = Entity.getNewBot(1, 42);

		assertTrue(BotEntity.areAllies(aBot, notOnABotsTeam) == false);
	}

	@Test
	public void testAddReceivedMessage() {
		boolean[] msgData =
			{
					true, false, false
			};
		MessageSignal aMsgSgnl = getMessageSignalInstance(msgData, 3);

		aBot.addReceivedMessage(aMsgSgnl);
		
		
		ImmutableList<MessageSignal> botInbox = aBot.getReceivedMessages();
		assertTrue(botInbox.size() == 1);

		assertTrue(messageSignalEqual(aMsgSgnl, botInbox.get(0)));
	}

	/* ---------------------------- */

	private boolean messageSignalEqual(MessageSignal a, MessageSignal b) {
		if (a.getSignalStrength() != b.getSignalStrength()) {
			return false;
		}

		Message msgA = a.getMessage();
		Message msgB = b.getMessage();

		if (!messageEqual(msgA, msgB)) {
			return false;
		}

		return true;
	}

	private boolean messageEqual(Message a, Message b) {
		boolean[] aContents = a.getBits(0, Settings.getMessageLength());
		boolean[] bContents = b.getBits(0, Settings.getMessageLength());

		return Arrays.equals(aContents, bContents);
	}

	private MessageSignal getMessageSignalInstance(boolean[] contents,
			int strength) {
		Message msg = new Message(contents);
		MessageSignal ret = new MessageSignal(msg, strength);

		return ret;
	}
}
