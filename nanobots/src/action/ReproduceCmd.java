package action;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import entity.BotEntity;
import entity.Entity;
import entity.bot.Memory;
import game.Settings;
import game.world.World;
import replay.ReplayProto.Replay;
import replay.ReplayProto.Replay.Action.Type;
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
			ReproduceCmd action = (ReproduceCmd) bot.getRunningAction();
			Entity targetEnt = world.get(action.target);

			if (!(targetEnt == null)) {
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
			ReproduceCmd failedReproduce = (ReproduceCmd) invalidBot.getRunningAction();
			failedReproduce.destroy();
		}
		actors.removeAll(illegalActors);

		// EXECUTE remaining legals
		for (BotEntity legalBot : actors) {
			ReproduceCmd action = (ReproduceCmd) legalBot.getRunningAction();

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

	@Override
	public Type getType() {
		return Replay.Action.Type.REPRODUCE;
	}
}
