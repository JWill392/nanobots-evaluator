package action;

import java.util.List;

import teampg.grid2d.point.AbsPos;

import entity.BotEntity;
import entity.bot.Message;
import entity.bot.MessageSignal;
import game.Settings;
import game.world.World;

public class TransmitCmd extends RunningAction{
	public final Message msg;

	public TransmitCmd(Message toSend) {
		msg = toSend;
	}

	@Override
	public final void executeAll(World world, List<BotEntity> actors) {
		super.executeAll(world, actors);

		for (BotEntity actor : actors) {
			TransmitCmd action = actor.getRunningAction(TransmitCmd.class);
			AbsPos origin = world.getBotPosition(actor.getID());

			Iterable<BotEntity> teamBotsInMessageRange =
					world.getProxBots(origin, Settings.getMessageRange(), actor.getTeam());

			for (BotEntity receiver : teamBotsInMessageRange) {
					receiver.addReceivedMessage(new MessageSignal(action.msg, origin));
			}

			action.exactCostAndRemoveFrom(actor);
		}
	}

	@Override
	protected int getCost() {
		return Settings.getActionCost(this.getClass());
	}
}
