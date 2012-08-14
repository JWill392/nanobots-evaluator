package brain;

import teampg.grid2d.point.AbsPos;

import com.google.common.collect.ImmutableList;

import entity.bot.Memory;
import entity.bot.MessageSignal;


public abstract class BotBrain {
	protected int energy;
	protected Memory mem;
	protected Vision vision;
	protected AbsPos position;
	protected ImmutableList<MessageSignal> msgs;

	public BotBrain() {
	}

	public BrainCommand decideAction(BrainInfo info) {
		energy = info.getEnergy();
		mem = info.getMemory();
		vision = info.getVision();
		position = info.getPosition();
		msgs = info.getMessages();

		// TODO-DESIGN Sandbox
		BrainCommand brainAction;

		try {
			brainAction = brainDecideAction();
		} catch (Exception e) {
			//TODO make BotBrain errors visible
			e.printStackTrace(System.err);
			brainAction = new BrainCommand(null, null);
		}

		return brainAction;
	}

	protected abstract BrainCommand brainDecideAction()
			throws Exception;
}
