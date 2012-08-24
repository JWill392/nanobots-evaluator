package game;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import matchlog.MatchLog;

import entity.BotEntity;
import game.world.GameMap;
import game.world.MapLoader;
import game.world.World;

import action.RunningAction;
import brain.BrainInfo;
import brain.BotBrain.BrainActionAndMemory;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class Game {
	private final GameMap map;
	private final World world;
	private final ImmutableList<Team> teams;
	private int currTeamIndex;
	private final File replayFile;

	public Game(GameMap map, ImmutableList<Team> teams) {
		this(map, teams, null);
	}

	public Game(GameMap map, ImmutableList<Team> teams, File replayFile) {
		this.replayFile = replayFile;

		world = MapLoader.load(map, teams);
		this.map = map;
		this.teams = teams;
		currTeamIndex = 0;

		MatchLog.startMatch(this);
	}

	public List<Team> getTeams() {
		return teams;
	}

	public GameMap getMap() {
		return map;
	}

	public World getWorld() {
		return world;
	}

	public boolean runNextTurn() {
		Team currTeam = teams.get(currTeamIndex);
		Iterable<BotEntity> teamBots = world.getTeamBots(currTeam);

		//get actions from brain
		for (BotEntity bot : teamBots) {
			BrainInfo info = world.getBotInfo(bot.getID());
			BrainActionAndMemory decidedOutcome = currTeam.decideAction(info);

			bot.setMemory(decidedOutcome.mem);
			if (decidedOutcome.cmd != null) {
				bot.setRunningAction(decidedOutcome.cmd);
			}
		}

		//execute running actions
		for (final RunningAction actionType : Settings.getActionExecutionOrder()) {
			List<BotEntity> botsWithRunningActionType =
					Lists.newLinkedList(
							Iterables.filter(teamBots, new HasRunningActionOfType(actionType.getClass())));

			actionType.executeAll(world, botsWithRunningActionType);
		}

		MatchLog.endTurn();

		//tick all dynamic ents
		world.tick();

		// check lose condition on team
		for (Team team : teams) {
			if (teamHasLivingBots(world, team) == false) {
				team.setLost();
			}
		}

		// determine which team gets next turn
		currTeamIndex = getNextTeam();

		// Game over?
		if (currTeamIndex == -1) {
			// write replay
			if (replayFile != null) {
				try {
					MatchLog.endMatch(getWinner(), replayFile);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// TODO end
			//TODO terrible, fix
			System.out.println("skipping turn since done");
			return false;
		}

		return true;
	}

	private Team getWinner() {
		Iterable<Team> winnerList = Iterables.filter(teams, new Predicate<Team>() {
			@Override
			public boolean apply(Team team) {
				return !team.hasLost();
			}
		});

		if (Iterables.size(winnerList) != 1) {
			return null;
		}

		return Iterables.get(winnerList, 0);
	}

	private static int rotateIndex(int index, int size) {
		return (index + 1) % size;
	}

	private int getNextTeam() {
		int lastTeam = currTeamIndex;
		int possNextTeam = rotateIndex(currTeamIndex, teams.size());
		while (true) {
			if (possNextTeam == lastTeam) {
				return -1;
			}

			if (!teams.get(possNextTeam).hasLost()) {
				return possNextTeam;
			}

			possNextTeam = rotateIndex(possNextTeam, teams.size());
		}
	}

	private static final class HasRunningActionOfType implements Predicate<BotEntity> {
		private final Class<? extends RunningAction> actionType;
		public HasRunningActionOfType(Class<? extends RunningAction> actionType) {
			this.actionType = actionType;
		}

		@Override
		public boolean apply(BotEntity bot) {
			RunningAction runningAction = bot.getRunningAction();
			if (runningAction == null) {
				return false;
			}

			return runningAction.getClass().equals(actionType);
		}
	}

	private static boolean teamHasLivingBots(World world, Team team) {
		return Iterables.size(world.getTeamBots(team)) > 0;
	}
}
