package action;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import teampg.grid2d.point.AbsPos;
import teampg.grid2d.point.Pos2D;
import teampg.grid2d.point.RelPos;
import brain.BotBrain;

import com.google.common.collect.ImmutableList;

import entity.BotEntity;
import entity.EmptyEntity;
import entity.FoodEntity;
import entity.bot.Memory;
import game.Game;
import game.Settings;
import game.Team;
import game.world.MapLoader;
import game.world.World;

public class _ReproduceCmdTest {
	private static final int REPRODUCE_COST = 1;
	private static final int NEWBORN = 3;
	Team collisionTeam;
	World world;
	Game game;

	@Before
	public void setUp() throws Exception {
		Settings.load();
		Settings.setNewbornEnergy(NEWBORN);
		Settings.setActionCost(ReproduceCmd.class, REPRODUCE_COST);
		Settings.setActionRange(ReproduceCmd.class, 1);
		Settings.setMemorySize(1);
		Settings.lock();
	}

	private final void setUpBasicTest(String mapString) {
		collisionTeam = new Team(new BotBrain() {

			@Override
			protected ActionCmd brainDecideAction() throws Exception {
				return new ReproduceCmd(Pos2D.offset(position, RelPos.RIGHT), new Memory(0b1));
			}
		}, "ReproduceTestTeam");
		ImmutableList<Team> teams = ImmutableList.of(collisionTeam);

		world = MapLoader.load(mapString, teams);
		game = new Game(world, teams);
	}

	@Test
	public final void testBasicValidTarget() {
		setUpBasicTest("0.");

		BotEntity reproducerBot = (BotEntity) world.get(AbsPos.of(0, 0));

		game.runNextTurn();
		assertEquals(NEWBORN - REPRODUCE_COST, reproducerBot.getEnergy());

		BotEntity newbornBot = (BotEntity) world.get(AbsPos.of(1, 0));
		assertEquals(NEWBORN, newbornBot.getEnergy());
		assertEquals(reproducerBot.getTeam(), newbornBot.getTeam());
		assertEquals(new Memory(0b1), newbornBot.getMemory());
	}

	@Test
	public final void testBasicIllegalTarget() {
		setUpBasicTest("0F");

		BotEntity reproducerBot = (BotEntity) world.get(AbsPos.of(0, 0));

		game.runNextTurn();
		assertEquals(NEWBORN, reproducerBot.getEnergy());

		assertTrue(world.get(AbsPos.of(1, 0)) instanceof FoodEntity);
	}

	@Test
	public final void testOverlappingTargetsFail() {
		final AbsPos target = AbsPos.of(1, 0);

		collisionTeam = new Team(new BotBrain() {
			@Override
			protected ActionCmd brainDecideAction() throws Exception {
				return new ReproduceCmd(target, new Memory(0b1));
			}
		}, "ReproduceTestTeam");
		ImmutableList<Team> teams = ImmutableList.of(collisionTeam);

		world = MapLoader.load("0.0\n.0.", teams);
		game = new Game(world, teams);

		BotEntity leftReproducerBot = (BotEntity) world.get(AbsPos.of(0, 0));
		BotEntity rightReproducerBot = (BotEntity) world.get(AbsPos.of(2, 0));
		BotEntity bottomReproducerBot = (BotEntity) world.get(AbsPos.of(1, 1));

		game.runNextTurn();
		assertEquals(NEWBORN, leftReproducerBot.getEnergy());
		assertEquals(NEWBORN, rightReproducerBot.getEnergy());
		assertEquals(NEWBORN, bottomReproducerBot.getEnergy());

		assertTrue(world.get(target) instanceof EmptyEntity);
	}
}
