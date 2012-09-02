package jwill392.nanobotsreplay.ui;

import java.awt.Dimension;

import jwill392.nanobotsreplay.assets.Assets;
import jwill392.nanobotsreplay.util.ImgUtil;
import jwill392.nanobotsreplay.world.EntityModel;
import jwill392.nanobotsreplay.world.WorldModel.ModelTurnChange;
import jwill392.nanobotsreplay.world.display.WorldView.SelectedEntityChange;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheetFont;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.gui.GUIContext;

import replay.ReplayProto.Replay.Entity;
import com.google.common.eventbus.Subscribe;

public class WorldInfoPanel extends UIComponent {
	private int turn = -1;
	private EntityModel selected;
	private final Image mainPanel;
	private final Image turnPanel;
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

		Dimension turnPanelSize = new Dimension((int) getDrawArea().getWidth(), 30);
		Dimension mainPanelSize = new Dimension((int) getDrawArea().getWidth(), (int) getDrawArea().getHeight() - 25);
		turnPanel = ImgUtil.buildPanelImage(panelBackgroundTheme, turnPanelSize, 6, 6, 8, 8);
		mainPanel = ImgUtil.buildPanelImage(panelBackgroundTheme, mainPanelSize, 6, 6, 8, 8);

		font = Assets.getFont(2);
	}

	@Override
	public void render(GUIContext container, Graphics g) throws SlickException {
		super.render(container, g);
		turnPanel.draw(getDrawArea().getX(), getDrawArea().getY());
		mainPanel.draw(getDrawArea().getX(), getDrawArea().getY() + 30);

		font.drawString(getDrawArea().getX() + 10, getDrawArea().getY() + 10, "Turn: " + turn);

		if (selected != null) {
			Entity turnInfo = selected.onTurn(turn);

			font.drawString(getDrawArea().getX() + 10, getDrawArea().getY() + 40, "ID: " + turnInfo.getEid());
			font.drawString(getDrawArea().getX() + 10, getDrawArea().getY() + 60, "Energy: " + turnInfo.getEnergy());

			if (turnInfo.hasRunningAction()) {
				font.drawString(
						getDrawArea().getX() + 10, getDrawArea().getY() + 80,
						"Action: " + turnInfo.getRunningAction().getType());
				font.drawString(
						getDrawArea().getX() + 10, getDrawArea().getY() + 100,
						"Outcome: " + turnInfo.getRunningAction().getOutcome());
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
		turn = newTurn;
	}

	@Subscribe
	public void turnChanged(ModelTurnChange e) {
		if (selected != null && !selected.hasTurn(e.newTurn)) {
			selected = null;
		}

		setTurn(e.newTurn);
	}

	@Subscribe
	public void selectedEntityChanged(SelectedEntityChange e) {
		setSelected(e.selected);

		if (e.selected == null) {
			return;
		}

		System.out.println(selected.onTurn(turn));
	}
}
