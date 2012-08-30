package jwill392.nanobotsreplay.world.display.ent;

import org.newdawn.slick.Image;
import jwill392.nanobotsreplay.assets.Assets;
import jwill392.nanobotsreplay.world.EntityModel;
import jwill392.nanobotsreplay.world.display.WorldView;
import replay.ReplayProto.Replay;

public class DisplayBot extends WorldDisplayEntity {
	private final Image botImg;

	public DisplayBot(WorldView world, EntityModel data) {
		super(world, data);

		botImg = Assets.getSheet("assets/spritesheet").getSprite("bot.png");
		botImg.setCenterOfRotation(botImg.getWidth()/2, botImg.getHeight()/2);
	}

	@Override
	protected Image getImage() {
		return botImg;
	}
}
