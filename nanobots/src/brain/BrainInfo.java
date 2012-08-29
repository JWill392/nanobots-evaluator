package brain;


import replay.ReplayProto.Replay;
import replay.ReplayProto.Replay.Entity.BotState;
import teampg.grid2d.point.AbsPos;

import com.google.common.collect.ImmutableList;

import entity.BotEntity;
import entity.bot.MessageSignal;


public class BrainInfo {
	public final int energy;
	public final int mem;
	public final Vision vision;
	public final AbsPos position;
	public final ImmutableList<MessageSignal> msgs;
	public final Replay.Action lastAction;
	public final BotState botState;
	public final Integer elapsedGestation;

	public BrainInfo(BotEntity beholder, AbsPos position, Vision vision) {
		msgs = beholder.getReceivedMessages();
		energy = beholder.getEnergy();

		if (beholder.getRunningAction() != null) {
			System.out.println("BOT: " + beholder + " POS: " + position + " RA: " + beholder.getRunningAction() + " OUT: " + beholder.getOutcome());
			lastAction = beholder.getRunningAction().getData();
		} else {
			lastAction = null;
		}

		botState = beholder.getState();
		if (beholder.getState() == BotState.GESTATING) {
			elapsedGestation = beholder.getElapsedGestation();
		} else {
			elapsedGestation = null;
		}

		mem = beholder.getMemory().getAll();
		this.vision = vision;
		this.position = position;
	}
}
