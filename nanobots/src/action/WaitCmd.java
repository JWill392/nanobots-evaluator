package action;

import java.util.List;

import entity.BotEntity;
import game.Settings;
import game.world.World;

/**
 * Dummy action that does nothing.  Demonstrates basic requirements for every RunningAction.
 */
public class WaitCmd extends RunningAction{
	public WaitCmd() {
	}

	@Override
	public final void executeAll(World world, List<BotEntity> actors) {
		super.executeAll(world, actors); // validates for common things; eg cost.

		for (BotEntity bot : actors) {
			WaitCmd action = bot.getRunningAction(WaitCmd.class); //get the waitaction this actor is trying to execute
			action.exactCostAndRemoveFrom(bot); //and just execute it.  No need to validate; anyone can run this action, provided they can afford the cost.
		}
	}

	@Override
	protected int getCost() {
		return Settings.getActionCost(this.getClass());
	}
}
