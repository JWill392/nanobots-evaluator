package action;

import java.util.List;

import com.google.common.collect.ImmutableList;


import replay.ReplayProto.Replay;
import replay.ReplayProto.Replay.Action.Type;
import replay.ReplayProto.Replay.Entity.BotState;

import entity.BotEntity;
import game.Settings;
import game.world.World;

/**
 * Dummy action that does nothing.  Demonstrates basic requirements for every RunningAction.
 */
public class WaitCmd extends RunningAction{
	public WaitCmd() {
		super();
	}

	static void executeAll(World world, List<BotEntity> actors) {
		filterBasicInvalid(world, actors); // validates for common things; eg cost.

		for (BotEntity bot : actors) {
			WaitCmd action = (WaitCmd) bot.getRunningAction(); //get the waitaction this actor is trying to execute
			action.succeed(bot); //and just execute it.  No need to validate; anyone can run this action, provided they can afford the cost.
		}
	}

	@Override
	protected int getCost() {
		return Settings.getActionCost(this.getClass());
	}

	@Override
	public Type getType() {
		return Replay.Action.Type.WAIT;
	}

	@Override
	protected ImmutableList<BotState> getLegalActorStates() {
		// bots are allowed to wait no matter their current situation.
		return ImmutableList.of(BotState.NORMAL, BotState.GESTATING);
	}
}
