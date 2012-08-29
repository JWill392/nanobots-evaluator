package action;

import java.util.List;

import com.google.common.collect.ImmutableList;


import replay.ReplayProto.Replay;
import replay.ReplayProto.Replay.Action.Type;
import static replay.ReplayProto.Replay.Entity.BotState;
import teampg.grid2d.point.AbsPos;

import entity.BotEntity;
import entity.bot.Message;
import entity.bot.MessageSignal;
import game.Settings;
import game.world.World;

public class BroadcastCmd extends RunningAction{
	public final Message msg;

	public BroadcastCmd(Message toSend) {
		super();

		msg = toSend;
		data.setBroadcastMessage(msg.getAll());
	}

	static void executeAll(World world, List<BotEntity> actors) {
		filterBasicInvalid(world, actors);

		for (BotEntity actor : actors) {
			BroadcastCmd action = (BroadcastCmd) actor.getRunningAction();
			AbsPos origin = world.getBotPosition(actor.getID());

			Iterable<BotEntity> teamBotsInMessageRange =
					world.getProxBots(origin, Settings.getMessageRange(), actor.getTeam());

			for (BotEntity receiver : teamBotsInMessageRange) {
					receiver.addReceivedMessage(new MessageSignal(action.msg, origin));
			}

			action.succeed(actor);
		}
	}

	@Override
	protected int getCost() {
		return Settings.getActionCost(this.getClass());
	}

	@Override
	public Type getType() {
		return Replay.Action.Type.BROADCAST;
	}

	@Override
	protected ImmutableList<BotState> getLegalActorStates() {
		return ImmutableList.of(
				BotState.NORMAL,
				BotState.GESTATING);
	}
}
