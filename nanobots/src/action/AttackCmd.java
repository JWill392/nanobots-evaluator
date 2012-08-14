package action;

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
	public void executeAll(World world, Iterable<BotEntity> actors) {
		for (BotEntity bot : actors) {

			AttackCmd action = bot.getRunningAction(this.getClass());
			Entity targetEnt = world.get(action.target);

			// target illegal
			if (!(targetEnt instanceof BotEntity)) {
				bot.removeRunningAction(action);
				continue;
			}

			// can't afford
			if (!action.exactCost(bot)) {
				bot.removeRunningAction(action);
				continue;
			}

			((BotEntity) targetEnt).addEnergy(-Settings.getAttackDamage());
			bot.removeRunningAction(action);
		}
	}
}
