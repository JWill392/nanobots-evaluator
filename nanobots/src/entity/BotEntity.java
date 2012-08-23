package entity;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import replay.ReplayProto.Replay;
import replay.ReplayProto.Replay.Entity.Type;
import teampg.grid2d.point.AbsPos;

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
	private final Map<Class<? extends RunningAction>, RunningAction> runningActions;

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

		runningActions = new HashMap<>(10);
	}



	public void addRunningAction(RunningAction action) {
		if (runningActions.get(action.getClass()) != null) {
			// TODO fail action
			return;
		}

		runningActions.put(action.getClass(), action);
	}
	public void removeRunningAction(RunningAction action) {
		removeRunningAction(action.getClass());
	}
	public void removeRunningAction(Class<? extends RunningAction> type) {
		checkArgument(runningActions.containsKey(type));
		runningActions.remove(type);
	}
	@SuppressWarnings("unchecked")
	public <T extends RunningAction> T getRunningAction(Class<T> ofType) {
		return (T) runningActions.get(ofType);
	}
	public boolean hasRunningAction(Class<? extends RunningAction> ofType) {
		return runningActions.containsKey(ofType);
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
