package tests;

import static org.junit.Assert.*;
import entity.BotEntity;
import entity.EmptyEntity;
import entity.Entity;
import entity.FoodEntity;
import entity.WallEntity;


public class Util {
	public static final Class<? extends Entity> WALL = WallEntity.class;
	public static final Class<? extends Entity> FOOD = FoodEntity.class;
	public static final Class<? extends Entity> EMPTY = EmptyEntity.class;
	public static final Class<? extends Entity> BOT = BotEntity.class;

	public static void assertIsEntityType(Entity e, Class<? extends Entity> type) {
		assertTrue(e.getClass() == type);
	}

	public static <E> int instancesInIterable(Iterable<E> i, E toCount) {
		int count = 0;
		for (E element : i) {
			if (element == toCount) {
				count++;
			}
		}

		return count;
	}

	public static <E> int lengthOfIterable(Iterable<E> i) {
		int count = 0;
		for (@SuppressWarnings("unused")
		E element : i) {
			count++;
		}

		return count;
	}
}
