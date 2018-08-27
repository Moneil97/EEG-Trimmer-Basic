import java.awt.Color;
import java.awt.Graphics2D;

public class DraggableLine {

	static final int tolerance = 10;
	int xDrawn;
	double xReal;
	boolean dragging = false, left;
	
	public DraggableLine(int x, double xScale, boolean left) {
		xDrawn = x;
		xReal = x/xScale;
		this.left = left;
	}

	public void draw(Graphics2D g, int height) {
		g.setColor(Color.blue);
		g.drawLine(xDrawn, 0, xDrawn, height);
		g.setColor(new Color(0, 0, 1, .5f));
		if (left)
			g.fillRect(0, 0, xDrawn, height);
	}

	public void checkMouse(int mouseX) {
		if (xDrawn + tolerance > mouseX && xDrawn - tolerance < mouseX) {
			System.out.println("line grabbed");
			dragging = true;
		}
	}
	
}
