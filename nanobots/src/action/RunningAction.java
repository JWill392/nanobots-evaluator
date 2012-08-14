package action;

import entity.BotEntity;
import game.Settings;
import game.world.World;

public abstract class RunningAction implements ActionCmd {
	public abstract void executeAll(World world, Iterable<BotEntity> actors);

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
