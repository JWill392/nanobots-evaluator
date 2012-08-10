package game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import brain.BotBrain;
import brain.BrainCommand;
import brain.BrainInfo;



/**
 * 
 * @author Jackson Williams
 */
// TODO what happens when we remove a bot from grid? Shouldn't have to remove it
// from here too...
public class Team implements Iterable<Integer> {
	private BotBrain brain;
	private Collection<Integer> memberIDs;
	
	private int teamID;
	private boolean hasLost;
	private String name;

	private static int teamIDCounter = 0;

	private Team(BotBrain inBrain, String inName, int inTeamID) {
		teamID = inTeamID;
		brain = inBrain;
		name = inName;

		memberIDs = new ArrayList<Integer>();
		hasLost = false;
	}

	public BrainCommand decideAction(BrainInfo info, int botID) {
		assert (memberIDs.contains(botID));

		BrainCommand cmd = brain.decideAction(info);
//		action.setBotID(botID);
		//TODO store botaction in bot; keep track of types in team

		return cmd;
	}

	/**
	 * Used to add newly born bots. Only called at end of turn.
	 * 
	 * @param botID
	 */
	public void addBot(int botID) {
		// should never try to add a bot already in team
		assert (memberIDs.contains(botID) == false);

		memberIDs.add(botID);
	}

	/**
	 * Used to remove dead bots. Only called at end of turn.
	 * 
	 * @param botID
	 */
	public void removeBot(int botID) {
		// should never try to remove a bot that isn't in team
		assert (memberIDs.contains(botID));

		memberIDs.remove(botID);
	}

	@Override
	public Iterator<Integer> iterator() {
		Iterator<Integer> iter = memberIDs.iterator();
		return iter;
	}

	public int getID() {
		return teamID;
	}

	public String getName() {
		return name;
	}

	public boolean hasLost() {
		return hasLost;
	}

	public void setLost() {
		hasLost = true;
	}

	@Override
	public boolean equals(Object what) {
		Team other = (Team) what;

		return name.equals(other.name);
	}
	
	public static Team getNewTeam(BotBrain inBrain, String inName) {
		Team t = new Team(inBrain, inName, getNewTeamID());
		return t;
	}
	
	private static int getNewTeamID() {
		int teamIDToUse = teamIDCounter;
		teamIDCounter++;
		return teamIDToUse;
	}
}
