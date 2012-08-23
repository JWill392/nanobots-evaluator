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
import game.Game;
import game.Settings;
import game.Team;
import game.world.GameMap;
import game.world.World;

public class _TransferCmdTest {
	private static final int TRANSFER_COST = 1;
	private static final int TRANSFER_AMOUNT = 3;
	private static final int NEWBORN = 10;
	Team transferTeam;
	Team otherTeam;
	World world;
	Game game;

	@Before
	public void setUp() throws Exception {
		Settings.load();
		Settings.setNewbornEnergy(NEWBORN);
		Settings.setBotMaxEnergy(10000);
		Settings.setActionCost(TransferCmd.class, TRANSFER_COST);
		Settings.setActionRange(TransferCmd.class, 1);
		Settings.setActionCost(WaitCmd.class, 0);
		Settings.lock();
	}

	private final void setUpBasicTest(String mapString) {
		//attackTeam bots always try to attack right
		transferTeam = new Team(new BotBrain() {
			@Override
			protected ActionCmd brainDecideAction() throws Exception {
				return new TransferCmd(Pos2D.offset(position, RelPos.RIGHT), TRANSFER_AMOUNT);
			}
		}, "transferTeam");
		otherTeam = _MoveCmdTest.getMockTeam(); //always waits
		ImmutableList<Team> teams = ImmutableList.of(transferTeam, otherTeam);

		game = new Game(new GameMap("PLACEHOLDER", mapString), teams);
		world = game.getWorld();
	}

	@Test
	public final void testBasicValidTarget() {
		setUpBasicTest("00");

		AbsPos senderPos = AbsPos.of(0, 0);
		BotEntity senderBot = (BotEntity) world.get(senderPos);
		assertEquals(NEWBORN, senderBot.getEnergy());

		AbsPos receiverPos = AbsPos.of(1, 0);
		BotEntity receiverBot = (BotEntity) world.get(receiverPos);
		assertEquals(NEWBORN, receiverBot.getEnergy());

		// TURN 0

		game.runNextTurn();
		assertEquals(NEWBORN - TRANSFER_COST - TRANSFER_AMOUNT, senderBot.getEnergy());
		assertEquals(NEWBORN + TRANSFER_AMOUNT, receiverBot.getEnergy());
	}

	@Test
	public final void testBasicIllegalTarget() {
		setUpBasicTest("0.");

		AbsPos senderPos = AbsPos.of(0, 0);
		BotEntity senderBot = (BotEntity) world.get(senderPos);
		assertEquals(NEWBORN, senderBot.getEnergy());

		// TURN 0

		game.runNextTurn();
		assertEquals(NEWBORN, senderBot.getEnergy());
	}

	@Test
	public final void testEnemyTargetFails() {
		setUpBasicTest("01");

		AbsPos senderPos = AbsPos.of(0, 0);
		BotEntity senderBot = (BotEntity) world.get(senderPos);
		assertEquals(NEWBORN, senderBot.getEnergy());

		AbsPos receiverPos = AbsPos.of(1, 0);
		BotEntity receiverBot = (BotEntity) world.get(receiverPos);
		assertEquals(NEWBORN, receiverBot.getEnergy());

		// TURN 0

		game.runNextTurn();
		assertEquals(NEWBORN, senderBot.getEnergy());
		assertEquals(NEWBORN, receiverBot.getEnergy());
	}
}
