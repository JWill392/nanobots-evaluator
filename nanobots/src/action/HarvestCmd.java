package action;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import entity.BotEntity;
import entity.Entity;
import entity.FoodEntity;
import game.Settings;
import game.world.World;
import teampg.grid2d.point.AbsPos;

public class HarvestCmd extends TargettedAction {
	public HarvestCmd(AbsPos target) {
		super(target);
	}

	@Override
	public final void executeAll(World world, List<BotEntity> actors) {
		super.executeAll(world, actors); //remove obviously illegal actions

		// All bots harvesting one FoodEntity are in one harvest group.
		Multimap<AbsPos, BotEntity> harvestGroups = HashMultimap.create(500, 3);

		// VALIDATE, GROUP
		for (BotEntity bot : actors) {
			HarvestCmd action = bot.getRunningAction(this.getClass());
			AbsPos targetPos = action.target;
			Entity targetEnt = world.get(targetPos);

			// target illegal
			if (!(targetEnt instanceof FoodEntity)) {
				bot.removeRunningAction(action);
				continue;
			}

			harvestGroups.put(targetPos, bot);
		}

		// EXECUTE
		// Each group harvests from its FoodEntity once.
		for (AbsPos target : harvestGroups.keys()) {
			Collection<BotEntity> harvesters = harvestGroups.get(target);
			FoodEntity food = (FoodEntity) world.get(target);

			// no matter number of harvesters, amount harvested from one food per turn doesn't increase
			int amountTakenFromFood = food.harvest(Settings.getHarvestEnergy());
			// instead it is divided between each harvester
			int fractionForEachHarvester = amountTakenFromFood/harvesters.size();

			for (BotEntity bot : harvesters) {
				HarvestCmd action = bot.getRunningAction(HarvestCmd.class);

				action.exactCostAndRemoveFrom(bot);
				bot.addEnergy(fractionForEachHarvester);
			}
		}
	}

	@Override
	protected int getCost() {
		return Settings.getActionCost(this.getClass());
	}
}
