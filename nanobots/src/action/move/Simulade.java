package action.move;

import static com.google.common.base.Preconditions.checkArgument;

import action.MoveCmd;
import teampg.grid2d.GridInterface.Entry;
import teampg.grid2d.point.AbsPos;
import entity.BotEntity;
import entity.Entity;

public class Simulade {
	public final Entity simulatedEntity;

	public final AbsPos start;
	public final AbsPos goal;

	private MoveState moveState;

	public enum MoveState {
		READY_AT_START, // mobile ents start in this state.  Can move to goal.
		MOVED_TO_GOAL, // if MoveSim ends in this state, means this simulade can legally move.  Can move back to start.
		UNMOVED_TO_START, // final state for mobile.  If moveSim ends with simulade in this state, it cannot legally move.
		STATIC; // only state for not-ever-planning-to-move-this-turn entities
	}

	public Simulade(Entry<Entity> entEntry) {
		checkArgument(!(entEntry.getContents() == null));

		Entity ent = entEntry.getContents();
		AbsPos pos = entEntry.getPosition();

		if (ent instanceof BotEntity) {
			BotEntity bot = (BotEntity) ent;

			// moving simulade
			if (bot.hasRunningAction(MoveCmd.class)) {
				MoveCmd moveCmd = bot.getRunningAction(MoveCmd.class);

				AbsPos start = pos;
				AbsPos goal = moveCmd.target;

				checkArgument(!start.equals(goal));

				simulatedEntity = bot;
				moveState = MoveState.READY_AT_START;
				this.start = start;
				this.goal = goal;

				return;
			}
		}

		// static simulade
		simulatedEntity = ent;
		start = pos;
		goal = null;
		moveState = MoveState.STATIC;
	}

	public MoveState getMoveState() {
		return moveState;
	}

	public void advanceState() {
		assert moveState != MoveState.UNMOVED_TO_START;
		assert moveState != MoveState.STATIC;
		moveState = MoveState.values()[moveState.ordinal() + 1];
	}

	public void regressState() {
		assert moveState != MoveState.READY_AT_START;
		assert moveState != MoveState.STATIC;
		moveState = MoveState.values()[moveState.ordinal() - 1];
	}

	/**
	 * If planning a move, and at start, can move to goal. If at goal, can move
	 * to start. If at start but not planning a move, or have already moved back
	 * from goal, cannot move.
	 *
	 * @return Location to which can move. Null if none.
	 */
	public AbsPos getPossibleMove(AbsPos currentPos) {
		switch (moveState) {
		case UNMOVED_TO_START:
			return null;
		case READY_AT_START:
			return goal;
		case MOVED_TO_GOAL:
			return start;
		case STATIC:
			return null;
		default:
			throw new IllegalStateException();
		}
	}

	public AbsPos getCurrentPos() {
		switch (moveState) {
		case STATIC:
			return start;
		case UNMOVED_TO_START:
			return start;
		case READY_AT_START:
			return start;
		case MOVED_TO_GOAL:
			return goal;
		default:
			throw new IllegalStateException();
		}
	}

	@Override
	public String toString() {
		return "Simulade [simulatedEntity=" + simulatedEntity + ", start=" + start + ", goal="
				+ goal + ", moveState=" + moveState + "]";
	}
}