package action;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import entity.BotEntity;
import entity.EmptyEntity;
import entity.bot.Memory;
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
import brain.BrainCommand;
import brain.BrainInfo;

public class _AttackCmdTest {
	private static final int ATTACK_COST = 1;
	private static final int ATTACK_DAMAGE = 2;
	private static final int NEWBORN = 3;
	Team attackTeam;
	Team defenceTeam;
	World world;
	Game game;

	//basic tests
	BotEntity basicAttacker;
	BotEntity basicDefender;

	@Before
	public void setUp() throws Exception {
		Settings.load();
		Settings.setNewbornEnergy(NEWBORN);
		Settings.setAttackDamage(ATTACK_DAMAGE);
		Settings.setActionCost(AttackCmd.class, ATTACK_COST);
		Settings.setActionRange(AttackCmd.class, 1);
		Settings.lock();
	}

	private final void setUpBasicTest(RelPos attackTarget, String mapString) {
		attackTeam = getRelativeAttackerTeam(attackTarget);
		defenceTeam = getMockTeam(new WaitCmd());
		ImmutableList<Team> teams = ImmutableList.of(attackTeam, defenceTeam);

		world = MapLoader.load(mapString, teams);
		game = new Game(world, teams);

		basicAttacker = (BotEntity) world.get(AbsPos.of(1, 0));
		basicDefender = (BotEntity) world.get(AbsPos.of(1, 1));
		assertEquals(NEWBORN, basicAttacker.getEnergy());
		assertEquals(NEWBORN, basicDefender.getEnergy());
	}

	@Test
	public final void testBasicValidTarget() {
		setUpBasicTest(RelPos.DOWN,
				".0.\n"+
				".1.");

		// TURN 0

		game.runNextTurn();
		assertEquals(NEWBORN - ATTACK_COST, basicAttacker.getEnergy());
		assertEquals(NEWBORN - ATTACK_DAMAGE, basicDefender.getEnergy());

		game.runNextTurn(); //run defender turn
		game.runNextTurn();
		assertEquals(NEWBORN - ATTACK_COST * 2, basicAttacker.getEnergy());
		assertTrue(world.get(AbsPos.of(1, 1)) instanceof EmptyEntity);
	}

	@Test
	public final void testBasicIllegalTarget() {
		setUpBasicTest(RelPos.RIGHT,
				".0.\n"+
				".1.");

		// TURN 0

		game.runNextTurn();
		assertEquals(NEWBORN, basicAttacker.getEnergy());
		assertEquals(NEWBORN, basicDefender.getEnergy());
	}

	public static Team getMockTeam(ActionCmd actionToAlwaysTake) {
		Team retTeam = mock(Team.class);
		when(retTeam.decideAction(any(BrainInfo.class)))
		.thenReturn(new BrainCommand(
				actionToAlwaysTake, mock(Memory.class)));

		return retTeam;
	}

	//TODO eliminate copy/pasted code
	public static Team getRelativeAttackerTeam(final RelPos attackDir) {
		return new Team(new BotBrain() {
			@Override
			protected BrainCommand brainDecideAction() throws Exception {
				return new BrainCommand(new AttackCmd(Pos2D.offset(position, attackDir)), new Memory());
			}
		}, "Attacker");
	}
}
