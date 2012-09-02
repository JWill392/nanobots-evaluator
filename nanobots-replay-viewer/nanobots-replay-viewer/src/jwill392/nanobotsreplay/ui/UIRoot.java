package jwill392.nanobotsreplay.ui;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.gui.GUIContext;

public class UIRoot extends AbstractUIComponent {
	private final List<UIComponent> allChildren;
	private final GameContainer container;

	UIRoot(Rectangle drawArea, GameContainer container) {
		super(drawArea);

		allChildren = new ArrayList<>();
		this.container = container;
		container.getInput().addMouseListener(this);
	}

	@Override
	public void onChildAdded(UIComponent child) {
		super.onChildAdded(child);
		allChildren.add(child);

		container.getInput().addMouseListener(child);
	}

	@Override
	public void onChildRemoved(UIComponent child) {
		super.onChildRemoved(child);
		allChildren.remove(child);

		container.getInput().removeMouseListener(child);
	}

	@Override
	protected void draw(GUIContext container, Graphics g) throws SlickException {
	}
}
