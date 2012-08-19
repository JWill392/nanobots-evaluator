package action;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

		// bots cannot reproduce into same cell as others... so keep track of targets to know which overlap
		Map<AbsPos, BotEntity> targets = new HashMap<>();
		Set<BotEntity> illegalActors = new HashSet<>();

		// VALIDATE
		for (BotEntity bot : actors) {
			ReproduceCmd action = bot.getRunningAction(ReproduceCmd.class);
			Entity targetEnt = world.get(action.target);

			if (!(targetEnt instanceof EmptyEntity)) {
				illegalActors.add(bot);
				continue;
			}

			// more than one seemingly legal actors are targetting one position -- all are illegal
			if (targets.containsKey(action.target)) {
				BotEntity collidingActor = targets.get(action.target);
				illegalActors.add(collidingActor);
				illegalActors.add(bot);
				continue;
			}
			targets.put(action.target, bot);
		}

		// Clean up illegals
		for (BotEntity invalidBot : illegalActors) {
			invalidBot.removeRunningAction(ReproduceCmd.class);
		}
		actors.removeAll(illegalActors);

		// EXECUTE remaining legals
		for (BotEntity legalBot : actors) {
			ReproduceCmd action = legalBot.getRunningAction(ReproduceCmd.class);

			action.exactCostAndRemoveFrom(legalBot);
			BotEntity newborn = Entity.getNewBot(legalBot.getTeam());
			newborn.setMemory(action.getNewbornMemory());
			world.addNewEntity(action.target, newborn);
		}
	}

	@Override
	protected int getCost() {
		return Settings.getActionCost(this.getClass());
	}
}
