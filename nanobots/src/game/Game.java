package game;
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
	private int currentTeam;

	public Game(GameMap map, ImmutableList<Team> teams) {
		world = MapLoader.load(map, teams);
		this.map = map;
		this.teams = teams;
		currentTeam = 0;

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

	public void runNextTurn() {
		Team currTeam = teams.get(currentTeam);
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

		//TODO check lose/win condition, end
		MatchLog.endTurn();

		//tick all dynamic ents
		world.tick();


		currentTeam = (currentTeam + 1) % (teams.size());
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
}
