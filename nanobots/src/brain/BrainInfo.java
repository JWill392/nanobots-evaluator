package brain;


import teampg.grid2d.point.AbsPos;

import com.google.common.collect.ImmutableList;

import entity.BotEntity;
import entity.bot.Memory;
import entity.bot.MessageSignal;


public class BrainInfo {
	private final int energy;
	private final Memory mem;
	private final Vision vision;
	private final AbsPos position;
	private final ImmutableList<MessageSignal> msgs;

	public BrainInfo(BotEntity beholder, AbsPos position, Vision vision) {
		msgs = beholder.getReceivedMessages();
		energy = beholder.getEnergy();
		mem = beholder.getMemory();
		this.vision = vision;
		this.position = position;
	}

	public AbsPos getPosition() {
		return position;
	}

	public int getEnergy() {
		return energy;
	}

	public Memory getMemory() {
		return mem;
	}

	public Vision getVision() {
		return vision;
	}

	public ImmutableList<MessageSignal> getMessages() {
		return msgs;
	}
}
