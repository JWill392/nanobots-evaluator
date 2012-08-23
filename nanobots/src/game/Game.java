package game;
import java.util.List;

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
			bot.addRunningAction(decidedOutcome.cmd);
		}

		//execute running actions
		for (final RunningAction actionType : Settings.getActionExecutionOrder()) {
			List<BotEntity> botsWithRunningActionType =
					Lists.newLinkedList(
							Iterables.filter(teamBots, new HasRunningAction(actionType.getClass())));

			actionType.executeAll(world, botsWithRunningActionType);
		}

		//tick all dynamic ents
		world.tick();

		//TODO check lose/win condition, end

		currentTeam = (currentTeam + 1) % (teams.size());
	}

	private static final class HasRunningAction implements Predicate<BotEntity> {
		private final Class<? extends RunningAction> actionType;
		public HasRunningAction(Class<? extends RunningAction> actionType) {
			this.actionType = actionType;
		}

		@Override
		public boolean apply(BotEntity bot) {
			return bot.hasRunningAction(actionType);
		}
	}
}
