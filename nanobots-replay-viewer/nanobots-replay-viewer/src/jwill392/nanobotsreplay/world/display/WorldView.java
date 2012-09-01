package jwill392.nanobotsreplay.world.display;

import static com.google.common.base.Preconditions.checkArgument;

import java.awt.Dimension;

import jwill392.nanobotsreplay.NBRV;
import jwill392.nanobotsreplay.assets.Assets;
import jwill392.nanobotsreplay.ui.AbstractUIComponent;
import jwill392.nanobotsreplay.ui.UIComponent;
import jwill392.nanobotsreplay.util.ImgUtil;
import jwill392.nanobotsreplay.world.EntityModel;
import jwill392.nanobotsreplay.world.WorldModel;
import jwill392.nanobotsreplay.world.WorldModel.ModelTurnChange;
import jwill392.nanobotsreplay.world.display.ent.WorldDisplayEntity;
import jwill392.slickutil.SlickUtil;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.gui.GUIContext;

import com.google.common.eventbus.Subscribe;

import replay.Util;
import teampg.grid2d.RectGrid;
import teampg.grid2d.point.AbsPos;

public class WorldView extends UIComponent {
	private static final int ZOOM = 1; //TODO use
	private static final Dimension CELL_SIZE = new Dimension(34, 34);
	private static final Dimension CELL_PADDING = new Dimension(2, 2);
	private static final Dimension TABLE_BORDER = new Dimension(3, 3);

	private WorldModel worldData;
	private RectGrid<WorldDisplayEntity> grid;

	private Image worldGrid;

	public WorldView(Rectangle drawArea) {
		this(drawArea, AbstractUIComponent.getRoot());
	}
	public WorldView(Rectangle drawArea, UIComponent parent) {
		this(drawArea, (AbstractUIComponent)parent);
	}
	private WorldView(Rectangle drawArea, AbstractUIComponent parent) {
		super(drawArea, parent);


	}

	public void connectWorldModel(WorldModel model) {
		worldData = model;

		Image panelTheme = Assets.getSheet("assets/spritesheet").getSprite("grid_panel.gif");

		Dimension areaInTiles = worldData.getSize();

		Dimension gridArea = new Dimension( // TODO ugh
				panelTheme.getWidth() + (CELL_SIZE.width) * (areaInTiles.width-1),
				panelTheme.getHeight() + (CELL_SIZE.height) * (areaInTiles.height-1));

		worldGrid = ImgUtil.buildPanelImage(panelTheme, gridArea, 3, 3, 37, 37);

		grid = new RectGrid<>(worldData.getSize());
		setTurn(0);
	}
	public WorldModel getModel() {
		return worldData;
	}

	public Vector2f getAbsolutePos(AbsPos gridPos) {
		int originX = (int) getDrawArea().getX();
		int originY = (int) getDrawArea().getY();

		return new Vector2f(
				originX + (CELL_SIZE.width * gridPos.x) + CELL_PADDING.width + TABLE_BORDER.width,
				originY + (CELL_SIZE.height * gridPos.y) + CELL_PADDING.height + TABLE_BORDER.height);
	}

	@Override
	public void render(GUIContext container, Graphics g) throws SlickException {
		if (worldData == null) {
			return;
		}

		worldGrid.draw(getDrawArea().getX(), getDrawArea().getY());
		//drawTiled(gridImg, getDrawArea(), ZOOM, worldData.getSize());
		// TODO draw ents in grid
		for (WorldDisplayEntity ent : grid) {
			if (ent == null) {
				continue;
			}
			ent.render(container, g);
		}
	}

	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		if (worldData == null) {
			return;
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
		grid.fill(null);
		for (WorldDisplayEntity ent : grid) {
			if (ent == null) {
				continue;
			}
			removeChild(ent);
		}
		for (EntityModel ent : worldData) {
			grid.set(Util.of(ent.onTurn(turn).getPos()), WorldDisplayEntity.getEnt(this, ent));
		}
	}

	//TODO remove me
	public static void drawTiled(Image tile, Rectangle drawArea, int scale, Dimension areaInTiles) {
		int tileHeight = tile.getHeight() * scale;
		int tileWidth = tile.getWidth() * scale;

		int tiledAreaPixelWidth = areaInTiles.width * tileWidth;
		int tiledAreaPixelHeight = areaInTiles.height * tileHeight;

		Rectangle tiledAreaInPixels = new Rectangle(
				drawArea.getX() + 1,
				drawArea.getY() + 1,
				tiledAreaPixelWidth,
				tiledAreaPixelHeight
				);
		checkArgument(SlickUtil.contains(drawArea, tiledAreaInPixels), "Render area not big enough to draw full world");

		// TODO optimize

		for (int xt = 0; xt < areaInTiles.width; xt++) {
			for (int yt = 0; yt < areaInTiles.height; yt++) {
				tile.draw(xt * tileWidth, yt * tileHeight, scale);
			}
		}
	}


	@Override
	public void onClick(int x, int y) {
		super.onClick(x, y);

		if (getFocusedChild() instanceof WorldDisplayEntity) {
			NBRV.eventBus.post(new SelectedEntityChange(((WorldDisplayEntity)getFocusedChild()).getData()));
		}
	}

	public static class SelectedEntityChange {
		public final EntityModel selected;
		public SelectedEntityChange(EntityModel selected) {
			this.selected = selected;
		}
	}
}
