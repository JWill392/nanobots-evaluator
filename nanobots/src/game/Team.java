package game;

import entity.BotEntity;
import replay.ReplayProto.Replay;
import brain.BotBrain;
import brain.BotBrain.BrainActionAndMemory;
import brain.BrainInfo;


public class Team {
	private final BotBrain brain;

	private boolean hasLost;
	private final String name;

	private final Replay.Team.Builder data;

	public Team(BotBrain inBrain, String inName) {
		brain = inBrain;
		name = inName;

		hasLost = false;
		data = Replay.Team.newBuilder()
				.setName(name);
	}

	public Replay.Team getData(int tid) {
		return data.setTid(tid).build();
	}

	public BrainActionAndMemory decideAction(BrainInfo info) {
		BrainActionAndMemory cmd = brain.decideAction(info);
		return cmd;
	}

	public BotBrain getBrain() {
		return brain;
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

	public static boolean onSameTeam(BotEntity a, BotEntity b) {
		return a.getTeam().equals(b.getTeam());
	}
}
