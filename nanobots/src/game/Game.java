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
import action.WaitCmd;
import brain.BrainInfo;
import brain.BotBrain.BrainActionAndMemory;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

public class Game {
	private static final int MAX_TURNS = 5000;
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

		// log world state when brains get their input
		MatchLog.logWorldState();

		// SET ACTIONS - given info, bots decide what they want to do this turn.
		for (BotEntity bot : world) {
			BrainInfo info = world.getBotInfo(bot.getID());
			BrainActionAndMemory brainDecision = bot.getTeam().decideAction(info);

			if (brainDecision.mem != null) {
				bot.setMemory(brainDecision.mem);
			}
			if (brainDecision.cmd != null) {
				bot.setRunningAction(brainDecision.cmd);
			} else {
				bot.setRunningAction(new WaitCmd());
			}
		}

		//execute actions just proposed
		for (Class<? extends RunningAction> actionType : Settings.getActionExecutionOrder()) {
			RunningAction.executeAll(actionType, world, world);
		}

		// log the actions brains proposed, and whether they succeeded.
		MatchLog.logActionsAndOutcomes();


		//##########################################
		//# clean up dead bots, check if game over #
		//##########################################

		// kill dead ents, clear actions and outcomes, etc.
		world.tick();

		// check lose condition on team
		for (Team team : teams) {
			if (teamHasLivingBots(world, team) == false) {
				team.setLost();
			}
		}

		// determine which team gets next turn
		currTeamIndex = getNextTeam();

		// Game too long.. tie
		if (MatchLog.getTurnCount() > MAX_TURNS) {
			currTeamIndex = -1;
		}

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
			//TODO terrible, fix
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

	private static boolean teamHasLivingBots(World world, Team team) {
		return Iterables.size(world.getTeamBots(team)) > 0;
	}
}
