package game.world;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import matchlog.MatchLog;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import teampg.grid2d.GridInterface;
import teampg.grid2d.GridInterface.Entry;
import teampg.grid2d.RectGrid;
import teampg.grid2d.point.AbsPos;
import teampg.grid2d.point.Pos2D;

import entity.BotEntity;
import entity.DynamicEntity;
import entity.Entity;
import entity.MortalEntity;
import game.Settings;
import game.Team;
import brain.BrainInfo;
import brain.Vision;

/**
 * World is the Entity manager. It keeps track of Entity positions. It is the
 * only thing that keeps Entity references between turns. As such, removing an
 * entity from World using {@link #destroy(AbsPos)} removes it from the game.
 *
 * @author Jackson Williams
 */
public class World {
	private final GridInterface<Entity> grid;
	private final Map<Integer, AbsPos> botIndex;
	private final Entity TO_APPEAR_OUTSIDE_GRID = Entity.getNewWall();

	// TODO removeme
	private final Entity DEBUG_PENDING_CHANGE_PLACEHOLDER = Entity.getNewWall();

	private final Queue<Entity> removeBuffer;
	private final Queue<Entry<Entity>> addBuffer;

	// TODO consider making private and including a static method to load from a
	// map
	public World(int width, int height) {
		removeBuffer = new LinkedList<>();
		addBuffer = new LinkedList<>();
		grid = new RectGrid<Entity>(new Dimension(width, height));

		botIndex = new HashMap<Integer, AbsPos>();
	}

	/**
	 * Finds position of a bot
	 *
	 * @param botID
	 * @return Position in grid
	 */
	public AbsPos getBotPosition(int botID) {
		// should never try to get a bot and find it's not there
		assert (botIndex.containsKey(botID));

		AbsPos pos = botIndex.get(botID);
		return pos;
	}

	/**
	 * Gets reference to a bot
	 *
	 * @param botID
	 * @return Reference to bot with given ID
	 */
	public BotEntity get(int botID) {
		// should never try to get a bot and find it's not there
		assert (botIndex.containsKey(botID));

		AbsPos pos = botIndex.get(botID);

		BotEntity found = (BotEntity) grid.get(pos);
		return found;
	}

	/**
	 * Builds a bot.Info object for given bot
	 *
	 * @param botID
	 * @return A {@link brain.BrainInfo} for specified bot
	 */
	public BrainInfo getBotInfo(int botID) {
		BotEntity observer = get(botID);
		AbsPos observerPos = getBotPosition(botID);

		Collection<AbsPos> visPoints = Pos2D
				.getDiamondNear(observerPos, Settings.getVisionRadius());

		BrainInfo info = new BrainInfo(observer, observerPos, new Vision(this, visPoints,
				observerPos));
		return info;
	}

	/**
	 * Get reference to Entity at position p
	 *
	 * @param p
	 *            Position to look
	 * @return Entity at position p
	 */
	public Entity get(AbsPos p) {
		if (!grid.isInBounds(p)) {
			return TO_APPEAR_OUTSIDE_GRID;
		}

		Entity found = grid.get(p);
		return found;
	}

	/**
	 * Get list of all bots on map in a certain team
	 */
	public Iterable<BotEntity> getTeamBots(final Team team) {
		List<BotEntity> ret = new ArrayList<>();

		for (AbsPos botPos : botIndex.values()) {
			BotEntity bot = (BotEntity) grid.get(botPos);
			if (bot.getTeam().equals(team)) {
				ret.add(bot);
			}
		}

		return ret;
	}

	/**
	 * Skips nulls
	 */
	public Iterable<Entry<Entity>> getEntries() {
		return Iterables.filter(grid.getEntries(), new Predicate<Entry<Entity>>() {
			@Override
			public boolean apply(Entry<Entity> input) {
				return input.getContents() != null;
			};
		});
	}

