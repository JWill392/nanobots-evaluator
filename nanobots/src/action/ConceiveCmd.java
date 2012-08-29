package action;

import java.util.List;

import com.google.common.collect.ImmutableList;

import entity.BotEntity;
import game.Settings;
import game.world.World;
import replay.ReplayProto.Replay;
import replay.ReplayProto.Replay.Action.Type;
import replay.ReplayProto.Replay.Entity.BotState;

public class ConceiveCmd extends RunningAction {

	public ConceiveCmd() {
		super();

	}

	@Override
	protected int getCost() {
		return Settings.getActionCost(ConceiveCmd.class);
	}

	@Override
	public Type getType() {
		return Replay.Action.Type.CONCEIVE;
	}

	static void executeAll(World world, List<BotEntity> actors) {
		filterBasicInvalid(world, actors);

		for (BotEntity bot : actors) {
			RunningAction action = bot.getRunningAction();

			bot.setState(Replay.Entity.BotState.GESTATING);
			action.succeed(bot);
		}
	}

	@Override
	protected ImmutableList<BotState> getLegalActorStates() {
		return ImmutableList.of(BotState.NORMAL);
	}
}
