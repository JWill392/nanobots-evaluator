package action;

import java.util.List;

import replay.ReplayProto.Replay;
import replay.ReplayProto.Replay.Action.Type;
import replay.ReplayProto.Replay.Entity.BotState;
import teampg.grid2d.point.AbsPos;

import com.google.common.collect.ImmutableList;

import entity.BotEntity;
import entity.Entity;
import entity.bot.Memory;
import game.Settings;
import game.world.World;

public class BirthCmd extends TargettedAction {
	public BirthCmd(AbsPos target, Memory newbornMemory) {
		super(target);

		data.setNewbornMemory(newbornMemory.getAll());
	}

	static void executeAll(World world, List<BotEntity> actors) {
		filterBasicInvalid(world, actors);

		for (BotEntity bot : actors) {
			BirthCmd action = (BirthCmd) bot.getRunningAction();

			if (bot.getElapsedGestation() < Settings.getGestationDuration()) {
				action.fail(Replay.Action.Outcome.GESTATION_NOT_COMPLETE);
				continue;
			}

			bot.setState(Replay.Entity.BotState.NORMAL);
			action.succeed(bot);

			// create newborn
			BotEntity newborn = Entity.getNewBot(bot.getTeam());
			newborn.setMemory(new Memory(action.data.getNewbornMemory()));
			world.addNewEntity(action.getTarget(), newborn);
		}
	}

	@Override
	protected int getCost() {
		return Settings.getActionCost(BirthCmd.class);
	}

	@Override
	protected ImmutableList<BotState> getLegalActorStates() {
		return ImmutableList.of(BotState.GESTATING);
	}

	@Override
	public Type getType() {
		return Replay.Action.Type.BIRTH;
	}

	@Override
	public String toString() {
		return "BirthCmd [getTarget()=" + getTarget() + "]";
	}

}
