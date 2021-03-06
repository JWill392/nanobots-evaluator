package entity;

import replay.ReplayProto.Replay;
import teampg.grid2d.point.AbsPos;
import game.Team;

public abstract class Entity {
	public static final Class<? extends Entity> WALL = WallEntity.class;
	public static final Class<? extends Entity> FOOD = FoodEntity.class;
	public static final Class<? extends Entity> BOT = BotEntity.class;

	protected Replay.Entity.Builder data;

	public Entity() {
		data = Replay.Entity.newBuilder()
				.setType(getType());
	}

	public Replay.Entity getData(AbsPos entPos, int eid) {
		return (data.clone()).setPos(replay.Util.of(entPos))
				.setEid(eid)
				.build();
	}

	public abstract Replay.Entity.Type getType();

	public static FoodEntity getNewFood(int energy) {
		return new FoodEntity(energy);
	}

	public static BotEntity getNewBot(Team team) {
		return BotEntity.getNewBotEntity(team);
	}

	public static WallEntity getNewWall() {
		return new WallEntity();
	}
}
