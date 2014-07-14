package testing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;

import testing.Note.Pitch;

public class DrawableKey extends JComponent implements MouseListener {

	private static final long serialVersionUID = -928060735947826720L;

	public enum Shape {
		L, R, T, F, B
	}

	public static final int BLACK_KEY_WIDTH = 30, BLACK_KEY_HEIGHT = 140,
			WHITE_KEY_WIDTH = 50, WHITE_KEY_HEIGHT = 200;

	private Shape shape;
	private Polygon drawArea;
	private final Pitch pitch;
	private boolean isPressed;

	private final boolean special;

	public DrawableKey(Pitch pitch, boolean special, int xCoord, int yCoord) {
		// (xCoord, yCoord) are the coordinates of the top left corner of the
		// key.
		this.pitch = pitch;
		this.special = special;
		this.isPressed = false;
		this.addMouseListener(this);
		// set shape
		deriveShape();
		// set drawArea
		deriveDrawArea(xCoord, yCoord);
	}

	public DrawableKey(Pitch pitch, int xCoord, int yCoord) {
		this(pitch, false, xCoord, yCoord);
	}

	public void play() {
		isPressed = true;
		repaint();
		System.out.println(pitch);
	}

	@Override
	protected void paintComponent(Graphics g) {
		if (isPressed) {
			if (shape == Shape.B)
				g.setColor(Color.DARK_GRAY);
			else
				g.setColor(Color.GRAY);
			g.fillPolygon(drawArea);
			g.setColor(Color.BLACK);
			g.drawPolygon(drawArea);
		} else {
			g.setColor(Color.BLACK);
			if (shape == Shape.B) {
				g.fillPolygon(drawArea);
			}
			g.drawPolygon(drawArea);
		}
		System.out.println("painted");
	}

	@Override
	public Dimension getPreferredSize() {
		return drawArea.getBounds().getSize();
	}

	@Override
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

	@Override
	public Dimension getMaximumSize() {
		return getPreferredSize();
	}

	@Override
	public int getWidth() {
		return getPreferredSize().width;
	}

	@Override
	public int getHeight() {
		return getPreferredSize().height;
	}

	private void deriveShape() {
		if (pitch.isAccidental) {
			this.shape = Shape.B;
		} else if (pitch.next().isAccidental && pitch.previous().isAccidental)
			this.shape = Shape.T;
		else if (pitch.previous().isAccidental)
			this.shape = Shape.R;
		else if (pitch.next().isAccidental)
			this.shape = Shape.L;
		if (special)
			this.shape = Shape.F;
	}

	private void deriveDrawArea(int x, int y) {
		drawArea = new Polygon();
		drawArea.addPoint(x, y);
		switch (shape) {
		case L:
			drawArea.addPoint(x + WHITE_KEY_WIDTH - BLACK_KEY_WIDTH / 2, y);

			drawArea.addPoint(x + WHITE_KEY_WIDTH - BLACK_KEY_WIDTH / 2, y
					+ BLACK_KEY_HEIGHT);
			drawArea.addPoint(x + WHITE_KEY_WIDTH, y + BLACK_KEY_HEIGHT);
			drawArea.addPoint(x + WHITE_KEY_WIDTH, y + WHITE_KEY_HEIGHT);
			drawArea.addPoint(x, y + WHITE_KEY_HEIGHT);
			break;
		case B:
			drawArea.addPoint(x + BLACK_KEY_WIDTH, y);
			drawArea.addPoint(x + BLACK_KEY_WIDTH, y + BLACK_KEY_HEIGHT);
			drawArea.addPoint(x, y + BLACK_KEY_HEIGHT);
			break;
		case F:
			drawArea.addPoint(x + WHITE_KEY_WIDTH, y);
			drawArea.addPoint(x + WHITE_KEY_WIDTH, y + WHITE_KEY_HEIGHT);
			drawArea.addPoint(x, y + WHITE_KEY_HEIGHT);
			break;
		case R:
			drawArea.addPoint(x + WHITE_KEY_WIDTH - BLACK_KEY_WIDTH / 2, y);
			drawArea.addPoint(x + WHITE_KEY_WIDTH - BLACK_KEY_WIDTH / 2, y
					+ WHITE_KEY_HEIGHT);
			drawArea.addPoint(x - BLACK_KEY_WIDTH / 2, y + WHITE_KEY_HEIGHT);
			drawArea.addPoint(x - BLACK_KEY_WIDTH / 2, y + BLACK_KEY_HEIGHT);
			drawArea.addPoint(x, y + BLACK_KEY_HEIGHT);
			break;
		case T:
			drawArea.addPoint(x - BLACK_KEY_WIDTH + WHITE_KEY_WIDTH, y);
			drawArea.addPoint(x - BLACK_KEY_WIDTH + WHITE_KEY_WIDTH, y
					+ BLACK_KEY_HEIGHT);
			drawArea.addPoint(x - BLACK_KEY_WIDTH / 2 + WHITE_KEY_WIDTH, y
					+ BLACK_KEY_HEIGHT);
			drawArea.addPoint(x - BLACK_KEY_WIDTH / 2 + WHITE_KEY_WIDTH, y
					+ WHITE_KEY_HEIGHT);
			drawArea.addPoint(x - BLACK_KEY_WIDTH / 2, y + WHITE_KEY_HEIGHT);
			drawArea.addPoint(x - BLACK_KEY_WIDTH / 2, y + BLACK_KEY_HEIGHT);
			drawArea.addPoint(x, y + BLACK_KEY_HEIGHT);
			break;
		default:
			break;
		}
	}

	public Shape getShape() {
		return shape;
	}

	public Pitch getPitch() {
		return pitch;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		System.out.println(pitch);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}
}