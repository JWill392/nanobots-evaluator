package brain;

import entity.bot.Memory;
import action.ActionCmd;
import action.RunningAction;

public class BrainCommand {
	RunningAction act;
	Memory mem;

	public BrainCommand(ActionCmd actToTake, Memory toSetMem) {
		act = (RunningAction) actToTake;
		mem = toSetMem;
	}

	public Memory getMemory() {
		return mem;
	}

	public RunningAction getAction() {
		return act;
	}
}
