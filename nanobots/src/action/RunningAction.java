package action;

import com.google.common.collect.Iterables;
import entity.BotEntity;
import game.Settings;
import game.world.World;

public abstract class RunningAction implements ActionCmd {
	public abstract void executeAll(World world, Iterable<BotEntity> actors);

	public static boolean hasMultiple(BotEntity bot, Class<? extends RunningAction> actionType) {
		return Iterables.size(Iterables.filter(bot.getRunningActions(), actionType)) > 1;
	}

	public static <T extends RunningAction> Iterable<T> filter(BotEntity bot, Class<T> actionType) {
		return Iterables.filter(bot.getRunningActions(), actionType);
	}

	public static void removeAll(BotEntity bot, Class<? extends RunningAction> actionType) {
		for (RunningAction toRemove : bot.getRunningActions()) {
			bot.removeRunningAction(toRemove);
		}
	}

	/**
	 * If actor has enough energy, takes away cost of this action.
	 * @return true if enough energy.  false if not.
	 */
	public boolean exactCost(BotEntity actor) {
		if (actor.getEnergy() <= Settings.getActionCost(this.getClass())) {
			return false;
		}

		actor.addEnergy(-Settings.getActionCost(this.getClass()));
		return true;
	}

	public boolean canAfford(BotEntity actor) {
		return Settings.getActionCost(this.getClass()) < actor.getEnergy();
	}
}
