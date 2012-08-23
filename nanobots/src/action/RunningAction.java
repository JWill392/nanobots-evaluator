package action;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Iterator;
import java.util.List;

import replay.ReplayProto.Replay.Action.Type;

import entity.BotEntity;
import game.world.World;

public abstract class RunningAction implements ActionCmd {
	private boolean dieOnNextTick = false;
	private final BotEntity actor = null;

	/**
	 * Removes unaffordable actions from <i>actors</i>
	 */
	public void executeAll(World world, List<BotEntity> actors) {
		for (Iterator<BotEntity> iter = actors.iterator(); iter.hasNext();) {
			BotEntity bot = iter.next();

			RunningAction action = bot.getRunningAction();

			// can't afford
			if (!action.canAfford(bot)) {
				destroy();
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
		destroy();
	}

	/**
	 * Amount bot is required to pay to execute this action.  Should be positive.
	 */
	protected abstract int getCost();

	public abstract Type getType();

	protected void destroy() {
		dieOnNextTick = true;
	}

	public boolean hasDieOnNextTick() {
		return dieOnNextTick;
	}
}
