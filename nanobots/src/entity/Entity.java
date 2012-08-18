package entity;

import game.Team;

public abstract class Entity {
	public static final Class<? extends Entity> WALL = WallEntity.class;
	public static final Class<? extends Entity> FOOD = FoodEntity.class;
	public static final Class<? extends Entity> EMPTY = EmptyEntity.class;
	public static final Class<? extends Entity> BOT = BotEntity.class;

	public static EmptyEntity getNewEmpty() {
		return new EmptyEntity();
	}

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
