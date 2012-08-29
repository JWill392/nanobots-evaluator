package game;
import game.world.GameMap;
import game.world.World;

import java.io.File;
import java.util.Scanner;

import matchlog.MatchLog;

import com.google.common.collect.ImmutableList;

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
		Settings.lock();

		teamA = new Team(new BasicBrain(), "teamA");
		teamB = new Team(new BasicBrain(), "teamB");
		ImmutableList<Team> teamList = ImmutableList.of(teamA, teamB);

		GameMap map = new GameMap("PLACEHOLDER",
				"00..F.........\n" +
				"..............\n" +
				"..............\n" +
				"..............\n" +
				"..............\n" +
				".........F..11");

		game = new Game(map, teamList, new File("/home/jackson/testreplay"));
		world = game.getWorld();

		runTest();
	}

	public void runTest() {
		try(Scanner continueListener = new Scanner(System.in)){
			int tc = 0;
			teampg.util.Util.setSeed(4337);
			while (game.runNextTurn()) {
				if (tc == 17) {
					System.out.println("FULL MATCH: " + MatchLog.getMatch());
				}
				System.out.println("TURN #" + tc++);
				/*
				System.out.println("\n------------\n");
				System.out.println(world);
				String line = continueListener.nextLine();
				if (line.equals("done")) {
					MatchLog.endMatch();
				}
				*/
			}
		}
	}
}
