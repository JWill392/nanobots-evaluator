package brain;

import static com.google.common.base.Preconditions.checkNotNull;
import teampg.grid2d.point.AbsPos;

import action.ActionCmd;
import action.RunningAction;
import action.WaitCmd;

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

	public BrainActionAndMemory decideAction(BrainInfo info) {
		energy = info.getEnergy();
		mem = info.getMemory();
		vision = info.getVision();
		position = info.getPosition();
		msgs = info.getMessages();

		// TODO-DESIGN Sandbox
		BrainActionAndMemory brainAction;

		try {
			brainAction = new BrainActionAndMemory(mem, brainDecideAction());
			checkNotNull(brainAction.cmd);
			checkNotNull(mem);
		} catch (Exception e) {
			//TODO make BotBrain errors visible
			e.printStackTrace(System.err);
			// NOTE, undoes any changes to memory made by brain
			brainAction = new BrainActionAndMemory(info.getMemory(), new WaitCmd());
		}

		return brainAction;
	}

	/**
	 * super.mem will save changes to the bot's memory.
	 */
	protected abstract ActionCmd brainDecideAction()
			throws Exception;

	public static class BrainActionAndMemory {
		public final Memory mem;
		public final RunningAction cmd;

		private BrainActionAndMemory(Memory mem, ActionCmd cmd) {
			this.mem = mem;
			this.cmd = (RunningAction) cmd;
		}
	}
}
