package action;

import java.util.Collection;

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
	public void executeAll(World world, Iterable<BotEntity> actors) {
		// All bots harvesting one FoodEntity are in one harvest group.
		Multimap<AbsPos, BotEntity> harvestGroups = HashMultimap.create(1000, 3);

		// VALIDATE
		for (BotEntity bot : actors) {

			HarvestCmd action = bot.getRunningAction(this.getClass());
			AbsPos targetPos = action.target;
			Entity targetEnt = world.get(targetPos);

			// target illegal
			if (!(targetEnt instanceof FoodEntity)) {
				bot.removeRunningAction(action);
				continue;
			}

			// can't afford
			if (!action.exactCost(bot)) {
				bot.removeRunningAction(action);
				continue;
			}

			harvestGroups.put(targetPos, bot);
			bot.removeRunningAction(action);
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
				bot.addEnergy(fractionForEachHarvester);
			}
		}
	}
}
