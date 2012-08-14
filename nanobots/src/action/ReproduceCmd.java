package action;

import entity.BotEntity;
import game.world.World;
import teampg.grid2d.point.AbsPos;

public class ReproduceCmd extends TargettedAction {
	public ReproduceCmd(AbsPos target) {
		super(target);
	}

	@Override
	public void executeAll(World world, Iterable<BotEntity> actors) {
		// TODO Auto-generated method stub

	}
}
