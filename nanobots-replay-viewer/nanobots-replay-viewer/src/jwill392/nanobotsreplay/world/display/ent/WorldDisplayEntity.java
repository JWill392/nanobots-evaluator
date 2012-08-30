package jwill392.nanobotsreplay.world.display.ent;

import java.awt.Dimension;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.GUIContext;

import replay.Util;
import jwill392.nanobotsreplay.ui.UIComponent;
import jwill392.nanobotsreplay.world.EntityModel;
import jwill392.nanobotsreplay.world.display.WorldView;
import jwill392.slickutil.SillySlickFixes;

public abstract class WorldDisplayEntity extends UIComponent {
	public static final Dimension DISP_ENT_SIZE = new Dimension(32, 32);
	protected final EntityModel data;

	public WorldDisplayEntity(WorldView world, EntityModel data) {
		super(
				SillySlickFixes.newRect(
						world.getAbsolutePos(Util.of(
								data.getTurn(world.getModel().getTurn()).getPos())),
						DISP_ENT_SIZE.width, DISP_ENT_SIZE.height),
				world);
		this.data = data;
	}

	protected abstract Image getImage();

	public EntityModel getData() {
		return data;
	}

	@Override
	public void render(GUIContext container, Graphics g) throws SlickException {
		super.render(container, g);

		getImage().draw(getDrawArea().getX(), getDrawArea().getY());
	}

	public static WorldDisplayEntity getEnt(WorldView world, EntityModel data) {
		switch (data.getTurn(world.getModel().getTurn()).getType()) {
		case BOT:
			return new DisplayBot(world, data);
		case FOOD:
			return new DisplayFood(world, data);
		case WALL:
			return new DisplayWall(world, data);
		default:
			throw new IllegalStateException();
		}
	}
}
