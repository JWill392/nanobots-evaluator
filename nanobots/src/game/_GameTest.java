package game;
import game.world.GameMap;
import game.world.World;

import java.io.File;
import java.util.Scanner;

import com.google.common.collect.ImmutableList;

import brain.demo.BasicBrain;
import brain.demo.communalbot.CommunalBrain;


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
		teamB = new Team(new CommunalBrain(), "teamB");
		ImmutableList<Team> teamList = ImmutableList.of(teamA, teamB);

		GameMap map = new GameMap("PLACEHOLDER",
						"00..F..F......................................\n" +
						"00..F..F......................................\n" +
						"00..F..F......................................\n" +
						"..............................................\n" +
						"..............................................\n" +
						"..............................................\n" +
						"..............................................\n" +
						"..............................................\n" +
						"..............................................\n" +
						"..............................................\n" +
						"..............................................\n" +
						"..............................................\n" +
						"..............................................\n" +
						"..............................................\n" +
						"..............................................\n" +
						"..............................................\n" +
						"..............................................\n" +
						"..............................................\n" +
						"..............................................\n" +
						"......................................F..F..11\n" +
						"......................................F..F..11\n" +
						"......................................F..F..11"
						);

		game = new Game(map, teamList, new File("C:\\Users\\jack\\Documents\\nanobots\\testreplay"));
		world = game.getWorld();

		runTest();
	}

	public void runTest() {
		try(Scanner continueListener = new Scanner(System.in)){
			int tc = 0;
			teampg.util.Util.setSeed(4337);
			while (game.runNextTurn()) {
				tc++;
				if (tc % 10 == 0) {
					System.out.println("TURN #: " + tc);
				}
			}
		}
	}
}
