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
import replay.ReplayProto.Replay.Entity.BotState;
import teampg.grid2d.point.AbsPos;

public class TransferCmd extends TargettedAction {
	public final int amount;

	public TransferCmd(AbsPos target, int energyAmount) {
		super(target);
		amount = energyAmount;

		data.setTransferAmount(energyAmount);
	}

	@Override
	protected int getCost() {
		return amount + Settings.getActionCost(TransferCmd.class);
	}

	static void executeAll(World world, List<BotEntity> actors) {
		filterBasicInvalid(world, actors);

		for (BotEntity actor : actors) {
			TransferCmd cmd = (TransferCmd) actor.getRunningAction();

			// must be bot
			Entity targetEnt = world.get(cmd.getTarget());
			if(!(targetEnt instanceof BotEntity)) {
				cmd.fail(Replay.Action.Outcome.ILLEGAL_TARGET);
				continue;
			}

			BotEntity targetBot = (BotEntity) targetEnt;

			// must be on same team
			if (Team.onSameTeam(actor, targetBot) == false) {
				cmd.fail(Replay.Action.Outcome.ILLEGAL_TARGET);
				continue;
			}

			cmd.succeed(actor);
			targetBot.addEnergy(cmd.amount);
		}
	}

	@Override
	public Type getType() {
		return Replay.Action.Type.TRANSFER;
	}

	@Override
	protected ImmutableList<BotState> getLegalActorStates() {
		return ImmutableList.of(BotState.NORMAL);
	}
}
