package action;

import java.util.List;

import entity.BotEntity;
import entity.EmptyEntity;
import entity.Entity;
import entity.bot.Memory;
import game.Settings;
import game.world.World;
import teampg.grid2d.point.AbsPos;

public class ReproduceCmd extends TargettedAction {
	private final Memory newbornMemory;

	public ReproduceCmd(AbsPos target, Memory newbornMemory) {
		super(target);

		this.newbornMemory = newbornMemory;
	}

	public Memory getNewbornMemory() {
		return (Memory) newbornMemory.clone();
	}

	@Override
	public final void executeAll(World world, List<BotEntity> actors) {
		super.executeAll(world, actors);

		for (BotEntity bot : actors) {
			ReproduceCmd action = bot.getRunningAction(ReproduceCmd.class);
			Entity targetEnt = world.get(action.target);

			if (!(targetEnt instanceof EmptyEntity)) {
				bot.removeRunningAction(action);
				continue;
			}

			action.exactCostAndRemoveFrom(bot);
			BotEntity newborn = Entity.getNewBot(bot.getTeam());
			newborn.setMemory(action.getNewbornMemory());
			world.addNewEntity(action.target, newborn);
		}
	}

	@Override
	protected int getCost() {
		return Settings.getActionCost(this.getClass());
	}
}
