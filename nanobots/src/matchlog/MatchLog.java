package matchlog;

import entity.BotEntity;
import entity.Entity;
import game.Game;
import game.Team;
import game.world.World;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import replay.ReplayProto.Replay;
import teampg.grid2d.GridInterface.Entry;
import teampg.grid2d.point.AbsPos;

public class MatchLog {

	private static MatchLog inst;
	private static Logger logger = Logger.getLogger(MatchLog.class.getName());

	private final Map<Entity, Integer> entIdMap;
	private final Map<Team, Integer> teamIdMap;

	private int entIdCounter = 0;
	private int teamIdCounter = 0;

	private final Game game;

	private final Replay.Builder replayBuilder;
	private final Replay.TurnInfo.Builder currTurnBuilder;

	//private final Map<RunningAction, BotEntity> actions;

	private MatchLog(Game game) {
		//actions = new HashMap<>();
		entIdMap = new HashMap<>(2000);
		teamIdMap = new HashMap<>(10);

		this.game = game;

		replayBuilder = Replay.newBuilder();
		replayBuilder.setMapSize(
				replay.Util.of(game.getWorld().getSize()));

		for (Team team : game.getTeams()) {
			int tid = teamIdCounter;
			teamIdCounter++;

			teamIdMap.put(team, tid);
			replayBuilder.addTeams(team.getData(tid));
		}

		currTurnBuilder = Replay.TurnInfo.newBuilder();
	}

	public static int getTurnCount() {
		return inst.replayBuilder.getTurnsCount();
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
		//logger.log(Level.INFO, "addEnt| " + newEnt + " -> " + pos);
		//assert(!inst.entIdMap.containsKey(newEnt));

		//TODO needed?  I don't think so.  remove me


	}

	public static void removeEntity(Entity ent) {
		//logger.log(Level.INFO, "removeEnt| " + ent);

		//TODO
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

	private int getEid(Entity ent) {
		if (!entIdMap.containsKey(ent)) {
			int usedEid = entIdCounter;
			entIdMap.put(ent, usedEid);
			entIdCounter++;

			return usedEid;
		}

		return entIdMap.get(ent);
	}

	public static void endTurn() {
		if ((getTurnCount() % 100) == 0) {
			System.out.println("Turn#" + getTurnCount());
		}

		World world = inst.game.getWorld();
		for (Entry<Entity> entry : world.getEntries()) {
			Entity entity = entry.getContents();
			AbsPos entPos = entry.getPosition();
			int eid = inst.getEid(entity);

			Replay.Entity entData;
			if (entity instanceof BotEntity) {
				BotEntity bot = (BotEntity) entity;

				int tid = inst.teamIdMap.get(bot.getTeam());
				entData = entity.getData(entPos, eid, tid);
			} else {
				entData = entity.getData(entPos, eid);
			}

			inst.currTurnBuilder.addEnts(entData);
		}

		inst.replayBuilder.addTurns(inst.currTurnBuilder.build());
		inst.currTurnBuilder.clear();
	}

	public static void endMatch(Team winner, File outputFile) throws FileNotFoundException, IOException {
		inst.replayBuilder.setWinningTeam(inst.teamIdMap.get(winner));

		System.out.println("END MATCH, writing replay to " + outputFile.getAbsolutePath());
		// overwrite
		if (outputFile.exists()) {
			if (!outputFile.delete()) {
				throw new IllegalStateException(); //TODO cleanup
			}
		}

		Replay fullGameReplay = inst.replayBuilder.build();
		fullGameReplay.writeTo(new FileOutputStream(outputFile));
	}
}
