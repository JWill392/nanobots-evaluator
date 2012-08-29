package action;

import java.util.List;

import com.google.common.collect.ImmutableList;

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

	static void executeAll(World world, List<BotEntity> actors) {
		//remove obviously illegal actions
		filterBasicInvalid(world, actors);

		for (BotEntity bot : actors) {
			AttackCmd action = (AttackCmd) bot.getRunningAction();
			Entity targetEnt = world.get(action.getTarget());

			// target must be bot
			if (!(targetEnt instanceof BotEntity)) {
				action.fail(Replay.Action.Outcome.ILLEGAL_TARGET);
				continue;
			}

			// cannot attack teammates
			BotEntity targetBot = (BotEntity) targetEnt;
			if (Team.onSameTeam(bot, targetBot) == true) {
				action.fail(Replay.Action.Outcome.ILLEGAL_TARGET);
				continue;
			}

			action.succeed(bot);
			((BotEntity) targetEnt).addEnergy(-Settings.getAttackDamage());
		}
	}

	@Override
	protected ImmutableList<Replay.Entity.BotState> getLegalActorStates() {
		return ImmutableList.of(Replay.Entity.BotState.NORMAL);
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
