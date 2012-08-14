package game;
import game.world.MapLoader;
import game.world.World;

import java.util.List;
import java.util.Scanner;


import org.junit.Before;
import org.junit.Test;

import teampg.grid2d.point.AbsPos;
import teampg.grid2d.point.Pos2D;
import teampg.grid2d.point.RelPos;
import teampg.util.Util;

import com.google.common.collect.ImmutableList;

import action.HarvestCmd;
import action.MoveCmd;
import action.WaitCmd;
import brain.BotBrain;
import brain.BrainCommand;
import brain.BrainUtil;
import brain.Vision;


public class _GameTest {
	Game game;
	World world;
	Team teamA;
	Team teamB;

	@Before
	public void setUp() throws Exception {
		Settings.load();
		Settings.setNewbornEnergy(10);
		Settings.setActionCost(MoveCmd.class, 1);
		Settings.setActionRange(MoveCmd.class, 1);
		Settings.setVisionRadius(2);
		Settings.lock();


		teamA = new Team(new BotBrain() {

			@Override
			protected BrainCommand brainDecideAction() throws Exception {

				List<RelPos> foodPos = vision.indexOf(Vision.FOOD);
				if (!foodPos.isEmpty()) {
					RelPos bestFoodPos = foodPos.get(0);
					AbsPos bestFoodPosAbs = Pos2D.offset(position, bestFoodPos);

					if (bestFoodPos.squareMagnitude() == 1) {
						return new BrainCommand(new HarvestCmd(bestFoodPosAbs));
					}

					AbsPos moveTowardsFood =
							BrainUtil.getMoveTowards(position, bestFoodPos);

					return new BrainCommand(new MoveCmd(moveTowardsFood));
				}

				return new BrainCommand(new MoveCmd(Pos2D.offset(position, Util.choice(RelPos.UP, RelPos.RIGHT, RelPos.DOWN, RelPos.LEFT))));
			}
		}, "teamA");

		teamB = new Team(new BotBrain() {

			@Override
			protected BrainCommand brainDecideAction() throws Exception {
				// TODO Auto-generated method stub
				return new BrainCommand(new WaitCmd());
			}
		}, "teamB");

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
