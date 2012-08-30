package jwill392.slickutil;

import java.awt.Dimension;

import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

public class SillySlickFixes {
	/**
	 * Two rects of same size are NOT considered to contain each other
	 */
	public static boolean contains(Rectangle a, Rectangle b) {
		float ax = a.getX();
		float ay = a.getY();
		float aw = a.getWidth();
		float ah = a.getHeight();

		float bx = b.getX();
		float by = b.getY();
		float bw = b.getWidth();
		float bh = b.getHeight();

		return ax < bx && (bx + bw) < (ax + aw) &&
				ay < by && (by + bh) < (ay + ah);
	}

	/**
	 * Two rects of same size ARE considered to contain each other
	 */
	public static boolean containsNotStrict(Rectangle a, Rectangle b) {
		float ax = a.getX();
		float ay = a.getY();
		float aw = a.getWidth();
		float ah = a.getHeight();

		float bx = b.getX();
		float by = b.getY();
		float bw = b.getWidth();
		float bh = b.getHeight();

		return ax <= bx && (bx + bw) <= (ax + aw) &&
				ay <= by && (by + bh) <= (ay + ah);
	}

	public static Rectangle copy(Rectangle r) {
		return new Rectangle(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	public static Rectangle newRect(Vector2f topLeft, float width, float height) {
		return new Rectangle(topLeft.x, topLeft.y, width, height);
	}

	public static Rectangle newRect(float lx, float ty, float rx, float by) {
		return new Rectangle(lx, ty, rx - lx, by - ty);
	}

	public static Dimension getRectDim(Rectangle rect) {
		return new Dimension((int)rect.getWidth(), (int)rect.getHeight());
	}
}
