import java.awt.Color;
import java.awt.Graphics2D;

public class DraggableLine {

	static final int tolerance = 10;
	boolean enabled = false;
	private int xDrawn, xReal;
	boolean dragging = false, left;
	private double lastXscale;
	
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
		lastXscale = xScale;
		xReal =  (int)(x*xScale);
		xDrawn = (int)(xReal/xScale);
	}
	
	public void setXReal(int x, double xScale) {
		lastXscale = xScale;
		xReal = x;
		xDrawn = (int)(x/xScale);
	}
	
	public void setXReal(int x) {
		xReal = x;
		xDrawn = (int)(x/lastXscale);
	}
	
	public void changeScale(double xScale) {
		lastXscale = xScale;
		xDrawn = (int)(xReal/xScale);
	}
	
	public int getXDrawn() {
		return xDrawn;
	}
	
	public int getXReal() {
		return xReal;
	}
	
}
