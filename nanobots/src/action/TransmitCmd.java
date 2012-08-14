package action;

import entity.BotEntity;
import entity.bot.Message;
import game.world.World;

public class TransmitCmd extends RunningAction{
	private final Message msg;

	public TransmitCmd(Message toSend) {
		msg = toSend;
	}

	public Message getMessage() {
		return (Message) msg.clone();
	}

	@Override
	public void executeAll(World world, Iterable<BotEntity> actors) {
		// TODO Auto-generated method stub

	}
}
