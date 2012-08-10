package brain;

import entity.bot.Memory;
import action.cmd.ActionCmd;

public class BrainCommand {
	ActionCmd act;
	Memory mem;
	
	public BrainCommand(ActionCmd actToTake, Memory toSetMem) {
		act = actToTake;
		mem = toSetMem;
	}
	
	public Memory getMemory() {
		return mem;
	}
	
	public ActionCmd getAction() {
		return act;
	}
}
