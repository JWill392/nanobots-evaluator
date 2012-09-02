package jwill392.nanobotsreplay.ui;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import jwill392.slickutil.SlickUtil;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.MouseListener;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.gui.GUIContext;

public abstract class AbstractUIComponent implements Iterable<UIComponent>, MouseListener {
	private Rectangle drawArea;
	private final List<UIComponent> children;

	private boolean hidden;

	private boolean mouseDownAndStartedInsideComponent;
	private boolean mouseHover;
	private boolean focused;

	private final Queue<InputNotificationCommand> inputNotifications;

	// root stuff
	private static UIRoot root;
	public static void setRoot(Rectangle screen, GameContainer container) {
		checkArgument(root == null);
		root = new UIRoot(screen, container);
	}
	public static UIRoot getRoot() {
		return root;
	}

	public boolean isMouseDown() {
		return mouseDownAndStartedInsideComponent;
	}
	public boolean isMouseHover() {
		return mouseHover;
	}
	public boolean isFocused() {
		return focused;
	}

	public UIComponent getFocusedChild() {
		for (UIComponent child : this) {
			if (child.isFocused()) {
				return child;
			}
		}
		return null;
	}

	// can be overridden
	public void onChildAdded(UIComponent child) {
	}
	public void onChildRemoved(UIComponent child) {
	}

	public List<UIComponent> getRecursiveChildren() {
		final List<UIComponent> ret = new ArrayList<>();

		// only add children; not root
		for (UIComponent child : this) {
			teampg.util.Util.addEachBranchAndLeaf(ret, child);
		}

		return ret;
	}

	public AbstractUIComponent(Rectangle drawArea) {
		this.drawArea = drawArea;

		hidden = false;
		children = new ArrayList<>();
		inputNotifications = new LinkedList<>();
	}

	public Rectangle getDrawArea() {
		return SlickUtil.copy(drawArea);
	}

	public void setDrawArea(Rectangle drawArea) {
		this.drawArea = drawArea;
	}

	/**
	 * Can call remove; it's equivalent to calling removeChild.
	 */
	@Override
	public final Iterator<UIComponent> iterator() {
		final Iterator<UIComponent> iter = children.iterator();
		return new Iterator<UIComponent>() {
			private UIComponent curr;

			@Override
			public boolean hasNext() {
				return iter.hasNext();
			}

			@Override
			public UIComponent next() {
				curr = iter.next();
				return curr;
			}

			@Override
			public void remove() {
				if (curr == null) {
					throw new IllegalStateException();
				}

				onChildRemoved(curr);
				iter.remove();
			}
		};
	}
	public final void addChild(UIComponent child) {
		checkArgument(SlickUtil.contains(drawArea, child.getDrawArea()), drawArea + " does not contain " + child.getDrawArea());
		children.add(child);
		onChildAdded(child);
	}
	public final void removeChild(UIComponent child) {
		checkArgument(children.remove(child));
		children.remove(child);
		onChildRemoved(child);
	}
	public final void removeAllChildren() {
		for (Iterator<UIComponent> iter = iterator(); iter.hasNext();) {
			iter.next();
			iter.remove();
		}
	}

	public boolean getHidden() {
		return hidden;
	}
	public void setHidden(boolean hid) {
		hidden = hid;
	}

	public final void render(GUIContext container, Graphics g) throws SlickException {
		if (hidden) {
			return;
		}

		draw(container, g);

		for (UIComponent child : children) {
			child.render(container, g);
		}
	}

	protected abstract void draw(GUIContext container, Graphics g) throws SlickException;

	public void update(GameContainer container, int delta)
			throws SlickException {
		if (hidden) {
			return;
		}

		for (UIComponent child : children) {
			child.update(container, delta);
		}
	}



	public void onHoverStart() {
	}
	public void onHoverEnd() {
	}
	public void onPressed(int x, int y) {
	}
	public void onClick(int x, int y) {
	}
	public void onFocus() {
	}
	public void onBlur() {
	}


	@Override
	public void setInput(Input input) {
	}
	@Override
	public boolean isAcceptingInput() {
		return true;
	}
	@Override
	public void inputEnded() {
		while (!inputNotifications.isEmpty()) {
			InputNotificationCommand cmd = inputNotifications.remove();
			cmd.execute();
		}
	}
	@Override
	public void inputStarted() {
	}
	@Override
	public void mouseWheelMoved(int change) {
	}
	@Override
	public void mouseClicked(int button, int x, int y, int clickCount) {
	}
	@Override
	public void mousePressed(int button, int x, int y) {
		assert !mouseDownAndStartedInsideComponent;
		if (drawArea.contains(x, y)) {
			mouseDownAndStartedInsideComponent = true;
			inputNotifications.add(new OnClick(true, x, y));

			if (!focused) {
				focused = true;
				inputNotifications.add(new OnFocus(true));
				onFocus();
			}

		} else {
			if (focused) {
				focused = false;
				inputNotifications.add(new OnFocus(false));
			}

		}
	}
	@Override
	public void mouseReleased(int button, int x, int y) {
		if (drawArea.contains(x, y) && mouseDownAndStartedInsideComponent) {
			inputNotifications.add(new OnClick(false, x, y));
		}
		mouseDownAndStartedInsideComponent = false;
	}
	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		// moved outside component
		if (!drawArea.contains(newx, newy) && mouseHover) {
			mouseHover = false;
			inputNotifications.add(new OnHover(false));
			return;
		}

		// moved onto component
		if (!mouseHover) {
			inputNotifications.add(new OnHover(true));
			mouseHover = true;
			return;
		}

		// already hovering, moving within component.  Don't care.
	}
	@Override
	public void mouseDragged(int oldx, int oldy, int newx, int newy) {
		mouseMoved(oldx, oldy, newx, newy);
	}

	// HERE BE DRAGONS
	private abstract class InputNotificationCommand {
		final boolean start;
		public InputNotificationCommand(boolean start) {
			this.start = start;
		}
		abstract void execute();
	}
	private class OnHover extends InputNotificationCommand {
		public OnHover(boolean start) {
			super(start);
		}

		@Override
		void execute() {
			if (start) {
				onHoverStart();
			} else {
				onHoverEnd();
			}
		}
	}
	private class OnFocus extends InputNotificationCommand {
		public OnFocus(boolean start) {
			super(start);
		}

		@Override
		void execute() {
			if (start) {
				onFocus();
			} else {
				onBlur();
			}
		}
	}
	private class OnClick extends InputNotificationCommand {
		final int x;
		final int y;
		public OnClick(boolean start, int x, int y) {
			super(start);
			this.x = x;
			this.y = y;
		}

		@Override
		void execute() {
			if (start) {
				onPressed(x, y);
			} else {
				onClick(x, y);
			}
		}
	}
}
