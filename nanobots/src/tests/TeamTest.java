package tests;

import static org.junit.Assert.*;

import java.awt.Dimension;

import entity.BotEntity;
import entity.Entity;
import game.Settings;
import game.Team;


import org.junit.Before;
import org.junit.Test;

import teampg.grid2d.RectGrid;
import teampg.grid2d.point.AbsPos;

import brain.BotBrain;
import brain.BrainCommand;
import brain.BrainInfo;
import brain.Vision;


import action.ActionCmd;
import action.cmd.Wait;

public class TeamTest {
	Team aTeam;

	@Before
	public void setUp() throws Exception {
		Settings.load();
		aTeam = Team.getNewTeam(new BotBrain("Foobrain") {

			@Override
			protected BrainCommand brainDecideAction(BrainInfo info)
					throws Exception {
				return new BrainCommand(new Wait(), null);
			}

		}, "Foo Fighters");
	}

	@Test
	public void testGetNewTeam() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetLost() {
		assertTrue(aTeam.hasLost() == false);

		aTeam.setLost();
		assertTrue(aTeam.hasLost() == true);
	}

	@Test
	public void testSimpleDecideAction() {

		BotEntity bot = Entity.getNewBot(1, 0);
		RectGrid<Entity> entData = new RectGrid<>(new Dimension(3, 3));
		entData.set(AbsPos.of(0, 0), Entity.getNewFood(1));
		entData.set(AbsPos.of(1, 0), Entity.getNewEmpty());
		entData.set(AbsPos.of(2, 0), Entity.getNewEmpty());

		entData.set(AbsPos.of(0, 0), Entity.getNewBot(1, 1));
		entData.set(AbsPos.of(1, 0), bot);
		entData.set(AbsPos.of(2, 0), Entity.getNewBot(1, 0));

		entData.set(AbsPos.of(0, 0), Entity.getNewEmpty());
		entData.set(AbsPos.of(1, 0), Entity.getNewWall());
		entData.set(AbsPos.of(2, 0), Entity.getNewEmpty());

		BrainInfo fakeInfo = new BrainInfo(bot, new Vision(entData, //TODO points, AbsPos.of(1, 0)));
		BrainCommand cmd = aTeam.decideAction(fakeInfo,
				bot.getID());

		ActionCmd decided = cmd.getAction();
		ActionCmd expected = new Wait();
		assertTrue(ActionCmd.sameActionType(decided, expected));
	}

	@Test
	public void testAddBot() {
		fail("Not yet implemented");
	}

	@Test
	public void testRemoveBot() {
		fail("Not yet implemented");
	}

	@Test
	public void testIterator() {
		/*
		fail("test removing bots, see if iterator still works as expected");
		final int TEAM_A = 48;
		final int TEAM_B = 94;
		BotEntity cBot = Entity.getNewBot(1, TEAM_A);
		grid.addNewEntity(new Point(0,0), cBot);
		BotEntity dBot = Entity.getNewBot(1, TEAM_A);
		grid.addNewEntity(new Point(0,1), dBot);
		BotEntity eBot = Entity.getNewBot(1, TEAM_A);
		grid.addNewEntity(new Point(0,2), eBot);

		BotEntity fBot = Entity.getNewBot(1, TEAM_B);
		grid.addNewEntity(new Point(1,0), fBot);
		BotEntity gBot = Entity.getNewBot(1, TEAM_B);
		grid.addNewEntity(new Point(1,1), gBot);
		BotEntity hBot = Entity.getNewBot(1, TEAM_B);
		grid.addNewEntity(new Point(1,2), hBot);

		System.out.println("-------------");
		System.out.println("cBot id " + cBot.getID());
		System.out.println("dBot id " + dBot.getID());
		System.out.println("eBot id " + eBot.getID());

		//count number of times team iterator gives us each bot
		//for team 1
		Map<Integer, Integer> botCount = new HashMap<Integer, Integer>();
		botCount.put(cBot.getID(), 0);
		botCount.put(dBot.getID(), 0);
		botCount.put(eBot.getID(), 0);
		botCount.put(fBot.getID(), 0);
		botCount.put(gBot.getID(), 0);
		botCount.put(hBot.getID(), 0);

		for (int botID : grid.getTeamBots(TEAM_A)) {
			assertTrue(botCount.containsKey(botID));

			int timesIteratedOverThisBot = botCount.get(botID);
			botCount.put(botID, timesIteratedOverThisBot + 1);
		}

		assertTrue(botCount.get(cBot.getID()) == 1);
		assertTrue(botCount.get(dBot.getID()) == 1);
		assertTrue(botCount.get(eBot.getID()) == 1);
		assertTrue(botCount.get(fBot.getID()) == 0);
		assertTrue(botCount.get(gBot.getID()) == 0);
		assertTrue(botCount.get(hBot.getID()) == 0);

		//------------------------------------
		//again, but for other team
		botCount = new HashMap<Integer, Integer>();
		botCount.put(cBot.getID(), 0);
		botCount.put(dBot.getID(), 0);
		botCount.put(eBot.getID(), 0);
		botCount.put(fBot.getID(), 0);
		botCount.put(gBot.getID(), 0);
		botCount.put(hBot.getID(), 0);

		for (int botID : grid.getTeamBots(TEAM_B)) {
			assertTrue(botCount.containsKey(botID));

			int timesIteratedOverThisBot = botCount.get(botID);
			botCount.put(botID, timesIteratedOverThisBot + 1);
		}

		assertTrue(botCount.get(cBot.getID()) == 0);
		assertTrue(botCount.get(dBot.getID()) == 0);
		assertTrue(botCount.get(eBot.getID()) == 0);
		assertTrue(botCount.get(fBot.getID()) == 1);
		assertTrue(botCount.get(gBot.getID()) == 1);
		assertTrue(botCount.get(hBot.getID()) == 1);
		*/
	}

}
