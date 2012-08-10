package brain;


import java.util.Collection;

import teampg.grid2d.point.AbsPos;

import com.google.common.collect.ImmutableList;

import entity.BotEntity;
import entity.bot.Memory;
import entity.bot.MessageSignal;
import game.World;


public class BrainInfo {
	private final int energy;
	private final Memory mem;
	private final Vision vision;
	private final ImmutableList<MessageSignal> msgs;

	public BrainInfo(BotEntity beholder, World map, Collection<AbsPos> visibleCells) {
		msgs = beholder.getReceivedMessages();
		energy = beholder.getEnergy();
		mem = beholder.getMemory();
		vision = new Vision(map, visibleCells, map.getBotPosition(beholder.getID()));
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
