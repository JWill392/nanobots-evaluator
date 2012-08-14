package action;

import java.util.Collection;
import action.move.MoveSim;
import action.move.Simulade;
import action.move.Simulade.MoveState;

import entity.BotEntity;
import entity.EmptyEntity;
import entity.Entity;
import game.world.World;
import teampg.datatypes.BufferedCollection;
import teampg.grid2d.point.AbsPos;

public class MoveCmd extends TargettedAction {
	public MoveCmd(AbsPos target) {
		super(target);
	}

	@Override
	public void executeAll(World world, Iterable<BotEntity> actors) {
		BufferedCollection<BotEntity> validActors = new BufferedCollection<>(actors);

		// Basic validation - remove obviously invalid MoveCmds from their actors
		for (BotEntity actor : validActors) {
			MoveCmd action = actor.getRunningAction(MoveCmd.class);

			AbsPos actorPos = world.getBotPosition(actor.getID());
			if (action.targetIllegal(actor, actorPos, world.get(action.target))) {
				removeAction(actor, action, validActors);
				continue;
			}
		}
		validActors.mergeRemoveBuffer();

		// Group validation (check two bots don't move into same spot)
		{
			MoveSim moveSim = new MoveSim(world);

			for (Simulade simulade : moveSim) {
				// all simulades that couldn't legally move to their goal
				if (simulade.getMoveState() == MoveState.READY_AT_START
						|| simulade.getMoveState() == MoveState.UNMOVED_TO_START) {

					BotEntity moveFailedBot = (BotEntity) simulade.simulatedEntity;
					MoveCmd failedCmd = moveFailedBot.getRunningAction(MoveCmd.class);

					removeAction(moveFailedBot, failedCmd, validActors);
				}
			}
		}
		validActors.mergeRemoveBuffer();

		// Execution
		for (BotEntity actor : validActors) {
			MoveCmd action = actor.getRunningAction(MoveCmd.class);
			AbsPos currPos = world.getBotPosition(actor.getID());
			AbsPos goal = action.target;

			action.exactCost(actor);
			world.swap(currPos, goal);
			actor.removeRunningAction(action);
		}

	}

	private static void removeAction(BotEntity actorBot, MoveCmd action,
			Collection<BotEntity> validActors) {
		validActors.remove(actorBot);
		actorBot.removeRunningAction(action);
	}


	public boolean targetIllegal(BotEntity actor, AbsPos startPos, Entity targetEnt) {
		// can't afford
		if (!canAfford(actor)) {
			return true;
		}

		// dist too far
		if (!validRange(startPos)) {
			return true;
		}

		// target is start
		if (startPos.equals(target)) {
			return true;
		}

		// TODO remove.  Hypothesize we don't need this check because getting outside world returns wall.  And we check for moving into wall.
		// out of bounds
		//if (!world.isInBounds(targetPos)) {
		//	return true;
		//}

		// ent@target pos
		{
			// bot planning to move is fine
			if (targetEnt instanceof BotEntity
					&& ((BotEntity)targetEnt).hasRunningAction(MoveCmd.class) == false) {
				return true;
			}

			// static entity (eg wall) bad
			if (!(targetEnt instanceof BotEntity) && !(targetEnt instanceof EmptyEntity)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public String toString() {
		return "MoveCmd [target=" + target + ", hashCode=" + hashCode() + "]";
	}

}
