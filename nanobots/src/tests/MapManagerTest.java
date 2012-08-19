package tests;

import static org.junit.Assert.*;
import static tests.Util.*;

import entity.BotEntity;
import entity.Entity;
import entity.FoodEntity;
import game.Settings;
import game.world.World;


import org.junit.Before;
import org.junit.Test;

import teampg.grid2d.point.AbsPos;

import brain.BrainInfo;



public class MapManagerTest {

	World grid;
	FoodEntity aFood;
	BotEntity aBot;
	BotEntity bBot;

	@Before
	public void setUp() throws Exception {
		Settings.load();
		Settings.setBotMaxEnergy(10);
		Settings.setVisionRadius(2);
		Settings.lock();

		aFood = Entity.getNewFood(1);
		aBot = Entity.getNewBot(1, 1);
		bBot = Entity.getNewBot(1, 0);
		grid = new World(4, 3);
		/*   Expected dimensions
		 *   *---*---*---*---*
		 *   |   |   |   |   |
		 *   *---*---*---*---*
		 *   |   |   |   |   |
		 *   *---*---*---*---*
		 *   |   |   |   |   |
		 *   *---*---*---*---*
		 */
	}

	@Test
	public void testLoad() {
		/*		Charset charset = Charset.forName("US-ASCII");
				Path file = Paths.get(fileName);

				try (BufferedWriter writer = Files.newBufferedWriter(file, charset)) {
				} catch (IOException x) {
					System.err.format("IOException: %s%n", x);
				}
				String s = bam.getFirstName() + " " + bam.getLastName() + " "
				                + bam.getAge();
				writer.write(s);
				writer.newLine();*/
	}

	@Test
	public void testGrid() {
		assertIsEntityType(grid.get(AbsPos.of(0, 0)), Util.EMPTY);
		assertIsEntityType(grid.get(AbsPos.of(0, 1)), Util.EMPTY);
		assertIsEntityType(grid.get(AbsPos.of(0, 2)), Util.EMPTY);
		assertIsEntityType(grid.get(AbsPos.of(1, 0)), Util.EMPTY);
		assertIsEntityType(grid.get(AbsPos.of(1, 1)), Util.EMPTY);
		assertIsEntityType(grid.get(AbsPos.of(1, 2)), Util.EMPTY);
		assertIsEntityType(grid.get(AbsPos.of(2, 0)), Util.EMPTY);
		assertIsEntityType(grid.get(AbsPos.of(2, 1)), Util.EMPTY);
		assertIsEntityType(grid.get(AbsPos.of(2, 2)), Util.EMPTY);
		assertIsEntityType(grid.get(AbsPos.of(3, 0)), Util.EMPTY);
		assertIsEntityType(grid.get(AbsPos.of(3, 1)), Util.EMPTY);
		assertIsEntityType(grid.get(AbsPos.of(3, 2)), Util.EMPTY);

		// co-ordinates out of grid are wall
		assertIsEntityType(grid.get(AbsPos.of(-1, 0)), Util.WALL);
		assertIsEntityType(grid.get(AbsPos.of(0, -1)), Util.WALL);
		assertIsEntityType(grid.get(AbsPos.of(4, 0)), Util.WALL);
		assertIsEntityType(grid.get(AbsPos.of(0, 3)), Util.WALL);
	}

	@Test
	public void testAddNewEntity() {
		AbsPos foodPos = AbsPos.of(0, 2);
		grid.addNewEntity(foodPos, aFood);

		assertTrue(grid.get(foodPos) == aFood);
	}

	@Test
	public void testAddNewBots() {
		AbsPos aBotPos = AbsPos.of(2, 1);
		grid.addNewEntity(aBotPos, aBot);
		assertTrue(grid.get(aBotPos) == aBot);

		AbsPos bBotPos = AbsPos.of(3, 1);
		grid.addNewEntity(bBotPos, bBot);
		assertTrue(grid.get(bBotPos) == bBot);
	}

	@Test
	public void testGetBotPosition() {
		AbsPos aBotPos = AbsPos.of(2, 1);
		grid.addNewEntity(aBotPos, aBot);

		AbsPos bBotPos = AbsPos.of(3, 1);
		grid.addNewEntity(bBotPos, bBot);

		int aBotID = aBot.getID();
		AbsPos aBotPosByID = grid.getBotPosition(aBotID);
		assertTrue(aBotPosByID.equals(aBotPos));

		int bBotID = bBot.getID();
		AbsPos bBotPosByID = grid.getBotPosition(bBotID);
		assertTrue(bBotPosByID.equals(bBotPos));
	}

	@Test
	public void testGetBotByID() {
		testAddNewBots();

		int aBotID = aBot.getID();
		BotEntity aFoundBot = grid.get(aBotID);
		assertTrue(aFoundBot == aBot);

		int bBotID = bBot.getID();
		BotEntity bFoundBot = grid.get(bBotID);
		assertTrue(bFoundBot == bBot);
	}

	@Test
	public void testGetBotInfo() {
		BotEntity someBot = Entity.getNewBot(5, 0);
		int someID = someBot.getID();
		AbsPos somePos = AbsPos.of(2, 1);

		grid.addNewEntity(somePos, someBot);
		BrainInfo nfo = grid.getBotInfo(someID);

		assertTrue(nfo.getEnergy() == 5);
		// not fully testing botinfo
	}

	@Test
	public void testMoveOnce() {
		AbsPos start = AbsPos.of(2, 1);
		grid.addNewEntity(start, aBot);

		int botID = aBot.getID();
		AbsPos destination = AbsPos.of(2, 2);
		grid.move(botID, destination);

		AbsPos botPosByID = grid.getBotPosition(botID);
		assertTrue(botPosByID.equals(destination));

		Entity atDestination = grid.get(destination);
		assertTrue(atDestination == aBot);

		Entity atStart = grid.get(start);
		assertIsEntityType(atStart, Util.EMPTY);
	}

	@Test
	public void testMoveMany() {
		AbsPos start = AbsPos.of(3, 2);
		grid.addNewEntity(start, bBot);

		int botID = bBot.getID();
		AbsPos destination = AbsPos.of(0, 0);
		grid.move(botID, destination);

		AbsPos botPosByID = grid.getBotPosition(botID);
		assertTrue(botPosByID.equals(destination));

		Entity atDestination = grid.get(destination);
		assertTrue(atDestination == bBot);

		Entity atStart = grid.get(start);
		assertIsEntityType(atStart, Util.EMPTY);

		// moving a second time works?
		start = AbsPos.of(0, 0);

		destination = AbsPos.of(2, 2);
		grid.move(botID, destination);

		botPosByID = grid.getBotPosition(botID);
		assertTrue(botPosByID.equals(destination));

		atDestination = grid.get(destination);
		assertTrue(atDestination == bBot);

		atStart = grid.get(start);
		assertIsEntityType(atStart, Util.EMPTY);
	}

	@Test
	public void testClear() {
		AbsPos botPos = AbsPos.of(2, 1);
		grid.addNewEntity(botPos, aBot);

		assertTrue(grid.get(botPos) == aBot);
		grid.destroy(botPos);
		assertIsEntityType(grid.get(botPos), Util.EMPTY);

		try {
			grid.getBotPosition(aBot.getID());
			fail("Bot should be removed from index by clear");
		} catch (AssertionError e) { // TODO-ERROR exception for bot not found?
		}
	}

	@Test
	public void testProxBots() {
		fail("Test not yet implemented");
	}

}
