package action;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Iterator;
import java.util.List;

import entity.BotEntity;
import game.world.World;

public abstract class RunningAction implements ActionCmd {
	/**
	 * Removes unaffordable actions from <i>actors</i>
	 */
	public void executeAll(World world, List<BotEntity> actors) {
		for (Iterator<BotEntity> iter = actors.iterator(); iter.hasNext();) {
			BotEntity bot = iter.next();

			RunningAction action = bot.getRunningAction(this.getClass());

			// can't afford
			if (!action.canAfford(bot)) {
				bot.removeRunningAction(action);
				iter.remove();
				continue;
			}
		}
	}

	public final boolean canAfford(BotEntity actor) {
		return getCost() < actor.getEnergy();
	}

	public final void exactCostAndRemoveFrom(BotEntity actor) {
		checkArgument(canAfford(actor));
		actor.addEnergy(-getCost());
		actor.removeRunningAction(this);
	}

	/**
	 * Amount bot is required to pay to execute this action.  Should be positive.
	 */
	protected abstract int getCost();
}
