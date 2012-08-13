package tests;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import entity.BotEntity;
import entity.Entity;
import entity.bot.*;
import game.Settings;
import game.Team;


import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;


public class BotEntityTest {
	BotEntity aBot;
	Team mockTeam;

	@Before
	public void setUp() throws Exception {
		Settings.load();
		Settings.setMemorySize(3);
		Settings.setMessageLength(3);
		Settings.setBotMaxEnergy(10);
		Settings.lock();

		mockTeam = mock(Team.class);

		aBot = Entity.getNewBot(10, mockTeam);
	}

	@Test
	public void testNewInstance() {
		assertTrue(aBot.getEnergy() == 10);
		assertTrue(aBot.getTeam() == mockTeam);
		assertEquals(0, aBot.getMemory().getAll());
		assertEquals(0, aBot.getReceivedMessages().size());
	}

	@Test
	public void testSetEnergy() {
		aBot.addEnergy(-1);
		assertEquals(9, aBot.getEnergy());

		aBot.addEnergy(10);
		assertEquals(10, aBot.getEnergy());

		aBot.addEnergy(-15);
		assertEquals(-5, aBot.getEnergy());
	}

	@Test
	public void testGetMemoryDoesNotReference() {
		Memory memCopy = aBot.getMemory();
		assertNotSame(memCopy, aBot.getMemory());

		// changing copy does not change original
		assertEquals(false, aBot.getMemory().getBit(1));
		memCopy.setBit(1, true);
		assertEquals(false, aBot.getMemory().getBit(1));
	}

	@Test
	public void testTeamsEqual() {
		BotEntity notOnABotsTeam = Entity.getNewBot(10, mock(Team.class));

		assertFalse(BotEntity.areAllies(aBot, notOnABotsTeam));
	}

	@Test
	public void testAddReceivedMessage() {
		MessageSignal aMockMsg = mock(MessageSignal.class);
		aBot.addReceivedMessage(aMockMsg);

		MessageSignal bMockMsg = mock(MessageSignal.class);
		aBot.addReceivedMessage(bMockMsg);

		ImmutableList<MessageSignal> botInbox = aBot.getReceivedMessages();
		assertTrue(botInbox.size() == 2);
		assertTrue(botInbox.contains(aMockMsg));
		assertTrue(botInbox.contains(bMockMsg));
	}

}
