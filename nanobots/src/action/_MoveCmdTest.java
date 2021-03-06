package action;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import teampg.grid2d.point.AbsPos;
import teampg.grid2d.point.Pos2D;
import teampg.grid2d.point.RelPos;
import brain.BotBrain;
import brain.Vision;

import com.google.common.collect.ImmutableList;

import entity.BotEntity;
import game.Game;
import game.Settings;
import game.Team;
import game.world.GameMap;
import game.world.World;

public class _MoveCmdTest {
	private static final int MOVE_COST = 1;
	private static final int NEWBORN = 3;
	Team attackTeam;
	Team defenceTeam;
	World world;
	Game game;

	@Before
	public void setUp() throws Exception {
		Settings.load();
		Settings.setBotMaxEnergy(100);
		Settings.setNewbornEnergy(NEWBORN);
		Settings.setActionCost(MoveCmd.class, MOVE_COST);
		Settings.setActionRange(MoveCmd.class, 1);
		Settings.lock();
	}

	private final void setUpBasicTest(final RelPos attackTarget, String mapString) {
		attackTeam = getRelativeMoverTeam(attackTarget);
		defenceTeam = getMockTeam();
		ImmutableList<Team> teams = ImmutableList.of(attackTeam, defenceTeam);

		game = new Game(new GameMap("PLACEHOLDER", mapString), teams);
		world = game.getWorld();
	}

	@Test
	public final void testBasicIllegalTarget() {
		setUpBasicTest(RelPos.DOWN,
				".0.\n"+
				".1.");

		// TURN 0
		BotEntity mover = (BotEntity) world.get(AbsPos.of(1, 0));
		BotEntity waiter = (BotEntity) world.get(AbsPos.of(1, 1));
		assertEquals(NEWBORN, mover.getEnergy());
		assertEquals(NEWBORN, waiter.getEnergy());

		game.runNextTurn();
		assertEquals(mover, world.get(AbsPos.of(1, 0)));
		assertEquals(NEWBORN, mover.getEnergy());
	}

	@Test
	public final void testBasicValidTarget() {
		setUpBasicTest(RelPos.RIGHT,
				".0.\n"+
				".1.");

		// TURN 0
		BotEntity mover = (BotEntity) world.get(AbsPos.of(1, 0));

		game.runNextTurn();
		assertEquals(NEWBORN - MOVE_COST, mover.getEnergy());
		assertEquals(mover, world.get(AbsPos.of(2, 0)));

		game.runNextTurn(); // waiter turn
		game.runNextTurn();
		assertEquals(NEWBORN - MOVE_COST, mover.getEnergy());
		assertEquals(mover, world.get(AbsPos.of(2, 0)));

	}

	@Test
	public final void testBasicMultipleValid() {
		setUpBasicTest(RelPos.RIGHT,
				"0..1\n"+
				"00.#");

		BotEntity top = (BotEntity) world.get(AbsPos.of(0, 0));
		BotEntity bottomLeft = (BotEntity) world.get(AbsPos.of(0, 1));
		BotEntity bottomRight = (BotEntity) world.get(AbsPos.of(1, 1));

		System.out.println(world);
		game.runNextTurn();
		assertEquals(top, world.get(AbsPos.of(1, 0)));
		assertEquals(bottomLeft, world.get(AbsPos.of(1, 1)));
		assertEquals(bottomRight, world.get(AbsPos.of(2, 1)));

		System.out.println(world);
		game.runNextTurn(); // waiter turn
		System.out.println(world);
		game.runNextTurn();
		System.out.println(world);
		assertEquals(top, world.get(AbsPos.of(2, 0)));
		assertEquals(bottomLeft, world.get(AbsPos.of(1, 1)));
		assertEquals(bottomRight, world.get(AbsPos.of(2, 1)));

	}

	@Test
	public final void testPairMoveIntoEachOther() {
		attackTeam = new Team(new BotBrain() {

			@Override
			protected ActionCmd brainDecideAction() throws Exception {
				if (vision.get(RelPos.RIGHT) == Vision.FRIENDLY_BOT) {
					return new MoveCmd(Pos2D.offset(position, RelPos.RIGHT));
				}
				if (vision.get(RelPos.LEFT) == Vision.FRIENDLY_BOT) {
					return new MoveCmd(Pos2D.offset(position, RelPos.LEFT));
				}
				return new WaitCmd();
			}
		}, "attackTeam");
		defenceTeam = getMockTeam();
		ImmutableList<Team> teams = ImmutableList.of(attackTeam, defenceTeam);

		game = new Game(new GameMap("PLACEHOLDER", ".00."), teams);
		world = game.getWorld();

		BotEntity left = (BotEntity) world.get(AbsPos.of(1, 0));
		BotEntity right = (BotEntity) world.get(AbsPos.of(2, 0));

		game.runNextTurn();
		assertEquals(left, world.get(AbsPos.of(2, 0)));
		assertEquals(right, world.get(AbsPos.of(1, 0)));
	}

	public static Team getMockTeam() {
		Team retTeam = new Team(new BotBrain() {
			@Override
			protected ActionCmd brainDecideAction() throws Exception {
				// TODO Auto-generated method stub
				return new WaitCmd();
			}
		}, "MockTeam");

		return retTeam;
	}

	public static Team getRelativeMoverTeam(final RelPos attackDir) {
		return new Team(new BotBrain() {
			@Override
			protected ActionCmd brainDecideAction() throws Exception {
				return new MoveCmd(Pos2D.offset(position, attackDir));
			}
		}, "Mover");
	}
}
