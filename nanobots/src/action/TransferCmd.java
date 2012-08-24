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

public class TransferCmd extends TargettedAction {
	public final int amount;

	public TransferCmd(AbsPos target, int energyAmount) {
		super(target);
		amount = energyAmount;
	}

	@Override
	protected int getCost() {
		return amount + Settings.getActionCost(TransferCmd.class);
	}

	@Override
	public void executeAll(World world, List<BotEntity> actors) {
		super.executeAll(world, actors);

		for (BotEntity actor : actors) {
			TransferCmd cmd = (TransferCmd) actor.getRunningAction();

			// must be bot
			Entity targetEnt = world.get(cmd.target);
			if(!(targetEnt instanceof BotEntity)) {
				cmd.destroy();
				continue;
			}

			BotEntity targetBot = (BotEntity) targetEnt;

			// must be on same team
			if (Team.onSameTeam(actor, targetBot) == false) {
				cmd.destroy();
				continue;
			}

			cmd.exactCostAndRemoveFrom(actor);
			targetBot.addEnergy(cmd.amount);
		}
	}

	@Override
	public Type getType() {
		return Replay.Action.Type.TRANSFER;
	}
}
