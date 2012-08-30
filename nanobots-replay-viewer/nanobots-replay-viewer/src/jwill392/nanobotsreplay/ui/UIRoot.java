package jwill392.nanobotsreplay.ui;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.geom.Rectangle;

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
		System.out.println("child added to root: " + child);
	}

	@Override
	public void onChildRemoved(UIComponent child) {
		super.onChildRemoved(child);
		allChildren.remove(child);

		container.getInput().removeMouseListener(child);
	}


}
