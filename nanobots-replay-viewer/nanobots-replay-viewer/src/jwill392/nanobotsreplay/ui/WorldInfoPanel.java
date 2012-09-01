package jwill392.nanobotsreplay.ui;

import jwill392.nanobotsreplay.assets.Assets;
import jwill392.nanobotsreplay.util.ImgUtil;
import jwill392.nanobotsreplay.world.EntityModel;
import jwill392.nanobotsreplay.world.WorldModel.ModelTurnChange;
import jwill392.nanobotsreplay.world.display.WorldView.SelectedEntityChange;
import jwill392.slickutil.SlickUtil;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheetFont;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.gui.GUIContext;

import replay.ReplayProto.Replay.Entity;
import teampg.grid2d.point.AbsPos;
import teampg.grid2d.point.RelPos;

import com.google.common.eventbus.Subscribe;

public class WorldInfoPanel extends UIComponent {
	private int turn = -1;
	private EntityModel selected;
	private final Image background;
	private final SpriteSheetFont font;

	public WorldInfoPanel(Rectangle drawArea, UIComponent parent) throws SlickException {
		this(drawArea, (AbstractUIComponent) parent);
	}

	public WorldInfoPanel(Rectangle drawArea) throws SlickException {
		this(drawArea, AbstractUIComponent.getRoot());
	}

	protected WorldInfoPanel(Rectangle drawArea, AbstractUIComponent parent) throws SlickException {
		super(drawArea, parent);
		Image panelBackgroundTheme = Assets.getSheet("assets/spritesheet").getSprite("panel.png");

		background = ImgUtil.buildPanelImage(panelBackgroundTheme, SlickUtil.getRectDim(drawArea), 6, 6, 8, 8);

		font = Assets.getFont(2);
	}

	@Override
	public void render(GUIContext container, Graphics g) throws SlickException {
		super.render(container, g);
		background.draw(getDrawArea().getX(), getDrawArea().getY());

		if (selected != null) {
			Entity turnInfo = selected.onTurn(turn);

			font.drawString(getDrawArea().getX() + 10, getDrawArea().getY() + 20, "ID: " + turnInfo.getEid());
			font.drawString(getDrawArea().getX() + 10, getDrawArea().getY() + 40, "Energy: " + turnInfo.getEnergy());

			if (selected.hasTurn(turn + 1)) {
				Entity nextTurn = selected.onTurn(turn + 1);

				if (nextTurn.hasRunningAction() && nextTurn.getRunningAction().hasTarget()) {
					AbsPos target = replay.Util.of(nextTurn.getRunningAction().getTarget());
					AbsPos currPos = replay.Util.of(turnInfo.getPos());

					RelPos tarVector = RelPos.offsetVector(currPos, target);

					font.drawString(
							getDrawArea().getX() + 10, getDrawArea().getY() + 60,
							"Target: " + "[" + tarVector.x + ", " + tarVector.y + "]");
					font.drawString(
							getDrawArea().getX() + 10, getDrawArea().getY() + 80,
							"Target: " + new Vector2f(tarVector.x, tarVector.y).getTheta() + " deg");
				}
			}
		}
	}

	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		super.update(container, delta);
	}

	private void setSelected(EntityModel ent) {
		selected = ent;
	}

	private void setTurn(int newTurn) {
		if (selected != null) {
			System.out.println(selected.onTurn(newTurn));
		}
		turn = newTurn;
	}

	@Subscribe
	public void turnChanged(ModelTurnChange e) {
		setTurn(e.newTurn);
	}

	@Subscribe
	public void selectedEntityChanged(SelectedEntityChange e) {
		setSelected(e.selected);
		System.out.println(selected.onTurn(turn));
	}
}
