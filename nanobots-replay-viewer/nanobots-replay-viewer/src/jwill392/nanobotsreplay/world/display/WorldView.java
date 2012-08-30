package jwill392.nanobotsreplay.world.display;

import static com.google.common.base.Preconditions.checkArgument;

import java.awt.Dimension;

import jwill392.nanobotsreplay.NBRV;
import jwill392.nanobotsreplay.assets.Assets;
import jwill392.nanobotsreplay.ui.AbstractUIComponent;
import jwill392.nanobotsreplay.ui.UIComponent;
import jwill392.nanobotsreplay.world.EntityModel;
import jwill392.nanobotsreplay.world.WorldModel;
import jwill392.nanobotsreplay.world.WorldModel.ModelTurnChange;
import jwill392.nanobotsreplay.world.display.ent.WorldDisplayEntity;
import jwill392.slickutil.SillySlickFixes;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import com.google.common.eventbus.Subscribe;

import replay.Util;
import teampg.grid2d.RectGrid;
import teampg.grid2d.point.AbsPos;

public class WorldView extends UIComponent {
	private static final int ZOOM = 1; //TODO use

	private WorldModel worldData;
	private RectGrid<WorldDisplayEntity> grid;

	private final Image gridImg;
	private static final AbsPos GRID_SPACING = AbsPos.of(3, 3);

	public WorldView(Rectangle drawArea) {
		this(drawArea, AbstractUIComponent.getRoot());
	}
	public WorldView(Rectangle drawArea, UIComponent parent) {
		this(drawArea, (AbstractUIComponent)parent);
	}
	private WorldView(Rectangle drawArea, AbstractUIComponent parent) {
		super(drawArea, parent);
		gridImg = Assets.getSheet("assets/spritesheet").getSprite("grid.png");
	}

	public void connectWorldModel(WorldModel model) {
		worldData = model;

		grid = new RectGrid<>(worldData.getSize());
		setTurn(0);
	}
	public WorldModel getModel() {
		return worldData;
	}

	public Vector2f getAbsolutePos(AbsPos of) {
		return new Vector2f(
				getDrawArea().getX() + gridImg.getWidth() * of.x + GRID_SPACING.x,
				getDrawArea().getY() + gridImg.getHeight() * of.y + GRID_SPACING.y
				);
	}

	public void render(GameContainer container, Graphics g) throws SlickException {
		if (worldData == null) {
			return;
		}

		drawTiled(gridImg, getDrawArea(), ZOOM, worldData.getSize());
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
		for (EntityModel ent : worldData) {
			grid.set(Util.of(ent.getTurn(turn).getPos()), WorldDisplayEntity.getEnt(this, ent));
		}
	}

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
		checkArgument(SillySlickFixes.contains(drawArea, tiledAreaInPixels), "Render area not big enough to draw full world");

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
