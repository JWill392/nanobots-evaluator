package jwill392.nanobotsreplay.world.display;

import java.awt.Dimension;
import jwill392.nanobotsreplay.NBRV;
import jwill392.nanobotsreplay.assets.Assets;
import jwill392.nanobotsreplay.ui.AbstractUIComponent;
import jwill392.nanobotsreplay.util.ImgUtil;
import jwill392.nanobotsreplay.world.EntityModel;
import jwill392.nanobotsreplay.world.WorldModel;
import jwill392.nanobotsreplay.world.WorldModel.ModelTurnChange;
import jwill392.nanobotsreplay.world.display.ent.WorldDisplayEntity;
import jwill392.slickutil.SlickUtil;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.gui.GUIContext;

import com.google.common.eventbus.Subscribe;

import replay.Util;
import teampg.grid2d.point.AbsPos;

public class WorldView extends AbstractUIComponent {
	private static final Dimension CELL_SIZE = new Dimension(34, 34);
	private static final Dimension CELL_PADDING = new Dimension(2, 2);

	private WorldModel worldData;

	//private Image worldGrid;
	private final Image gridTheme;
	private Rectangle gridSize;

	private AbsPos panDown;
	private Vector2f panVector;
	private Vector2f viewOffset;

	public WorldView(Dimension drawSize, Vector2f drawPos) {
		super(drawSize, drawPos);

		gridTheme = Assets.getSheet("assets/spritesheet").getSprite("grid.gif");
	}

	public void connectWorldModel(WorldModel model) {
		viewOffset = new Vector2f();
		worldData = model;


		Dimension sizeInTiles = worldData.getSize();
		gridSize = new Rectangle(0, 0, // TODO ugh
				gridTheme.getWidth() * sizeInTiles.width,
				gridTheme.getHeight() * sizeInTiles.height);


		//worldGrid = ImgUtil.buildPanelImage(panelTheme, gridArea, 3, 3, 37, 37);

		setTurn(0);
	}
	public WorldModel getModel() {
		return worldData;
	}

	/**
	 * Gets drawing offset from world view pos, given a grid pos
	 */
	private Vector2f getDrawPosFromGridPos(AbsPos gridPos) {
		return new Vector2f(
				(CELL_SIZE.width * gridPos.x) + CELL_PADDING.width +  viewOffset.x,
				(CELL_SIZE.height * gridPos.y) + CELL_PADDING.height + viewOffset.y);
	}

	@Override
	protected void draw(GUIContext container, Graphics g) throws SlickException {
		if (worldData == null) {
			return;
		}

		ImgUtil.drawTiled(gridTheme, SlickUtil.of(viewOffset), ImgUtil.getRectangle(getAbsBounds()));
	}

	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		if (panDown != null) {
			// TODO check if we'd have scrolled off map.  If so, only scroll to edge of map.
			Vector2f adjustedPanVec = panVector.copy().scale(delta);

			// FIXME temp measure to stop accidentally scrolling off screen
			if (!gridSize.contains(-(viewOffset.x + adjustedPanVec.x), -(viewOffset.y + adjustedPanVec.y))) {
				return;
			}

			viewOffset.add(adjustedPanVec);
			int turnIndex = worldData.getTurn();

			for (AbstractUIComponent child : this) {
				WorldDisplayEntity dispEnt = (WorldDisplayEntity)child;

				child.setRelPos(getDrawPosFromGridPos(dispEnt.getGridPos(turnIndex)));
			}
		}
	}

	@Subscribe
	public void turnChanged(ModelTurnChange e) {
		if (worldData != e.model) {
			return;
		}

		setTurn(e.newTurn);
	}

	private void setTurn(int turn) {
		//TODO persist living ents; don't remake every turn
		removeAllChildren();
		int turnIndex = worldData.getTurn();

		for (EntityModel ent : worldData) {
			Vector2f displayEntDrawPos = getDrawPosFromGridPos(Util.of(ent.onTurn(turnIndex).getPos()));
			WorldDisplayEntity displayEnt = WorldDisplayEntity.getEnt(displayEntDrawPos, ent);

			addChild(displayEnt);
		}
	}


	@Override
	public void onPressed(int x, int y, int button) {
		super.onPressed(x, y, button);

		switch (button) {
		case Input.MOUSE_LEFT_BUTTON:
			if (getFocusedChild() instanceof WorldDisplayEntity) {
				NBRV.eventBus.post(new SelectedEntityChange(((WorldDisplayEntity)getFocusedChild()).getData()));
			} else {
				NBRV.eventBus.post(new SelectedEntityChange(null));
			}
			break;
		case Input.MOUSE_MIDDLE_BUTTON:
			panDown = AbsPos.of(x, y);
			panVector = new Vector2f();
		}
	}

	@Override
	public void mouseReleased(int button, int x, int y) {
		// TODO Auto-generated method stub
		super.mouseReleased(button, x, y);

		switch(button) {
		case Input.MOUSE_MIDDLE_BUTTON:
			panDown = null;
			panVector = null;
		}
	}

	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		super.mouseMoved(oldx, oldy, newx, newy);
		if (panDown == null) {
			return;
		}

		panVector = (new Vector2f(panDown.x, panDown.y).sub(new Vector2f(newx, newy)).scale(0.02f));
	}



	public static class SelectedEntityChange {
		public final EntityModel selected;
		public SelectedEntityChange(EntityModel selected) {
			this.selected = selected;
		}
	}
}
