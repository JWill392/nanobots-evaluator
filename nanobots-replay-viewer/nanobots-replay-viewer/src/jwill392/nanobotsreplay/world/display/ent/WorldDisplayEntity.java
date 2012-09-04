package jwill392.nanobotsreplay.world.display.ent;

import java.awt.Dimension;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.gui.GUIContext;

import replay.Util;
import teampg.grid2d.point.AbsPos;

import jwill392.nanobotsreplay.ui.AbstractUIComponent;
import jwill392.nanobotsreplay.world.EntityModel;
import jwill392.nanobotsreplay.world.display.WorldView;

public abstract class WorldDisplayEntity extends AbstractUIComponent {
	public static final Dimension DISP_ENT_SIZE = new Dimension(31, 31);
	protected final EntityModel data;

	public WorldDisplayEntity(Vector2f pos, EntityModel data) {
		super(DISP_ENT_SIZE, pos);
		this.data = data;

	}

	protected int getTurn() {
		return ((WorldView)getParent()).getModel().getTurn();
	}

	protected abstract void draw(float x, float y);

	public EntityModel getData() {
		return data;
	}

	public AbsPos getGridPos(int turn) {
		return Util.of(data.onTurn(turn).getPos());
	}

	@Override
	protected void draw(GUIContext container, Graphics g) throws SlickException {
		draw(getAbsPos().x, getAbsPos().y);
	}

	public static WorldDisplayEntity getEnt(Vector2f pos, EntityModel data) {
		int someLivingTurn = data.getLifespan().lowerEndpoint();
		switch (data.onTurn(someLivingTurn).getType()) {
		case BOT:
			return new DisplayBot(pos, data);
		case FOOD:
			return new DisplayFood(pos, data);
		case WALL:
			return new DisplayWall(pos, data);
		default:
			throw new IllegalStateException();
		}
	}
}
