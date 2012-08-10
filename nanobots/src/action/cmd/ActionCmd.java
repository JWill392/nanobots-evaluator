package action.cmd;

public abstract class ActionCmd {

	public static boolean sameActionType(ActionCmd a, ActionCmd b) {
		Class<? extends ActionCmd> aType = a.getClass();
		Class<? extends ActionCmd> bType = b.getClass();

		return aType.equals(bType);
	}
}
