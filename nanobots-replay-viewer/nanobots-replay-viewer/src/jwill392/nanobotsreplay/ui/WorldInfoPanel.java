package jwill392.nanobotsreplay.ui;

import jwill392.nanobotsreplay.assets.Assets;
import jwill392.nanobotsreplay.util.ImgUtil;
import jwill392.nanobotsreplay.world.EntityModel;
import jwill392.nanobotsreplay.world.WorldModel.ModelTurnChange;
import jwill392.nanobotsreplay.world.display.WorldView.SelectedEntityChange;
import jwill392.slickutil.SillySlickFixes;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.gui.GUIContext;

import com.google.common.eventbus.Subscribe;

public class WorldInfoPanel extends UIComponent {
	private int turn = -1;
	private EntityModel selected;
	private final Image background;

	public WorldInfoPanel(Rectangle drawArea, UIComponent parent) throws SlickException {
		this(drawArea, (AbstractUIComponent) parent);
	}

	public WorldInfoPanel(Rectangle drawArea) throws SlickException {
		this(drawArea, AbstractUIComponent.getRoot());
	}

	protected WorldInfoPanel(Rectangle drawArea, AbstractUIComponent parent) throws SlickException {
		super(drawArea, parent);
		Image panelBackgroundTheme = Assets.getSheet("assets/spritesheet").getSprite("panel.png");

		background = ImgUtil.buildPanelImage(panelBackgroundTheme, SillySlickFixes.getRectDim(drawArea), 6, 6, 8, 8);
	}

	@Override
	public void render(GUIContext container, Graphics g) throws SlickException {
		super.render(container, g);
		background.draw(getDrawArea().getX(), getDrawArea().getY());
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
			System.out.println(selected.getTurn(newTurn));
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
		System.out.println(selected.getTurn(turn));
	}
}
