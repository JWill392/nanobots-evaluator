package game;

import brain.BotBrain;
import brain.BotBrain.BrainActionAndMemory;
import brain.BrainInfo;


public class Team {
	private final BotBrain brain;

	private boolean hasLost;
	private final String name;

	public Team(BotBrain inBrain, String inName) {
		brain = inBrain;
		name = inName;

		hasLost = false;
	}

	public BrainActionAndMemory decideAction(BrainInfo info) {
		BrainActionAndMemory cmd = brain.decideAction(info);
		return cmd;
	}

	public String getName() {
		return name;
	}

	public boolean hasLost() {
		return hasLost;
	}

	public void setLost() {
		hasLost = true;
	}
}
