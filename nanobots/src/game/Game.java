package game;
import entity.BotEntity;
import entity.bot.Memory;
import game.world.World;

import action.RunningAction;
import brain.BrainCommand;
import brain.BrainInfo;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

public class Game {
	private final World world;
	private final ImmutableList<Team> teams;
	private int currentTeam;

	public Game(World world, ImmutableList<Team> teams) {
		this.world = world;
		this.teams = teams;
		currentTeam = 0;
	}

	public void runNextTurn() {
		Team currTeam = teams.get(currentTeam);
		Iterable<BotEntity> teamBots = world.getTeamBots(currTeam);

		//get actions from brain
		for (BotEntity bot : teamBots) {
			BrainInfo info = world.getBotInfo(bot.getID());
			BrainCommand decidedOutcome = currTeam.decideAction(info);

			Memory desiredNewMem = decidedOutcome.getMemory();
			if (desiredNewMem != null) {
				bot.setMemory(desiredNewMem);
			}
			bot.addRunningAction(decidedOutcome.getAction());
		}

		//execute running actions
		for (final RunningAction actionType : Settings.getActionExecutionOrder()) {
			Iterable<BotEntity> botsWithRunningActionType =
					Iterables.filter(teamBots, new HasRunningAction(actionType.getClass()));

			actionType.executeAll(world, botsWithRunningActionType);
		}

		//tick all dynamic ents
		world.tick();

		//TODO check lose/win condition

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
