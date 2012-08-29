package action;

import java.util.Iterator;
import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;


import replay.ReplayProto.Replay;
import replay.ReplayProto.Replay.Action.Outcome;
import replay.ReplayProto.Replay.Action.Type;
import replay.ReplayProto.Replay.Entity.BotState;

import entity.BotEntity;
import game.world.World;

public abstract class RunningAction implements ActionCmd {
	protected final Replay.Action.Builder data;
	public Outcome debugOutcome; // TODO REMOVE

	public RunningAction() {
		data = Replay.Action.newBuilder()
				.setType(getType());
	}

	/**
	 * Removes unaffordable actions from <i>actors</i>
	 */
	static void filterBasicInvalid(World world, Iterable<BotEntity> actors) {
		for (Iterator<BotEntity> iter = actors.iterator(); iter.hasNext();) {
			BotEntity bot = iter.next();

			RunningAction action = bot.getRunningAction();

			// can't afford
			if (!action.canAfford(bot)) {
				action.fail(Replay.Action.Outcome.INSUFFICIENT_ENERGY);
				iter.remove();
				continue;
			}

			// check bot in correct state
			if (!action.getLegalActorStates().contains(bot.getState())) {
				action.fail(Replay.Action.Outcome.WRONG_BOT_STATE);
				iter.remove();
				continue;
			}
		}
	}

	public final void fail(Replay.Action.Outcome reason) {
		assert reason != Replay.Action.Outcome.SUCCESS;
		assert data.hasOutcome() == false;

		debugOutcome = reason;
		data.setOutcome(reason);
	}

	public final boolean canAfford(BotEntity actor) {
		return getCost() < actor.getEnergy();
	}

	public final void succeed(BotEntity actor) {
		assert canAfford(actor);
		assert data.hasOutcome() == false;

		actor.addEnergy(-getCost());
		debugOutcome = Outcome.SUCCESS;
		data.setOutcome(Replay.Action.Outcome.SUCCESS);
	}

	/**
	 * Amount bot is required to pay to execute this action.  Should be positive.
	 */
	protected abstract int getCost();
	protected abstract ImmutableList<BotState> getLegalActorStates();
	public abstract Type getType();

	public Replay.Action getData() {
		return (data.clone()).build();
	}

	public static void executeAll(Class<? extends RunningAction> actionType, Iterable<BotEntity> teamBots,
			World world) {
		List<BotEntity> botsWithRunningActionType =
				Lists.newLinkedList(
						Iterables.filter(teamBots, new HasRunningActionOfType(actionType)));

		if (actionType.equals(AttackCmd.class)) {
			AttackCmd.executeAll(world, botsWithRunningActionType);
		} else if (actionType.equals(BirthCmd.class)) {
			BirthCmd.executeAll(world, botsWithRunningActionType);
		} else if (actionType.equals(BroadcastCmd.class)) {
			BroadcastCmd.executeAll(world, botsWithRunningActionType);
		} else if (actionType.equals(ConceiveCmd.class)) {
			ConceiveCmd.executeAll(world, botsWithRunningActionType);
		} else if (actionType.equals(HarvestCmd.class)) {
			HarvestCmd.executeAll(world, botsWithRunningActionType);
		} else if (actionType.equals(MoveCmd.class)) {
			MoveCmd.executeAll(world, botsWithRunningActionType);
		} else if (actionType.equals(TransferCmd.class)) {
			TransferCmd.executeAll(world, botsWithRunningActionType);
		} else if (actionType.equals(WaitCmd.class)) {
			WaitCmd.executeAll(world, botsWithRunningActionType);
		} else {
			throw new UnsupportedOperationException("Need to add " + actionType + " to this silly switch statement");
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

}
