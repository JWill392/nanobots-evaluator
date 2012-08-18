package action;

import static org.junit.Assert.*;
import entity.BotEntity;
import entity.EmptyEntity;
import game.Game;
import game.Settings;
import game.Team;
import game.world.MapLoader;
import game.world.World;


import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import teampg.grid2d.point.AbsPos;
import teampg.grid2d.point.Pos2D;
import teampg.grid2d.point.RelPos;

import brain.BotBrain;

public class _AttackCmdTest {
	private static final int ATTACK_COST = 1;
	private static final int ATTACK_DAMAGE = 2;
	private static final int NEWBORN = 3;
	Team attackTeam;
	Team defenceTeam;
	World world;
	Game game;

	@Before
	public void setUp() throws Exception {
		Settings.load();
		Settings.setNewbornEnergy(NEWBORN);
		Settings.setAttackDamage(ATTACK_DAMAGE);
		Settings.setActionCost(AttackCmd.class, ATTACK_COST);
		Settings.setActionCost(WaitCmd.class, 0);
		Settings.setActionRange(AttackCmd.class, 1);
		Settings.lock();
	}

	private final void setUpBasicTest(String mapString) {
		//attackTeam bots always try to attack right
		attackTeam = new Team(new BotBrain() {
			@Override
			protected ActionCmd brainDecideAction() throws Exception {
				return new AttackCmd(Pos2D.offset(position, RelPos.RIGHT));
			}
		}, "attackTeam");
		defenceTeam = _MoveCmdTest.getMockTeam(); //defenceTeam always waits
		ImmutableList<Team> teams = ImmutableList.of(attackTeam, defenceTeam);

		world = MapLoader.load(mapString, teams);
		game = new Game(world, teams);
	}

	@Test
	public final void testBasicValidTarget() {
		setUpBasicTest("01");

		AbsPos attackerPos = AbsPos.of(0, 0);
		BotEntity attackerBot = (BotEntity) world.get(attackerPos);
		assertEquals(NEWBORN, attackerBot.getEnergy());

		AbsPos defenderPos = AbsPos.of(1, 0);
		BotEntity defenderBot = (BotEntity) world.get(defenderPos);
		assertEquals(NEWBORN, defenderBot.getEnergy());

		// TURN 0

		game.runNextTurn();
		assertEquals(NEWBORN - ATTACK_COST, attackerBot.getEnergy());
		assertEquals(NEWBORN - ATTACK_DAMAGE, defenderBot.getEnergy());

		game.runNextTurn(); //run defender turn
		game.runNextTurn();
		assertEquals(NEWBORN - ATTACK_COST * 2, attackerBot.getEnergy());
		assertTrue(world.get(defenderPos) instanceof EmptyEntity); //defender died
	}

	@Test
	public final void testBasicIllegalTarget() {
		setUpBasicTest("0.");

		AbsPos attackPos = AbsPos.of(0, 0);
		BotEntity basicAttacker = (BotEntity) world.get(attackPos);
		assertEquals(NEWBORN, basicAttacker.getEnergy());

		// TURN 0

		game.runNextTurn();
		assertEquals(NEWBORN, basicAttacker.getEnergy());
	}
}
