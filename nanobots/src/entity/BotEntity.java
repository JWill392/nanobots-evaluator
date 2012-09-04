package entity;

import java.util.LinkedList;
import java.util.Queue;

import replay.ReplayProto.Replay;
import replay.ReplayProto.Replay.Action.Outcome;
import replay.ReplayProto.Replay.Entity.ReceivedMessage;
import replay.ReplayProto.Replay.Entity.Type;
import replay.ReplayProto.Replay.Entity.BotState;
import teampg.grid2d.point.AbsPos;

import com.google.common.collect.Iterators;
import com.google.common.collect.UnmodifiableIterator;

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

	private final Queue<ReceivedMessage> inboxBuffer;

	private RunningAction runningAction;

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

		setEnergy(Settings.getNewbornEnergy());

		runningAction = null;
		inboxBuffer = new LinkedList<>();

		data.setBotState(Replay.Entity.BotState.NORMAL);
		data.setMemory(0);
	}


	public void setRunningAction(RunningAction action) {
		runningAction = action;
	}
	public RunningAction getRunningAction() {
		return runningAction;
	}
	public boolean hasRunningAction() {
		return runningAction != null;
	}

	public Replay.Entity.Builder getData(AbsPos entPos, int eid, int tid) {
		// shouldn't get running action through this; see getRunningAction
		assert data.hasRunningAction() == false;

		return (data.clone()).setPos(replay.Util.of(entPos))
				.setEid(eid)
				.setTid(tid);
	}


	@Override
	public void tick() {
		super.tick();

		// TODO tweak overcharge dissipation algorithm
		if (getEnergy() > Settings.getBotMaxEnergy()) {
			int amountOverMax = getEnergy() - Settings.getBotMaxEnergy();
			final int reducedEnergyValue = Settings.getBotMaxEnergy() + Math.round(amountOverMax*Settings.getOverchargeDrain());
			setEnergy(reducedEnergyValue);
		}

		// exact gestation cost, else fall back to normal state
		if (data.getBotState() == BotState.GESTATING) {
			if (getEnergy() > Settings.getGestationUpkeep()) {
				addEnergy(-Settings.getGestationUpkeep());
				data.setElapsedGestation(data.getElapsedGestation() + 1);
			} else {
				setState(BotState.NORMAL);
			}
		}

		// receive messages from this turn, throw out ones from last turn

		data.clearInbox();

		while (!inboxBuffer.isEmpty()) {
			ReceivedMessage msg = inboxBuffer.remove();
			data.addInbox(msg);
		}

		runningAction = null;
	}

	public int getID() {
		return botID;
	}

	@Override
	public int getEnergy() {
		return data.getEnergy();
	}

	private void setEnergy(int e) {
		data.setEnergy(e);
	}

	public void addEnergy(int inEnergy) {
		setEnergy(inEnergy + getEnergy());
	}

	public Team getTeam() {
		return team;
	}

	public Memory getMemory() {
		return new Memory(data.getMemory());
	}

	public void setMemory(Memory inMem) {
		data.setMemory(inMem.getAll());
	}

	public void addReceivedMessage(MessageSignal msg) {
		inboxBuffer.add(msg.getData());
	}

	public UnmodifiableIterator<ReceivedMessage> getReceivedMessages() {
		return Iterators.unmodifiableIterator(data.getInboxList().iterator());
	}

	public Replay.Entity.BotState getState() {
		return data.getBotState();
	}
	public void setState(Replay.Entity.BotState state) {
		data.setBotState(state);
		if (state == Replay.Entity.BotState.GESTATING) {
			data.setElapsedGestation(0);
		} else {
			data.clearElapsedGestation();
		}
	}
	public int getElapsedGestation() {
		if (data.hasElapsedGestation()) {
			return data.getElapsedGestation();
		} else {
			return -1;
		}
	}

	public Outcome getOutcome() {
		if (runningAction == null) {
			return null;
		}
		return runningAction.debugOutcome;
	}

	@Override
	public String toString() {
		return "BotEntity [botID=" + botID + "]";
	}
}
