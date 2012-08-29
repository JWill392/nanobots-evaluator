package brain;

import replay.ReplayProto.Replay;
import replay.ReplayProto.Replay.Entity.BotState;
import teampg.grid2d.point.AbsPos;

import action.ActionCmd;
import action.RunningAction;
import com.google.common.collect.ImmutableList;

import entity.bot.Memory;
import entity.bot.MessageSignal;


public abstract class BotBrain {
	protected int energy;
	protected Memory mem;
	protected Vision vision;
	protected AbsPos position;
	protected ImmutableList<MessageSignal> msgs;
	protected Replay.Action lastAction;
	protected BotState state;
	protected Integer elapsedGestation;

	public BotBrain() {
	}

	public BrainActionAndMemory decideAction(BrainInfo info) {
		energy = info.energy;
		mem = new Memory(info.mem);
		vision = info.vision;
		position = info.position;
		msgs = info.msgs;
		lastAction = info.lastAction;
		state = info.botState;
		elapsedGestation = info.elapsedGestation;


		// TODO-DESIGN Sandbox
		BrainActionAndMemory brainAction;

		try {
			brainAction = new BrainActionAndMemory(mem, brainDecideAction());
		} catch (Exception e) {
			//TODO make BotBrain errors visible
			e.printStackTrace(System.err);
			// NOTE, undoes any changes to memory made by brain
			brainAction = new BrainActionAndMemory(null, null);
			//TODO REMOVE ME -- HERE FOR DEBUG
			throw new IllegalStateException();
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
