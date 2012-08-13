package game;

import brain.BotBrain;
import brain.BrainCommand;
import brain.BrainInfo;



/**
 *
 * @author Jackson Williams
 */
// TODO what happens when we remove a bot from grid? Shouldn't have to remove it
// from here too...
public class Team {
	private final BotBrain brain;

	private boolean hasLost;
	private final String name;

	public Team(BotBrain inBrain, String inName) {
		brain = inBrain;
		name = inName;

		hasLost = false;
	}

	public BrainCommand decideAction(BrainInfo info) {
		BrainCommand cmd = brain.decideAction(info);
		//TODO store botaction in bot; keep track of types in team

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
