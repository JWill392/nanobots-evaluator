package brain;


public abstract class BotBrain {
	private final String name;

	protected abstract BrainCommand brainDecideAction(BrainInfo info)
			throws Exception;

	public BotBrain(String brainName) {
		name = brainName;
	}

	public String getName() {
		return name;
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

	@Override
	public boolean equals(Object what) {
		BotBrain other = (BotBrain) what;

		return name.equals(other.name);
	}
}
