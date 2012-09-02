package jwill392.nanobotsreplay.world;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Range;
import com.google.common.collect.Ranges;

import replay.ReplayProto.Replay;
import replay.ReplayProto.Replay.TurnInfo;

/**
 * Every frame of a Replay Entity 's life
 */
public class EntityModel {
	private final Range<Integer> lifespan;
	private final List<Replay.Entity> turnEntities;

	public EntityModel(Replay rep, int eid) {
		int birthTurn = -1;
		int lastLivingTurn = -1;

		turnEntities = new ArrayList<>();
		for (int i = 0; i < rep.getTurnsCount(); i++) {
			TurnInfo turn = rep.getTurns(i);

			Replay.Entity turnEnt = replay.Util.getEntFromTurn(turn, eid);
			if (turnEnt == null && birthTurn == -1) {
				// before birth
				continue;
			}
			if (turnEnt == null) {
				// after death
				break;
			}

			// found birth date
			if (birthTurn == -1) {
				birthTurn = i;
			}

			lastLivingTurn = i;
			turnEntities.add(turnEnt);
		}

		assert birthTurn != -1;
		lifespan =  Ranges.closed(birthTurn, lastLivingTurn);
	}

	public Range<Integer> getLifespan() {
		return lifespan;
	}

	public Replay.Entity onTurn(int turn) {
		assert getLifespan().contains(turn) : "Given turn " + turn + " is outside lifetime" + getLifespan() + "\n\n\n" + turnEntities;
		return turnEntities.get(turn - lifespan.lowerEndpoint()); // FIXME above says valid, but this get gets out of range for 2086.
	}

	public boolean hasTurn(int turn) {
		return getLifespan().contains(turn);
	}
}
