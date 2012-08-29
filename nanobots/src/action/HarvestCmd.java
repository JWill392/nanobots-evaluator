package action;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import entity.BotEntity;
import entity.Entity;
import entity.FoodEntity;
import game.Settings;
import game.world.World;
import replay.ReplayProto.Replay;
import replay.ReplayProto.Replay.Action.Type;
import replay.ReplayProto.Replay.Entity.BotState;
import teampg.grid2d.point.AbsPos;

public class HarvestCmd extends TargettedAction {
	public HarvestCmd(AbsPos target) {
		super(target);
	}

	static void executeAll(World world, List<BotEntity> actors) {
		filterBasicInvalid(world, actors);

		// All bots harvesting one FoodEntity are in one harvest group.
		Multimap<AbsPos, BotEntity> harvestGroups = HashMultimap.create(100, 3);

		// VALIDATE, GROUP
		for (BotEntity bot : actors) {
			HarvestCmd action = (HarvestCmd) bot.getRunningAction();
			AbsPos targetPos = action.getTarget();
			Entity targetEnt = world.get(targetPos);

			// target illegal
			if (!(targetEnt instanceof FoodEntity)) {
				action.fail(Replay.Action.Outcome.ILLEGAL_TARGET);
				continue;
			}

			harvestGroups.put(targetPos, bot);
		}

		// EXECUTE
		// Each group harvests from its FoodEntity once.
		for (AbsPos target : harvestGroups.keySet()) {
			Collection<BotEntity> harvesters = harvestGroups.get(target);
			FoodEntity food = (FoodEntity) world.get(target);

			// no matter number of harvesters, amount harvested from one food per turn doesn't increase
			int amountTakenFromFood = food.harvest(Settings.getHarvestEnergy());
			// instead it is divided between each harvester
			int fractionForEachHarvester = amountTakenFromFood/harvesters.size();

			for (BotEntity bot : harvesters) {
				HarvestCmd action = (HarvestCmd) bot.getRunningAction();

				action.succeed(bot);
				bot.addEnergy(fractionForEachHarvester);
			}
		}
	}

	@Override
	protected int getCost() {
		return Settings.getActionCost(this.getClass());
	}

	@Override
	public Type getType() {
		return Replay.Action.Type.HARVEST;
	}

	@Override
	protected ImmutableList<BotState> getLegalActorStates() {
		return ImmutableList.of(BotState.NORMAL);
	}
}
