package old_get_rid_of;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import ca.camosun.jwill392.datatypes.collections.HashAutoSplitSet;
import ca.camosun.jwill392.datatypes.collections.SubtypeSplitSet;


import action.cmd.ActionCmd;
import action.cmd.TargettedAction;

public class TurnActions {
	private SubtypeSplitSet<ActionCmd> validSet;
	private Set<ActionCmd> invalidSet;

	public TurnActions() {
		validSet = new HashAutoSplitSet<>();
		invalidSet = new HashSet<ActionCmd>();
	}

	/**
	 * Add a new action of some type to this collection.
	 * 
	 * @param action
	 *            Action to add. Not copied.
	 */
	public <T extends ActionCmd> void add(T action) {
		validSet.add(action);
	}

	/**
	 * Set an action as invalid. Irreversable.
	 * 
	 * @param nowInvalidAction
	 *            BotAction instance to move to invalid.
	 */
	public <T extends ActionCmd> void setInvalid(T nowInvalidAction) {
		// should never get abstract base classes
		assert (nowInvalidAction.getClass() != ActionCmd.class);
		assert (nowInvalidAction.getClass() != TargettedAction.class);
		//thing being removed should already be there
		assert (validSet.contains(nowInvalidAction));

		invalidSet.add((ActionCmd) nowInvalidAction);
		validSet.remove(nowInvalidAction);
	}

	/**
	 * Get iterable object for all valid actions of one type.
	 * 
	 * @param actionType
	 *            Type of actions to get.
	 * @return Iterable object for specified action type
	 */

	public <T extends ActionCmd> Iterable<T> getValidActions(Class<T> actionType) {
		return validSet.getIterable(actionType);
	}

	/**
	 * Get iterable object for all invalid actions of all types
	 * 
	 * @return Iterable object for all invalid BotActions
	 */
	public Iterable<ActionCmd> getInvalidActions() {
		return new Iterable<ActionCmd>() {

			@Override
			public Iterator<ActionCmd> iterator() {
				return invalidSet.iterator();
			}
		};
	}
}