package brain.demo.communalbot.memory;

import entity.bot.Memory;

public class FighterMemory extends CommunalMemory {
	public FighterMemory(Memory mem) {
		super(mem);
		setRole(BotRole.FIGHTER);
	}

}
