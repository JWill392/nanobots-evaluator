package tests;

import static org.junit.Assert.*;
import static tests.Util.*;

import entity.BotEntity;
import entity.Entity;
import entity.FoodEntity;
import game.World;
import game.Settings;


import org.junit.Before;
import org.junit.Test;

import ca.camosun.jwill392.datatypes.grid2d.point.AbsPos;

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
		assertIsEntityType(grid.get(new AbsPos(0, 0)), Util.EMPTY);
		assertIsEntityType(grid.get(new AbsPos(0, 1)), Util.EMPTY);
		assertIsEntityType(grid.get(new AbsPos(0, 2)), Util.EMPTY);
		assertIsEntityType(grid.get(new AbsPos(1, 0)), Util.EMPTY);
		assertIsEntityType(grid.get(new AbsPos(1, 1)), Util.EMPTY);
		assertIsEntityType(grid.get(new AbsPos(1, 2)), Util.EMPTY);
		assertIsEntityType(grid.get(new AbsPos(2, 0)), Util.EMPTY);
		assertIsEntityType(grid.get(new AbsPos(2, 1)), Util.EMPTY);
		assertIsEntityType(grid.get(new AbsPos(2, 2)), Util.EMPTY);
		assertIsEntityType(grid.get(new AbsPos(3, 0)), Util.EMPTY);
		assertIsEntityType(grid.get(new AbsPos(3, 1)), Util.EMPTY);
		assertIsEntityType(grid.get(new AbsPos(3, 2)), Util.EMPTY);

		// co-ordinates out of grid are wall
		assertIsEntityType(grid.get(new AbsPos(-1, 0)), Util.WALL);
		assertIsEntityType(grid.get(new AbsPos(0, -1)), Util.WALL);
		assertIsEntityType(grid.get(new AbsPos(4, 0)), Util.WALL);
		assertIsEntityType(grid.get(new AbsPos(0, 3)), Util.WALL);
	}

	@Test
	public void testAddNewEntity() {
		AbsPos foodPos = new AbsPos(0, 2);
		grid.addNewEntity(foodPos, aFood);

		assertTrue(grid.get(foodPos) == aFood);
	}

	@Test
	public void testAddNewBots() {
		AbsPos aBotPos = new AbsPos(2, 1);
		grid.addNewEntity(aBotPos, aBot);
		assertTrue(grid.get(aBotPos) == aBot);

		AbsPos bBotPos = new AbsPos(3, 1);
		grid.addNewEntity(bBotPos, bBot);
		assertTrue(grid.get(bBotPos) == bBot);
	}

	@Test
	public void testGetBotPosition() {
		AbsPos aBotPos = new AbsPos(2, 1);
		grid.addNewEntity(aBotPos, aBot);

		AbsPos bBotPos = new AbsPos(3, 1);
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
		AbsPos somePos = new AbsPos(2, 1);

		grid.addNewEntity(somePos, someBot);
		BrainInfo nfo = grid.getBotInfo(someID);

		assertTrue(nfo.getEnergy() == 5);
		// not fully testing botinfo
	}

	@Test
	public void testMoveOnce() {
		AbsPos start = new AbsPos(2, 1);
		grid.addNewEntity(start, aBot);

		int botID = aBot.getID();
		AbsPos destination = new AbsPos(2, 2);
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
		AbsPos start = new AbsPos(3, 2);
		grid.addNewEntity(start, bBot);

		int botID = bBot.getID();
		AbsPos destination = new AbsPos(0, 0);
		grid.move(botID, destination);

		AbsPos botPosByID = grid.getBotPosition(botID);
		assertTrue(botPosByID.equals(destination));

		Entity atDestination = grid.get(destination);
		assertTrue(atDestination == bBot);

		Entity atStart = grid.get(start);
		assertIsEntityType(atStart, Util.EMPTY);

		// moving a second time works?
		start = new AbsPos(0, 0);

		destination = new AbsPos(2, 2);
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
		AbsPos botPos = new AbsPos(2, 1);
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
	
	/*
	 * TODO move these tests into GridInterface
	 * @Test
	public void testGetProximateSmallRadius() {
		addThingsToGrid();

		   Expected proximate
		 *   *---*---*---*
		 *   | w | w | w |
		 *   *---*---*---*
		 *   | w | 1 | f |
		 *   *---*---*---*
		 *   | w |   |   |
		 *   *---*---*---*
		 
		Entity centerBot = grid.get(new AbsPos(0, 0));
		Entity[][] proxGrid = grid.getProximate(new AbsPos(0, 0), 1);

		// correct dimensions
		assertTrue(proxGrid.length == 3);
		assertTrue(proxGrid[0].length == 3);

		// centre of near is same as specified centre of getProximate
		assertTrue(proxGrid[1][1] == centerBot);

		// every Entity in proximate grid is as expected
		assertIsEntityType(proxGrid[0][0], Util.WALL);
		assertIsEntityType(proxGrid[0][1], Util.WALL);
		assertIsEntityType(proxGrid[0][2], Util.WALL);

		assertIsEntityType(proxGrid[1][0], Util.WALL);
		assertIsEntityType(proxGrid[1][1], Util.BOT);
		assertIsEntityType(proxGrid[1][2], Util.FOOD);

		assertIsEntityType(proxGrid[2][0], Util.WALL);
		assertIsEntityType(proxGrid[2][1], Util.EMPTY);
		assertIsEntityType(proxGrid[2][2], Util.EMPTY);
	}

	@Test
	public void testGetProximateBigRadius() {
		addThingsToGrid();

		   Expected proximate
		 *     0   1   2   3   4   x
		 *   *---*---*---*---*---*
		 * 0 | w | w | w | w | w |
		 *   *---*---*---*---*---*
		 * 1 | 1 | f | w |   | w |
		 *   *---*---*---*---*---*
		 * 2 |   |   | 2 | 2 | w |
		 *   *---*---*---*---*---*
		 * 3 | w | f | 1 |   | w |
		 *   *---*---*---*---*---*
		 * 4 | w | w | w | w | w |
		 *   *---*---*---*---*---*
		 * y
		 
		Entity centerBot = grid.get(new AbsPos(2, 1));
		Entity[][] proxGrid = grid.getProximate(new AbsPos(2, 1), 2);

		// correct dimensions
		assertTrue(proxGrid.length == 5);
		assertTrue(proxGrid[0].length == 5);

		// centre of near is same as specified centre of getProximate
		assertTrue(proxGrid[2][2] == centerBot);

		TODO-TEST
		 * Write a test util method that generates the below asserts given the input:
		 "wwwww\n" +
		 "1fw w\n" +
		 "  22w\n" +
		 "wf1 w\n" +
		 "wwwww\n"
		 
		 

		// every Entity in proximate grid is as expected
		assertIsEntityType(proxGrid[0][0], Util.WALL);
		assertIsEntityType(proxGrid[0][1], Util.WALL);
		assertIsEntityType(proxGrid[0][2], Util.WALL);
		assertIsEntityType(proxGrid[0][3], Util.WALL);
		assertIsEntityType(proxGrid[0][4], Util.WALL);

		assertIsEntityType(proxGrid[1][0], Util.BOT);
		assertIsEntityType(proxGrid[1][1], Util.FOOD);
		assertIsEntityType(proxGrid[1][2], Util.WALL);
		assertIsEntityType(proxGrid[1][3], Util.EMPTY);
		assertIsEntityType(proxGrid[1][4], Util.WALL);

		assertIsEntityType(proxGrid[2][0], Util.EMPTY);
		assertIsEntityType(proxGrid[2][1], Util.EMPTY);
		assertIsEntityType(proxGrid[2][2], Util.BOT);
		assertIsEntityType(proxGrid[2][3], Util.BOT);
		assertIsEntityType(proxGrid[2][4], Util.WALL);

		assertIsEntityType(proxGrid[3][0], Util.WALL);
		assertIsEntityType(proxGrid[3][1], Util.FOOD);
		assertIsEntityType(proxGrid[3][2], Util.BOT);
		assertIsEntityType(proxGrid[3][3], Util.EMPTY);
		assertIsEntityType(proxGrid[3][4], Util.WALL);

		assertIsEntityType(proxGrid[4][0], Util.WALL);
		assertIsEntityType(proxGrid[4][1], Util.WALL);
		assertIsEntityType(proxGrid[4][2], Util.WALL);
		assertIsEntityType(proxGrid[4][3], Util.WALL);
		assertIsEntityType(proxGrid[4][4], Util.WALL);
	}

	private void addThingsToGrid() {
		/*   Stuff in grid (1 is bot on team 1)
		 *     0   1   2   3    x
		 *   *---*---*---*---*
		 * 0 | 1 | f | w |   |
		 *   *---*---*---*---*
		 * 1 |   |   | 2 | 2 |
		 *   *---*---*---*---*
		 * 2 | w | f | 1 |   |
		 *   *---*---*---*---*
		 * y
		 * 
		 */
	/*
		grid.addNewEntity(new AbsPos(0, 0), Entity.getNewBot(1, 1));
		grid.addNewEntity(new AbsPos(1, 0), Entity.getNewFood(1));
		grid.addNewEntity(new AbsPos(2, 0), Entity.getNewWall());

		grid.addNewEntity(new AbsPos(2, 1), Entity.getNewBot(1, 2));
		grid.addNewEntity(new AbsPos(3, 1), Entity.getNewBot(1, 2));

		grid.addNewEntity(new AbsPos(0, 2), Entity.getNewWall());
		grid.addNewEntity(new AbsPos(1, 2), Entity.getNewFood(1));
		grid.addNewEntity(new AbsPos(2, 2), Entity.getNewBot(1, 1));
	}*/

}
