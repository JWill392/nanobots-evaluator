package action;

import java.util.List;

import entity.BotEntity;
import entity.Entity;
import game.Settings;
import game.world.World;
import teampg.grid2d.point.AbsPos;

public class AttackCmd extends TargettedAction {
	public AttackCmd(AbsPos target) {
		super(target);
	}

	@Override
	public final void executeAll(World world, List<BotEntity> actors) {
		super.executeAll(world, actors); //remove obviously illegal actions

		for (BotEntity bot : actors) {
			AttackCmd action = bot.getRunningAction(this.getClass());
			Entity targetEnt = world.get(action.target);

			// target illegal
			if (!(targetEnt instanceof BotEntity)) {
				bot.removeRunningAction(action);
				continue;
			}

			action.exactCostAndRemoveFrom(bot);
			((BotEntity) targetEnt).addEnergy(-Settings.getAttackDamage());
		}
	}

	@Override
	protected int getCost() {
		return Settings.getActionCost(this.getClass());
	}
}
