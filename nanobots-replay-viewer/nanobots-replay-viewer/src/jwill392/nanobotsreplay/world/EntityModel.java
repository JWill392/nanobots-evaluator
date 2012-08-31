package jwill392.nanobotsreplay.world;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Range;
import com.google.common.collect.Ranges;

import replay.ReplayProto.Replay;

/**
 * Every frame of a Replay Entity 's life
 */
public class EntityModel {
	private int birthTurn;
	private final List<Replay.Entity> turnEntities;

	public EntityModel(Replay rep, int eid) {
		int turnIndex = -1;
		birthTurn = -1;

		turnEntities = new ArrayList<>();
		for (Replay.TurnInfo turn : rep.getTurnsList()) {
			turnIndex++;

			Replay.Entity turnEnt = replay.Util.get(turn, eid);
			if (turnEnt == null && birthTurn == -1) {
				// before birth
				continue;
			}
			if (turnEnt == null && birthTurn != -1) {
				// after death
				break;
			}

			if (birthTurn == -1) {
				birthTurn = turnIndex;
			}

			turnEntities.add(turnEnt);
		}
	}

	public Range<Integer> getLifespan() {
		return Ranges.closed(birthTurn, birthTurn + turnEntities.size() - 1);
	}

	public Replay.Entity getTurn(int turn) {
		checkArgument(getLifespan().contains(turn), "Given turn is outside lifetime");
		return turnEntities.get(turn - birthTurn); // FIXME above says valid, but this get gets out of range for 2086.
	}

	public boolean hasTurn(int turn) {
		return getLifespan().contains(turn);
	}
}
