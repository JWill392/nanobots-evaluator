package game;
import game.world.GameMap;
import game.world.World;

import java.util.Scanner;

import matchlog.MatchLog;

import com.google.common.collect.ImmutableList;

import action.AttackCmd;
import action.HarvestCmd;
import action.MoveCmd;
import action.ReproduceCmd;
import brain.demo.BasicBrain;


public class _GameTest {

	Game game;
	World world;
	Team teamA;
	Team teamB;

	public static void main (String[] args){
		new _GameTest();
	}

	public _GameTest() {
		Settings.load();
		Settings.setNewbornEnergy(5);
		Settings.setActionCost(ReproduceCmd.class, 10);
		Settings.setActionCost(AttackCmd.class, 0);
		Settings.setActionRange(AttackCmd.class, 1);
		Settings.setActionCost(HarvestCmd.class, 0);
		Settings.setActionRange(HarvestCmd.class, 1);
		Settings.setActionCost(MoveCmd.class, 0);
		Settings.setActionRange(MoveCmd.class, 1);
		Settings.setAttackDamage(1);
		Settings.setBotMaxEnergy(5);
		Settings.setVisionRadius(2);
		Settings.lock();

		teamA = new Team(new BasicBrain(), "teamA");
		teamB = new Team(new BasicBrain(), "teamB");
		ImmutableList<Team> teamList = ImmutableList.of(teamA, teamB);

		GameMap map = new GameMap("PLACEHOLDER",
				"00..F\n" +
				".....\n" +
				"F..11");

		game = new Game(map, teamList);
		world = game.getWorld();

		runTest();
	}

	public void runTest() {
		try(Scanner continueListener = new Scanner(System.in)){
			while(true) {
				game.runNextTurn();
				System.out.println("\n------------\n");
				System.out.println(world);
				String line = continueListener.nextLine();
				if (line.equals("done")) {
					MatchLog.endMatch();
				}
			}
		}
	}
}
