package brain;


public abstract class BotBrain {
	protected abstract BrainCommand brainDecideAction(BrainInfo info)
			throws Exception;

	public BotBrain() {
	}

	public BrainCommand decideAction(BrainInfo info) {
		// TODO-DESIGN Sandbox
		BrainCommand brainAction;

		try {
			brainAction = brainDecideAction(info);
		} catch (Exception e) {
			//TODO make BotBrain errors visible
			e.printStackTrace(System.err);
			brainAction = new BrainCommand(null, null);
		}

		return brainAction;
	}
}
