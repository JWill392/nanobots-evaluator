package brain;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.base.Objects;

import teampg.grid2d.GridInterface.Entry;
import teampg.grid2d.ReadGrid;
import teampg.grid2d.point.AbsPos;
import teampg.grid2d.point.RelPos;

import entity.BotEntity;
import entity.EmptyEntity;
import entity.Entity;
import entity.FoodEntity;
import entity.WallEntity;

public class Vision implements Iterable<Entry<Character>> {
	public static char FRIENDLY_BOT = 'b';
	public static char ENEMY_BOT = 'e';
	public static char FOOD = 'f';
	public static char WALL = 'w';
	public static char EMPTY = ' ';

	private final Map<AbsPos, Character> vis;
	private final AbsPos observerPos;

	public Vision(ReadGrid<Entity> map, Collection<AbsPos> visible, AbsPos observerPos) {
		checkArgument(map.get(observerPos) instanceof BotEntity);

		vis = new HashMap<>();
		this.observerPos = observerPos;
		BotEntity observer = (BotEntity) map.get(observerPos);

		for (AbsPos visibleCell : visible) {
			Character symbolForEnt = getSymbolForEntity(map.get(visibleCell), observer);
			vis.put(visibleCell, symbolForEnt);
		}
	}

	public char get(RelPos at) {
		AbsPos fromMid = RelPos.offset(observerPos, at);
		if (!vis.containsKey(fromMid)) {
			throw new IndexOutOfBoundsException("Position outside vision");
		}

		return vis.get(fromMid);
	}

	private static char getSymbolForEntity(Entity e, BotEntity beholder) {
		char visionSymbol = 0;

		if (e instanceof BotEntity) {
			BotEntity other = (BotEntity) e;

			if (BotEntity.areAllies(beholder, other)) {
				visionSymbol = FRIENDLY_BOT;
			} else {
				visionSymbol = ENEMY_BOT;
			}

		} else if (e instanceof FoodEntity) {
			visionSymbol = FOOD;

		} else if (e instanceof EmptyEntity) {
			visionSymbol = EMPTY;

		} else if (e instanceof WallEntity) {
			visionSymbol = WALL;

		} else {
			// above should cover every type
			assert (false);
		}

		return visionSymbol;
	}

	@Override
	public Iterator<Entry<Character>> iterator() {
		final Iterator<Map.Entry<AbsPos, Character>> iter = vis.entrySet().iterator();

		return new Iterator<Entry<Character>>() {
			@Override
			public boolean hasNext() {
				return iter.hasNext();
			}

			@Override
			public Entry<Character> next() {
				Map.Entry<AbsPos, Character> iterNext = iter.next();
				return new Entry<Character>(iterNext.getKey(), iterNext.getValue());
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	public List<RelPos> indexOf(char c) {
		ArrayList<RelPos> found = new ArrayList<>();

		for (Map.Entry<AbsPos, Character> entry : vis.entrySet()) {
			if (entry.getValue().equals(c)) {
				RelPos relPoint = RelPos.offsetVector(observerPos, entry.getKey());
				found.add(relPoint);
			}
		}

		return found;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("Mid", observerPos)
				.add("VisionGrid", vis)
				.toString();
	}
}
