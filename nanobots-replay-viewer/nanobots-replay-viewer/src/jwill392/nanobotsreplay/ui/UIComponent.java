package jwill392.nanobotsreplay.ui;

import org.newdawn.slick.geom.Rectangle;

public abstract class UIComponent extends AbstractUIComponent {
	private final AbstractUIComponent parent;

	public UIComponent(Rectangle drawArea) {
		this(drawArea, AbstractUIComponent.getRoot());
	}

	public UIComponent(Rectangle drawArea, UIComponent parent) {
		this(drawArea, (AbstractUIComponent)parent);
	}

	protected UIComponent(Rectangle drawArea, AbstractUIComponent parent) {
		super(drawArea);
		this.parent = parent;
		parent.addChild(this);
	}

	public AbstractUIComponent getParent() {
		return parent;
	}

	@Override
	public void onChildAdded(UIComponent child) {
		super.onChildAdded(child);
		parent.onChildAdded(child);
	}

	@Override
	public void onChildRemoved(UIComponent child) {
		super.onChildRemoved(child);
		parent.onChildRemoved(child);
	}
}
