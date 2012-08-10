package game;

import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teampg.grid2d.GridInterface;
import teampg.grid2d.GridInterface.Entry;
import teampg.grid2d.ReadGrid;
import teampg.grid2d.RectGrid;
import teampg.grid2d.point.AbsPos;
import teampg.grid2d.point.Pos2D;

import entity.BotEntity;
import entity.DynamicEntity;
import entity.EmptyEntity;
import entity.Entity;
import entity.MortalEntity;

import brain.BrainInfo;




import action.cmd.Move;


/**
 * World is the Entity manager. It keeps track of Entity positions. It is the
 * only thing that keeps Entity references between turns. As such, removing an
 * entity from World using {@link #destroy(AbsPos)} removes it from the game.
 *
 * @author Jackson Williams
 */
public class World implements ReadGrid<Entity>{
	private final GridInterface<Entity> grid;
	private final Map<Integer, AbsPos> botIndex;
	private final Entity TO_APPEAR_OUTSIDE_GRID = Entity.getNewWall();

	/**
	 * Loads and initializes a grid from a map file (*.nbm).
	 *
	 * @param mapFile
	 *            A File path to a valid nanobots map file
	 * @return The initialized Grid from the given map file
	 */
	public static World load(File mapFile) {
		World g = null;

		// TODO load map from file

		return g;
	}

	// TODO consider making private and including a static method to load from a
	// map
	public World(int width, int height) {
		grid = new RectGrid<Entity>(new Dimension(width, height));

		botIndex = new HashMap<Integer, AbsPos>();

		grid.fill(Entity.getNewEmpty());
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
		BotEntity beholder = get(botID);

		int sideLen = Settings.getVisionRadius();
		Collection<AbsPos> visPoints = Pos2D.getDiamondNear(getBotPosition(botID), Settings.getVisionRadius());

		BrainInfo info = new BrainInfo(beholder, this, visPoints);
		return info;
	}

	/**
	 * Get reference to Entity at position p
	 *
	 * @param p
	 *            Position to look
	 * @return Entity at position p
	 */
	@Override
	public Entity get(AbsPos p) {
		if (!grid.isInBounds(p)) {
			return TO_APPEAR_OUTSIDE_GRID;
		}

		Entity found = grid.get(p);
		return found;
	}

	// TODO move to util
	public List<BotEntity> getProxBots(int botID, int squareRadius) {
		ArrayList<BotEntity> bots = new ArrayList<BotEntity>(squareRadius * 2);

		AbsPos centre = getBotPosition(botID);
		RectGrid<Entity> prox = grid.getProximate(centre, squareRadius, squareRadius);

		for (Entity e : prox) {
			if (e instanceof BotEntity) {
				bots.add((BotEntity) e);
			}
		}

		return bots;
	}

	/**
	 * Insert a new Entity into Grid.
	 *
	 * @param target
	 *            Position to put the new entity. Must be empty.
	 * @param toAdd
	 *            New entity to add into grid. Must not already be in grid.
	 */
	public void addNewEntity(AbsPos target, Entity toAdd) {
		// target position for new entity should be empty
		assert (grid.get(target).getClass() == EmptyEntity.class);

		if (toAdd instanceof BotEntity) {
			BotEntity newBot = (BotEntity) toAdd;
			botIndex.put(newBot.getID(), target);
		}

		grid.set(target, toAdd);
	}

	/**
	 * Move a bot between two grid points. Starting position becomes Empty,
	 * target holds bot.
	 *
	 * @param moverID
	 *            ID of the bot to move.
	 * @param target
	 *            Position of the empty cell to move to.
	 */
	public void move(int moverID, AbsPos target) {
		assert(grid.isInBounds(target));
		// target to move to should always be empty
		assert (grid.get(target).getClass() == EmptyEntity.class);
		// move distance should never be more than settings range
		assert (target.x < Settings.getActionRange(Move.class));
		assert (target.y < Settings.getActionRange(Move.class));

		AbsPos origin = botIndex.get(moverID);
		BotEntity mover = (BotEntity) grid.get(origin);

		grid.set(target, mover);
		grid.set(origin, Entity.getNewEmpty());

		botIndex.put(moverID, target);
	}

	/**
	 * Removes Entity at position. Used to kill bots or food. Using on WALL or
	 * EMPTY will cause panic.
	 *
	 * @param i
	 */
	public void destroy(AbsPos i) {
		Entity entToDie = grid.get(i);

		// should never try to remove a non-dying entity
		assert ((entToDie instanceof MortalEntity));

		if (Entity.isType(entToDie, Entity.BOT)) {
			BotEntity botToDie = ((BotEntity) entToDie);
			botIndex.remove(botToDie.getID());
		}


		grid.set(i, Entity.getNewEmpty());
	}

	public void tick() {
		for (Entry<Entity> entry : grid.getEntries()) {
			Entity entity = entry.getContents();
			if (entity instanceof DynamicEntity) {
				((DynamicEntity) entity).tick();
			}
		}//end foreach point
	}
}
