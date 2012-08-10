package old_get_rid_of;

import static org.junit.Assert.*;
import game.Settings;


import org.junit.Before;
import org.junit.Test;

import ca.camosun.jwill392.datatypes.grid2d.point.RelPos;

import tests.Util;



import action.cmd.ActionCmd;
import action.cmd.Harvest;
import action.cmd.Move;
import action.cmd.Wait;

public class TurnActionsTest {
	TurnActions someActions;
	Wait aWait;
	Move aMove;
	Move bMove;
	Move cMove;

	@Before
	public void setUp() throws Exception {
		Settings.load();

		someActions = new TurnActions();

		aWait = new Wait();
		someActions.add(aWait);

		aMove = new Move(new RelPos(1, 0));
		someActions.add(aMove);

		bMove = new Move(new RelPos(0, 1));
		someActions.add(bMove);

		cMove = new Move(new RelPos(1, 1));
		someActions.add(cMove);
	}

	@Test
	public void testAddOneType() {
		TurnActions actions = new TurnActions();

		Iterable<Wait> waitActions = actions.getValidActions(Wait.class);
		assertTrue(Util.lengthOfIterable(waitActions) == 0);

		Wait someWait = new Wait();
		actions.add(someWait);
		Wait someOtherWait = new Wait();
		actions.add(someOtherWait);

		// whenever actions changes, we have to getIterable again, else it'll be
		// outdated
		waitActions = actions.getValidActions(Wait.class);
		assertTrue(Util.lengthOfIterable(waitActions) == 2);
		assertTrue(Util.instancesInIterable(waitActions, someWait) == 1);
		assertTrue(Util.instancesInIterable(waitActions, someOtherWait) == 1);

	}

	@Test
	public void testSetInvalid() {
		Iterable<Wait> waitActions = someActions.getValidActions(Wait.class);
		assertTrue(Util.instancesInIterable(waitActions, aWait) == 1);
		assertTrue(Util.instancesInIterable(someActions.getInvalidActions(),
				(ActionCmd) aWait) == 0);

		someActions.setInvalid(aWait);
		assertTrue(Util.instancesInIterable(waitActions, aWait) == 0);
		assertTrue(Util.instancesInIterable(someActions.getInvalidActions(),
				(ActionCmd) aWait) == 1);
	}

	@Test
	public void testGetIterable() {
		Iterable<Wait> waitActions = someActions.getValidActions(Wait.class);
		assertTrue(Util.lengthOfIterable(waitActions) == 1);
		assertTrue(Util.instancesInIterable(waitActions, aWait) == 1);

		Iterable<Move> moveActions = someActions.getValidActions(Move.class);
		assertTrue(Util.lengthOfIterable(moveActions) == 3);
		assertTrue(Util.instancesInIterable(moveActions, aMove) == 1);
		assertTrue(Util.instancesInIterable(moveActions, bMove) == 1);
		assertTrue(Util.instancesInIterable(moveActions, cMove) == 1);

		// didn't add any harvest actions; should be empty iterable
		Iterable<Harvest> harvestActions = someActions
				.getValidActions(Harvest.class);
		assertTrue(Util.lengthOfIterable(harvestActions) == 0);
	}

}