package brain;


import java.util.Comparator;

import com.google.common.base.Predicate;

import replay.ReplayProto.Replay.Entity.ReceivedMessage;
import teampg.grid2d.point.AbsPos;
import teampg.grid2d.point.Pos2D;
import teampg.grid2d.point.RelPos;
import teampg.util.Util;

public class BrainUtil {
	public static AbsPos getMoveTowards(AbsPos from, AbsPos to) {
		RelPos moveVector = RelPos.offsetVector(from, to).unitVector();

		if (moveVector.squareMagnitude() > 1 ) {
			moveVector = Util.choice(RelPos.of(moveVector.x, 0), RelPos.of(0, moveVector.y));
		}

		return Pos2D.offset(from, moveVector);
	}

	public static Comparator<ReceivedMessage> byProximity(final AbsPos pos) {
		final Comparator<Pos2D> pointComp = new Pos2D.DistanceComparator(pos);
		return new Comparator<ReceivedMessage>() {
			@Override
			public int compare(ReceivedMessage a, ReceivedMessage b) {
				return pointComp.compare(replay.Util.of(a.getOrigin()), replay.Util.of(b.getOrigin()));
			}
		};
	}

	public static Predicate<ReceivedMessage> byMessageBodyEquals(final int body) {
		return new Predicate<ReceivedMessage>() {
			@Override
			public boolean apply(ReceivedMessage msg) {
				return msg.getBody() == body;
			}
		};
	}
}
