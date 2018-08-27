import java.awt.Color;
import java.awt.Graphics2D;

public class DraggableLine {

	static final int tolerance = 10;
	boolean enabled = false;
	private int xDrawn;
	private double xReal;
	boolean dragging = false, left;
	
	public DraggableLine(int x, double xScale, boolean left) {
		setXDrawn(x, xScale);
		this.left = left;
	}

	public void draw(Graphics2D g, int height, int width) {
		if (enabled) {
			g.setColor(Color.blue);
			g.drawLine(xDrawn, 0, xDrawn, height);
			g.setColor(new Color(0, 0, 1, .5f));
			if (left)
				g.fillRect(0, 0, xDrawn, height);
			else
				g.fillRect(xDrawn, 0, width, height);
		}
	}

	public void checkMouse(int mouseX) {
		if (enabled) {
			if (xDrawn + tolerance > mouseX && xDrawn - tolerance < mouseX) {
				System.out.println("line grabbed");
				dragging = true;
			}
		}
	}
	
	public void setXDrawn(int x, double xScale) {
		xDrawn = x;
		System.out.println(xDrawn);
		xReal = x*xScale;
		System.out.println(xReal);
	}
	
	public void setXReal(double x, double xScale) {
		xReal = x;
		xDrawn = (int)(x/xScale);
		System.out.println(xDrawn);
		System.out.println(xReal);
	}
	
	public void changeScale(double xScale) {
		xDrawn = (int)(xReal/xScale);
		System.out.println(xDrawn);
		System.out.println(xReal);
	}
	
	public int getXDrawn() {
		return xDrawn;
	}
	
}
