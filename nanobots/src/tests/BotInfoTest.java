package tests;

import static org.junit.Assert.*;

import java.awt.Dimension;
import java.util.Arrays;
import java.util.Collection;

import entity.BotEntity;
import entity.Entity;
import entity.bot.Memory;
import entity.bot.Message;
import entity.bot.MessageSignal;
import game.Settings;


import org.junit.Before;
import org.junit.Test;

import teampg.grid2d.RectGrid;
import teampg.grid2d.point.AbsPos;
import teampg.grid2d.point.RelPos;

import com.google.common.collect.ImmutableList;

import brain.BrainInfo;
import brain.Vision;

public class BotInfoTest {
	Memory mem;
	MessageSignal[] sentMsgs;
	int ener;
	BotEntity b;
	BrainInfo i;

	@Before
	public void setUp() throws Exception {
		Settings.load();
		Settings.setMessageLength(3);
		Settings.lock();

		ener = 8;
		mem = new Memory();
		mem.storeBit(1, true);

		sentMsgs = new MessageSignal[5];
		boolean[] msgContents =
			{
					true, false, true
			};
		Message aMsg = new Message(msgContents);
		sentMsgs[0] = new MessageSignal(aMsg, 3);

		b = Entity.getNewBot(ener, 0);
		b.setMemory(mem);
		b.addReceivedMessage(sentMsgs[0]);

		RectGrid<Entity> prox = new RectGrid<>(new Dimension(3, 3));
		prox.set(AbsPos.of(0, 0), Entity.getNewFood(1));
		prox.set(AbsPos.of(1, 0), Entity.getNewEmpty());
		prox.set(AbsPos.of(2, 0), Entity.getNewEmpty());

		prox.set(AbsPos.of(0, 1), Entity.getNewBot(1, 1));
		prox.set(AbsPos.of(1, 1), b);
		prox.set(AbsPos.of(2, 1), Entity.getNewBot(1, 0));

		prox.set(AbsPos.of(0, 2), Entity.getNewEmpty());
		prox.set(AbsPos.of(1, 2), Entity.getNewWall());
		prox.set(AbsPos.of(2, 2), Entity.getNewEmpty());

		{
			AbsPos[] inVisRangeArray = {
					AbsPos.of(0, 0),
					AbsPos.of(1, 0),
					AbsPos.of(2, 0),
					AbsPos.of(0, 1),
					AbsPos.of(1, 1),
					AbsPos.of(2, 1),
					AbsPos.of(0, 2),
					AbsPos.of(1, 2),
					AbsPos.of(2, 2)
			};
			Collection<AbsPos> inVisRange = Arrays.asList(inVisRangeArray);
			i = new BrainInfo(b, prox, inVisRange);
		}
	}

	@Test
	public void testBotInfo() {
		assertTrue(i.getEnergy() == 8);
		assertTrue(i.getMemory().equals(mem));
		ImmutableList<MessageSignal> receivedMsgs = i.getMessages();

		assertTrue(receivedMsgs.size() == 1);
		assertTrue(sentMsgs[0] == receivedMsgs.get(0));

		Vision v = i.getVision();
		assertTrue(v.get(RelPos.of(-1, -1)) == Vision.FOOD);
		assertTrue(v.get(RelPos.of( 0, -1)) == Vision.EMPTY);
		assertTrue(v.get(RelPos.of( 1, -1)) == Vision.EMPTY);

		assertTrue(v.get(RelPos.of(-1,  0)) == Vision.ENEMY_BOT);
		assertTrue(v.get(RelPos.of( 0,  0)) == Vision.FRIENDLY_BOT);
		assertTrue(v.get(RelPos.of( 1,  0)) == Vision.FRIENDLY_BOT);

		assertTrue(v.get(RelPos.of(-1,  1)) == Vision.EMPTY);
		assertTrue(v.get(RelPos.of( 0,  1)) == Vision.WALL);
		assertTrue(v.get(RelPos.of( 1,  1)) == Vision.EMPTY);
	}
}
