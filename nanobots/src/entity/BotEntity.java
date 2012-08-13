package entity;

import java.util.ArrayList;
import java.util.Collection;
import com.google.common.collect.ImmutableList;
import entity.bot.Memory;
import entity.bot.MessageSignal;

import game.Settings;
import game.Team;
import action.RunningAction;

public class BotEntity extends MortalEntity implements MobileEntity{
	private static int botIDCounter = 0;

	private final int botID;
	private final Team team;
	private final Memory memory;

	private int energy;
	private ArrayList<MessageSignal> inbox;
	private final Collection<RunningAction> runningActions;

	static BotEntity getNewBotEntity(int energy, Team team) {
		int botIDToUse = botIDCounter;
		botIDCounter++;

		return new BotEntity(energy, team, botIDToUse);
	}

	public static boolean areAllies(BotEntity a, BotEntity b) {
		return a.team.equals(b.team);
	}

	private BotEntity(int inEnergy, Team team, int botID) {
		this.botID = botID;
		addEnergy(inEnergy);
		this.team = team;

		inbox = new ArrayList<MessageSignal>();
		memory = new Memory();
		runningActions = new ArrayList<>();
	}



	public void addRunningAction(RunningAction action) {
		runningActions.add(action);
	}
	public void removeRunningAction(RunningAction action) {
		runningActions.remove(action);
	}
	public ImmutableList<RunningAction> getRunningActions() {
		return ImmutableList.copyOf(runningActions);
	}



	@Override
	public void tick() {
		inbox = new ArrayList<MessageSignal>();
	}

	public int getID() {
		return botID;
	}

	@Override
	public int getEnergy() {
		return energy;
	}

	public void addEnergy(int inEnergy) {
		int newEnergy = energy + inEnergy;

		//TODO if energy is zero, die?  Or is that in tick?

		if (newEnergy > Settings.getBotMaxEnergy()) {
			newEnergy = Settings.getBotMaxEnergy();
		}

		energy = newEnergy;
	}

	public Team getTeam() {
		return team;
	}

	public Memory getMemory() {
		return (Memory) memory.clone();
	}

	public void setMemory(Memory inMem) {
		memory.fill(inMem.getAll());
	}

	public void addReceivedMessage(MessageSignal msg) {
		inbox.add(msg);
	}

	public ImmutableList<MessageSignal> getReceivedMessages() {
		return ImmutableList.copyOf(inbox);
	}
}
