package jwill392.nanobotsreplay.ui;

import java.awt.Dimension;

import jwill392.nanobotsreplay.assets.Assets;
import jwill392.nanobotsreplay.util.ImgUtil;
import jwill392.slickutil.SlickUtil;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.gui.GUIContext;

public class Frame extends AbstractUIComponent {
	private final Image frameImg;
	private AbstractUIComponent content;
	private final Rectangle relFramedArea;

	public Frame(Dimension drawSize, Vector2f drawPos) {
		super(drawSize, drawPos);

		Image frameTheme = Assets.getSheet("assets/spritesheet").getSprite("frame.gif");
		frameImg = ImgUtil.buildPanelImage(frameTheme, drawSize, 6, 6, 8, 8);

		relFramedArea = new Rectangle(
				6,
				6,
				- frameTheme.getWidth() + 2,
				- frameTheme.getHeight() + 2);
	}

	@Override
	protected void draw(GUIContext container, Graphics g) throws SlickException {
		frameImg.draw(getAbsPos().x, getAbsPos().y);
	}

	public void setContents(AbstractUIComponent newContent) {
		if (content != null) {
			removeChild(content);
		}

		content = newContent;
		newContent.setSize(new Dimension(
				getSize().width + (int) relFramedArea.getWidth(),
				getSize().height + (int) relFramedArea.getHeight()));
		newContent.setRelPos(SlickUtil.getPos(relFramedArea));

		addChild(newContent);
	}

	public Rectangle getFramedArea() {
		return SlickUtil.copy(relFramedArea);
	}

	@Override
	protected void tick(GameContainer container, int delta) throws SlickException {
	}
}
