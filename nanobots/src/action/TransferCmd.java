package action;

import java.util.List;

import entity.BotEntity;
import entity.Entity;
import game.Settings;
import game.world.World;
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
			TransferCmd cmd = actor.getRunningAction(TransferCmd.class);

			Entity targetEnt = world.get(cmd.target);
			if(!(targetEnt instanceof BotEntity)) {
				actor.removeRunningAction(cmd);
				continue;
			}

			BotEntity targetBot = (BotEntity) targetEnt;
			if (!(targetBot.getTeam().equals(actor.getTeam()))) {
				actor.removeRunningAction(cmd);
				continue;
			}

			cmd.exactCostAndRemoveFrom(actor);
			targetBot.addEnergy(cmd.amount);
		}
	}
}
