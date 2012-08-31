package jwill392.nanobotsreplay.world.display.ent;

import org.newdawn.slick.Image;

import jwill392.nanobotsreplay.assets.Assets;
import jwill392.nanobotsreplay.world.EntityModel;
import jwill392.nanobotsreplay.world.display.WorldView;

public class DisplayFood extends WorldDisplayEntity {
	private final Image foodImg;

	public DisplayFood(WorldView world, EntityModel data) {
		super(world, data);

		foodImg = Assets.getSheet("assets/spritesheet").getSprite("food.png");
		foodImg.setCenterOfRotation(foodImg.getWidth()/2, foodImg.getHeight()/2);
	}

	@Override
	protected void draw(float x, float y) {
		foodImg.draw(x, y);
	}
}
