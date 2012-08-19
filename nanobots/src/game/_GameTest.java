package game;
import game.world.MapLoader;
import game.world.World;

import java.util.Scanner;


import org.junit.Before;
import org.junit.Test;

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

	@Before
	public void setUp() throws Exception {
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
		String mapString =
				"00..F\n" +
				".....\n" +
				"F..11";

		world = MapLoader.load(mapString, teamList);
		game = new Game(world, ImmutableList.of(teamA, teamB));
	}

	@Test
	public final void test() {
		try(Scanner continueListener = new Scanner(System.in)){
			while(true) {
				game.runNextTurn();
				System.out.println(world);
				System.out.println("\n------------\n");
				continueListener.nextLine();
			}
		}
	}
}
