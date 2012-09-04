package brain.demo.communalbot.memory;

import entity.bot.Memory;

public class FeederMemory extends CommunalMemory {
	public FeederMemory(Memory mem) {
		super(mem);
		setRole(BotRole.FEEDER);
	}

}
