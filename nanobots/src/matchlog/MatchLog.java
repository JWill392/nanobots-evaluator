package matchlog;

import entity.Entity;
import game.Game;
import game.Team;

import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import teampg.grid2d.point.AbsPos;

public class MatchLog {

	private static MatchLog inst;
	private static Logger logger = Logger.getLogger(MatchLog.class.getName());

	//private final Map<RunningAction, BotEntity> actions;

	private MatchLog(Game game) {
		//actions = new HashMap<>();

	}

	//###############
	//### CHANGES ###
	//###############
	//TODO log cmds in later Replay versions
	/*	public static void addCmd(BotEntity actor, RunningAction action) {
			assert inst.phase == Phase.GET_CMDS;

			logger.log(Level.INFO, "addCmd| " + actor + " -> " + action);

			inst.actions.put(action, actor);
		}

		public static void succeedCmd(RunningAction action) {
			assert inst.phase == Phase.RUN_ACTIONS;

			logger.log(Level.INFO, "succeedCmd| " + inst.actions.get(action) + " -> " + action);

			inst.actions.remove(action);
		}

		public static void failCmd(RunningAction action) {
			assert inst.phase == Phase.RUN_ACTIONS;

			logger.log(Level.INFO, "succeedCmd| " + inst.actions.get(action) + " -> " + action);

			inst.actions.remove(action);
		}*/

	public static void addEntity(Entity newEnt, AbsPos pos) {
		logger.log(Level.INFO, "addEnt| " + newEnt + " -> " + pos);
	}

	public static void removeEntity(Entity ent) {
		logger.log(Level.INFO, "removeEnt| " + ent);
	}

	//#############
	//### PHASE ###
	//#############
	public static void startMatch(Game game) {
		assert inst == null;
		inst = new MatchLog(game);
		//TODO
		logger.log(Level.INFO, "startMatch| " + game);
	}

	public static void endMatch(Team winner, Path replayDirectory) {
		//TODO
	}
}
