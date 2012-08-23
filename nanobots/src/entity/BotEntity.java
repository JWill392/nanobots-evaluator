package entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import replay.ReplayProto.Replay;
import replay.ReplayProto.Replay.Entity.Type;
import com.google.common.collect.ImmutableList;
import entity.bot.Memory;
import entity.bot.MessageSignal;

import game.Settings;
import game.Team;
import action.RunningAction;

public class BotEntity extends MortalEntity implements MobileEntity{
	private static final Replay.Entity.Type TYPE = Type.BOT;
	private static int botIDCounter = 0;

	private final int botID;
	private final Team team;
	private final Memory memory;

	private int energy;
	private ArrayList<MessageSignal> inbox;
	private RunningAction runningAction;
	private final Map<Class<? extends RunningAction>, Integer> actionCooldowns;

	static BotEntity getNewBotEntity(Team team) {
		int botIDToUse = botIDCounter;
		botIDCounter++;

		return new BotEntity(team, botIDToUse);
	}

	@Override
	public Type getType() {
		return TYPE;
	}

	public static boolean areAllies(BotEntity a, BotEntity b) {
		return a.team.equals(b.team);
	}

	private BotEntity(Team team, int botID) {
		this.botID = botID;
		this.team = team;
		inbox = new ArrayList<MessageSignal>();

		setEnergy(Settings.getNewbornEnergy());

		memory = new Memory();
		setMemory(memory); // update data object

		actionCooldowns = new HashMap<>();
		runningAction = null;
	}


	public void setRunningAction(RunningAction action) {
		runningAction = action;
		data.setRunningAction(replay.Util.of(action));
	}
	public RunningAction getRunningAction() {
		return runningAction;
	}
	public void destroyRunningAction() {
		//TODO say if success or failure?
		runningAction = null;
		data.clearRunningAction();
	}



	@Override
	public void tick() {
		inbox = new ArrayList<MessageSignal>();

		// TODO tweak
		if (energy > Settings.getBotMaxEnergy()) {
			int overMax = energy - Settings.getBotMaxEnergy();
			energy = Settings.getBotMaxEnergy() + Math.round(overMax*Settings.getOverchargeDrain());
		}
	}

	public int getID() {
		return botID;
	}

	@Override
	public int getEnergy() {
		return energy;
	}

	private void setEnergy(int e) {
		energy = e;
		data.setEnergy(e);
	}

	public void addEnergy(int inEnergy) {
		setEnergy(inEnergy + energy);
	}

	public Team getTeam() {
		return team;
	}

	public Memory getMemory() {
		return (Memory) memory.clone();
	}

	public void setMemory(Memory inMem) {
		int newMemVal = inMem.getAll();
		memory.fill(newMemVal);

		data.setMemory(newMemVal);
	}

	public void addReceivedMessage(MessageSignal msg) {
		inbox.add(msg);
		data.addInbox(msg.getData());
	}

	public ImmutableList<MessageSignal> getReceivedMessages() {
		return ImmutableList.copyOf(inbox);
	}

	@Override
	public String toString() {
		return "BotEntity [botID=" + botID + "]";
	}
}
