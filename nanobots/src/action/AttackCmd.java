package action;

import java.util.List;

import entity.BotEntity;
import entity.Entity;
import game.Settings;
import game.Team;
import game.world.World;
import replay.ReplayProto.Replay;
import replay.ReplayProto.Replay.Action.Type;
import teampg.grid2d.point.AbsPos;

public class AttackCmd extends TargettedAction {
	public AttackCmd(AbsPos target) {
		super(target);
	}

	@Override
	public final void executeAll(World world, List<BotEntity> actors) {
		super.executeAll(world, actors); //remove obviously illegal actions

		for (BotEntity bot : actors) {
			AttackCmd action = (AttackCmd) bot.getRunningAction();
			Entity targetEnt = world.get(action.target);

			// target must be bot
			if (!(targetEnt instanceof BotEntity)) {
				action.destroy();
				continue;
			}

			// cannot attack teammates
			BotEntity targetBot = (BotEntity) targetEnt;
			Team actorTeam = bot.getTeam();
			if (targetBot.getTeam() == actorTeam) {
				action.destroy();
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

	@Override
	public Type getType() {
		return Replay.Action.Type.ATTACK;
	}
}