	// TODO move to util
	public Iterable<BotEntity> getProxBots(AbsPos near, int radius, Team team) {
		List<BotEntity> ret = new ArrayList<>();

		Collection<AbsPos> proxCells = Pos2D.getDiamondNear(near, radius);
		for (AbsPos cell : proxCells) {
			Entity proxEntity = get(cell);
			if (proxEntity instanceof BotEntity && ((BotEntity) proxEntity).getTeam().equals(team)) {
				ret.add((BotEntity) proxEntity);
			}
		}

		return ret;
	}

	/**
	 * Insert a new Entity into Grid.
	 *
	 * @param target
	 *            Position to put the new entity. Must be empty.
	 * @param toAdd
	 *            New entity to add into grid. Must not already be in grid.
	 */
	/*TODO should only happen in tick phase...
	 * Otherwise two bots could validly reproduce into same cell...
	 * one would succeed and the other would not.
	 * Or would kill the newborn or something.  Bad.
	 */
	public void addNewEntity(AbsPos target, Entity toAdd) {
		// target position for new entity should be empty
		assert (grid.get(target) != DEBUG_PENDING_CHANGE_PLACEHOLDER);
		assert (grid.get(target) == null);

		grid.set(target, DEBUG_PENDING_CHANGE_PLACEHOLDER); // TODO removeme; pending new ent; don't want anything else to happen here
		addBuffer.add(new Entry<Entity>(target, toAdd));
	}

	public void swap(AbsPos a, AbsPos b) {
		Entity aEnt = get(a);
		if (aEnt instanceof BotEntity) {
			botIndex.put(((BotEntity) aEnt).getID(), b);
		}

		Entity bEnt = get(b);
		if (bEnt instanceof BotEntity) {
			botIndex.put(((BotEntity) bEnt).getID(), a);
		}

		grid.set(a, bEnt);
		grid.set(b, aEnt);
	}

	/**
	 * Removes Entity at position. Used to kill bots or food. Using on WALL or
	 * EMPTY will cause panic.
	 *
	 * @param i
	 */
	public void destroy(Entity mortEnt) {
		removeBuffer.add(mortEnt);
	}

	private void flushRemoveBuffer() {
		while (!removeBuffer.isEmpty()) {
			Entity fakeEntToDie = removeBuffer.remove();

			Entry<Entity> entry = grid.get(fakeEntToDie);
			AbsPos pos = entry.getPosition();
			Entity entToDie = entry.getContents();

			if (entToDie instanceof BotEntity) {
				BotEntity botToDie = ((BotEntity) entToDie);
				botIndex.remove(botToDie.getID());
			}

			grid.set(pos, null);

			MatchLog.removeEntity(entToDie);
		}
	}

	private void flushAddBuffer() {
		while (!addBuffer.isEmpty()) {
			Entry<Entity> entry = addBuffer.remove();
			Entity toAdd = entry.getContents();
			AbsPos addPos = entry.getPosition();

			assert(grid.get(addPos) == DEBUG_PENDING_CHANGE_PLACEHOLDER);

			if (toAdd instanceof BotEntity) {
				BotEntity newBot = (BotEntity) toAdd;
				botIndex.put(newBot.getID(), addPos);
			}

			grid.set(addPos, toAdd);

			MatchLog.addEntity(toAdd, addPos);
		}
	}

	public void tick() {
		for (DynamicEntity dynEnt : Iterables.filter(grid, DynamicEntity.class)) {
			dynEnt.tick();
		}

		for (MortalEntity mortEnt : Iterables.filter(grid, MortalEntity.class)) {
			if (mortEnt.getEnergy() <= 0) {
				destroy(mortEnt);
			}
		}

		flushRemoveBuffer();
		flushAddBuffer();
	}


	public boolean isInBounds(AbsPos pos) {
		return grid.isInBounds(pos);
	}

	public Dimension getSize() {
		return grid.getSize();
	}

	@Override
	public String toString() {
		return grid.toString();
	}
}
