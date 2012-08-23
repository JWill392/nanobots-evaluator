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

import teampg.grid2d.point.AbsPos;

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
		Settings.setNewbornEnergy(10);
		Settings.lock();

		mockTeam = mock(Team.class);

		aBot = Entity.getNewBot(mockTeam);
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
		assertEquals(19, aBot.getEnergy());

		aBot.addEnergy(-20);
		assertEquals(-1, aBot.getEnergy());
	}

	@Test
	public void testEnergyOverMaxDecreasesOnTick() {
		fail("TODO");
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
		BotEntity notOnABotsTeam = Entity.getNewBot(mock(Team.class));

		assertFalse(BotEntity.areAllies(aBot, notOnABotsTeam));
	}

	@Test
	public void testAddReceivedMessage() {
		MessageSignal aMockMsg = new MessageSignal(new Message(1), AbsPos.of(0, 0));
		aBot.addReceivedMessage(aMockMsg);

		MessageSignal bMockMsg = new MessageSignal(new Message(2), AbsPos.of(0, 0));
		aBot.addReceivedMessage(bMockMsg);

		ImmutableList<MessageSignal> botInbox = aBot.getReceivedMessages();
		assertTrue(botInbox.size() == 2);
		assertTrue(botInbox.contains(aMockMsg));
		assertTrue(botInbox.contains(bMockMsg));
	}

}
