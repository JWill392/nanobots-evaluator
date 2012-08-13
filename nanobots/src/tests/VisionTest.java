package tests;

import static org.junit.Assert.*;

import java.awt.Dimension;
import java.util.Arrays;
import java.util.Collection;

import entity.BotEntity;
import entity.Entity;
import game.Settings;

import org.junit.Before;
import org.junit.Test;

import teampg.grid2d.RectGrid;
import teampg.grid2d.point.AbsPos;
import teampg.grid2d.point.RelPos;

import brain.Vision;

public class VisionTest {
	Vision v;
	BotEntity beholder;

	@Before
	public void setup() {
		Settings.load();
		Settings.setVisionRadius(1);
		Settings.lock();

		/*   entData looks like
		 *   *---*---*---*
		 *   | f |   |   |
		 *   *---*---*---*
		 *   | e | b | b |
		 *   *---*---*---*
		 *   |   | w |   |
		 *   *---*---*---*
		 */
		beholder = Entity.getNewBot(1, 0);

		RectGrid<Entity> entData = new RectGrid<Entity>(new Dimension(3, 3));
		entData.set(AbsPos.of(0, 0), Entity.getNewFood(1));
		entData.set(AbsPos.of(1, 0), Entity.getNewEmpty());
		entData.set(AbsPos.of(2, 0), Entity.getNewEmpty());

		entData.set(AbsPos.of(0, 1), Entity.getNewBot(1, 1));
		entData.set(AbsPos.of(1, 1), beholder);
		entData.set(AbsPos.of(2, 1), Entity.getNewBot(1, 0));

		entData.set(AbsPos.of(0, 2), Entity.getNewEmpty());
		entData.set(AbsPos.of(1, 2), Entity.getNewWall());
		entData.set(AbsPos.of(2, 2), Entity.getNewEmpty());

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
			v = new Vision(entData, inVisRange, AbsPos.of(1, 1));
		}
	}

	@Test
	public void testSmallGrid() {
		assertTrue(Util.lengthOfIterable(v) == 9);

		assertTrue(v.get(RelPos.of(-1, -1)) == Vision.FOOD);
		assertTrue(v.get(RelPos.of(0, -1)) == Vision.EMPTY);
		assertTrue(v.get(RelPos.of(1, -1)) == Vision.EMPTY);

		assertTrue(v.get(RelPos.of(-1, 0)) == Vision.ENEMY_BOT);
		assertTrue(v.get(RelPos.of(0, 0)) == Vision.FRIENDLY_BOT);
		assertTrue(v.get(RelPos.of(1, 0)) == Vision.FRIENDLY_BOT);

		assertTrue(v.get(RelPos.of(-1, 1)) == Vision.EMPTY);
		assertTrue(v.get(RelPos.of(0, 1)) == Vision.WALL);
		assertTrue(v.get(RelPos.of(1, 1)) == Vision.EMPTY);
	}

	@Test
	public void testIndexOf() {
		fail("Not yet implemented");
	}

	@Test
	public void testIterator() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetOutOfBounds() {
		try {
			v.get(RelPos.of(-2, 0));
			fail("getting out of bounds index should throw IndexOutOfBoundsException");
		} catch (IndexOutOfBoundsException e) {
			assert (true);
		}
	}
}