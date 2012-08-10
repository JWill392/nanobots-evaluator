package entity;

import java.util.ArrayList;

import com.google.common.collect.ImmutableList;

import entity.bot.Memory;
import entity.bot.MessageSignal;

import game.Settings;
import action.cmd.ActionCmd;
import action.cmd.ContinuePreviousAction;

public class BotEntity extends Entity implements DynamicEntity, MortalEntity {
	private static int botIDCounter = 0;

	private final int botID;
	private final int teamID;
	private int energy;
	private ArrayList<MessageSignal> inbox;
	private Memory memory;
	private ActionCmd executing;
	private int turnsExecuted;

	public static BotEntity getNewBotEntity(int inEnergy, int inTeamID) {
		int botIDToUse = botIDCounter;
		botIDCounter++;

		return new BotEntity(inEnergy, inTeamID, botIDToUse);
	}

	public static boolean areAllies(BotEntity a, BotEntity b) {
		int aTeam = a.getTeamID();
		int bTeam = b.getTeamID();

		return aTeam == bTeam;
	}

	private BotEntity(int inEnergy, int inTeamID, int inBotID) {
		botID = inBotID;
		addEnergy(inEnergy);
		teamID = inTeamID;
		inbox = new ArrayList<MessageSignal>(5);
		memory = new Memory();
		executing = new ContinuePreviousAction();
		turnsExecuted = 0;
	}
	
	public ActionCmd getAction() {
		return executing;
	}
	
	public int getTurnsActionExecuted() {
		return turnsExecuted;
	}
	
	public void setAction(ActionCmd nextAction) {
		executing = nextAction;
		turnsExecuted = 0;
	}
	
	public void tick() {
		turnsExecuted++;
		inbox = new ArrayList<MessageSignal>();
	}

	public int getID() {
		return botID;
	}

	public int getEnergy() {
		return energy;
	}

	public void addEnergy(int inEnergy) {
		int newEnergy = energy + inEnergy;

		if (newEnergy > Settings.getBotMaxEnergy()) {
			newEnergy = Settings.getBotMaxEnergy();
		}

		energy = newEnergy;
	}

	public int getTeamID() {
		return teamID;
	}

	public Memory getMemory() {
		return Memory.newInstance(memory);
	}

	public void setMemory(Memory inMem) {
		memory.load(inMem);
	}

	public void addReceivedMessage(MessageSignal msg) {
		inbox.add(msg);
	}

	public ImmutableList<MessageSignal> getReceivedMessages() {
		return ImmutableList.copyOf(inbox);
	}
}
