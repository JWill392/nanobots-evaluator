package action;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Sets;

import action.move.MoveSim;
import action.move.Simulade;
import action.move.Simulade.MoveState;

import entity.BotEntity;
import entity.Entity;
import game.Settings;
import game.world.World;
import replay.ReplayProto.Replay;
import replay.ReplayProto.Replay.Action.Type;
import teampg.grid2d.point.AbsPos;

public class MoveCmd extends TargettedAction {
	public MoveCmd(AbsPos target) {
		super(target);
	}

	@Override
	public final void executeAll(World world, List<BotEntity> actors) {
		super.executeAll(world, actors); //remove obviously illegal actions

		// BASIC VALIDATION
		for (Iterator<BotEntity> iter = actors.iterator(); iter.hasNext();) {
			BotEntity bot = iter.next();
			MoveCmd action = (MoveCmd) bot.getRunningAction();

			AbsPos startPos = world.getBotPosition(bot.getID());
			Entity targetEnt = world.get(action.target);

			// target is start
			if (startPos.equals(action.target)) {
				action.destroy();
				iter.remove();
				continue;
			}

			// at target is bot NOT planning to move
			if (targetEnt instanceof BotEntity) {
				BotEntity targetBot = (BotEntity)targetEnt;
				if ((targetBot.getRunningAction() instanceof MoveCmd) == false) {
					action.destroy();
					iter.remove();
					continue;
				}
			}

			// at target is static entity (eg wall)
			if (!(targetEnt instanceof BotEntity) && !(targetEnt == null)) {
				action.destroy();
				iter.remove();
				continue;
			}

		}

		// GROUP VALIDATION
		{
			MoveSim moveSim = new MoveSim(world, Sets.newHashSet(actors));

			for (Simulade simulade : moveSim) {
				// all simulades that couldn't legally move to their goal
				if (simulade.getMoveState() == MoveState.READY_AT_START
						|| simulade.getMoveState() == MoveState.UNMOVED_TO_START) {

					BotEntity moveFailedBot = (BotEntity) simulade.simulatedEntity;
					MoveCmd failedCmd = (MoveCmd) moveFailedBot.getRunningAction();

					failedCmd.destroy();
					actors.remove(moveFailedBot);
				}
			}
		}

		// EXECUTE
		for (BotEntity validBot : actors) {
			MoveCmd action = (MoveCmd) validBot.getRunningAction();
			AbsPos currPos = world.getBotPosition(validBot.getID());
			AbsPos goal = action.target;

			world.swap(currPos, goal);
			action.exactCostAndRemoveFrom(validBot);
		}

	}

	@Override
	protected int getCost() {
		return Settings.getActionCost(this.getClass());
	}

	@Override
	public String toString() {
		return "MoveCmd [target=" + target + "]";
	}

	@Override
	public Type getType() {
		return Replay.Action.Type.MOVE;
	}
}
