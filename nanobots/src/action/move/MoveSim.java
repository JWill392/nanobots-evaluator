package action.move;

import java.util.Iterator;

import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;

import action.move.Simulade.MoveState;

import entity.Entity;

import game.world.World;
import teampg.grid2d.GridInterface;
import teampg.grid2d.GridInterface.Entry;
import teampg.grid2d.RectGrid;
import teampg.grid2d.point.AbsPos;

public class MoveSim implements Iterable<Simulade> {
	private final GridInterface<Simulade> grid;

	public MoveSim(World world) {
		grid = new RectGrid<>(world.getSize());

		// fill in with simulades
		for (Entry<Entity> entry : world.getEntries()) {
			Entity entity = entry.getContents();
			AbsPos position = entry.getPosition();

			if (entity == null) {
				continue;
			}

			grid.set(position, new Simulade(entry));
		}

		//TODO run simulation
		for (int y = 0; y < grid.getSize().height; y++) {
			for (int x = 0; x < grid.getSize().width; x++) {
				AbsPos pos = AbsPos.of(x, y);
				Simulade sim = grid.get(pos);

				if (sim == null) {
					continue;
				}

				if (sim.getMoveState() == MoveState.READY_AT_START) {
					moveSomewhere(sim);
				}
			}
		}
	}

	private boolean moveSomewhere(Simulade sim) {
		if (sim == null) {
			return true;
		}

		if (sim.getMoveState() == MoveState.UNMOVED_TO_START ||
				sim.getMoveState() == MoveState.STATIC) {
			return false;
		}

		AbsPos current = sim.getCurrentPos();
		AbsPos target  = sim.getPossibleMove(current);

		Simulade simAtTarget = grid.get(target);

		// do move, undo later if turns out it was illegal
		sim.advanceState();
		grid.set(target, sim);
		grid.set(current, null);

		boolean moveAllowed = moveSomewhere(simAtTarget);
		if (moveAllowed == false) {
			sim.regressState();
			grid.set(target, null);
			grid.set(current, sim);
		}
		return moveAllowed;
	}

	/**
	 * Skips null simulades.
	 */
	@Override
	public Iterator<Simulade> iterator() {
		final PeekingIterator<Simulade> iter = Iterators.peekingIterator(grid.iterator());

		return new Iterator<Simulade>() {

			@Override
			public boolean hasNext() {
				while (iter.hasNext() && iter.peek() == null) {
					iter.next();
				}
				return iter.hasNext();
			}

			@Override
			public Simulade next() {
				return iter.next();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	@Override
	public String toString() {
		return grid.toString();
	}
}
