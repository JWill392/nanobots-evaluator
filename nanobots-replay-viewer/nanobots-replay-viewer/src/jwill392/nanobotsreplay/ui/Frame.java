package jwill392.nanobotsreplay.ui;

import jwill392.nanobotsreplay.assets.Assets;
import jwill392.nanobotsreplay.util.ImgUtil;
import jwill392.slickutil.SlickUtil;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.gui.GUIContext;

public class Frame extends UIComponent {
	private final Image frameImg;
	private UIComponent content;
	private final Rectangle framedArea;

	public Frame(Rectangle drawArea) {
		this(drawArea, AbstractUIComponent.getRoot());
	}

	public Frame(Rectangle drawArea, UIComponent parent) {
		this(drawArea, (AbstractUIComponent)parent);
	}

	protected Frame(Rectangle drawArea, AbstractUIComponent parent) {
		super(drawArea, parent);

		Image frameTheme = Assets.getSheet("assets/spritesheet").getSprite("frame.gif");
		frameImg = ImgUtil.buildPanelImage(frameTheme, SlickUtil.getRectDim(drawArea), 6, 6, 8, 8);


		framedArea = SlickUtil.copy(drawArea);
		framedArea.setBounds(
				getDrawArea().getX() + 6,
				getDrawArea().getY() + 6,
				getDrawArea().getWidth() - frameTheme.getWidth() + 2,
				getDrawArea().getHeight() - frameTheme.getHeight() + 2);
	}

	@Override
	protected void draw(GUIContext container, Graphics g) throws SlickException {
		frameImg.draw(getDrawArea().getX(), getDrawArea().getY());
	}

	public void setContents(UIComponent newContent) {
		if (content != null) {
			removeChild(content);
		}

		content = newContent;
		newContent.setDrawArea(framedArea);

		addChild(newContent);
	}

	public Rectangle getFramedArea() {
		return SlickUtil.copy(framedArea);
	}
}
