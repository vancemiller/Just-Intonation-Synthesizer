package testing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class View {
	Model m;
	JFrame f;

	public View() {
		this.m = new Model();
		this.f = new JFrame("Just Intonation");
		init();
		f.setMinimumSize(new Dimension(600, 270));
		f.setMaximumSize(new Dimension(600, 270));
		// f.pack();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}

	private void init() {
		JScrollPane sp = new JScrollPane(new KeyboardPanel(),
				JScrollPane.VERTICAL_SCROLLBAR_NEVER,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		f.add(sp);
	}
}

class KeyboardPanel extends JPanel implements MouseListener {

	private static final long serialVersionUID = -7261532115653344378L;

	private static class Keys {
		public static final int BLACK_KEY_WIDTH = 30, BLACK_KEY_HEIGHT = 140,
				WHITE_KEY_WIDTH = 50, WHITE_KEY_HEIGHT = 200,
				NUM_WHITE_KEYS = 52, NUM_OCTAVES = 7;

		private static final int keyPositionStart[] = { 0, 1, 1 };
		private static final boolean isBlackStart[] = { false, true, false };

		private static final int keyPositionOctave[] = { 0, 1, 1, 2, 2, 3, 4,
				4, 5, 5, 6, 6 };
		private static final boolean isBlackOctave[] = { false, true, false,
				true, false, false, true, false, true, false, true, false };

		private static final int keyPositionEnd[] = { 0 };
		private static final boolean isBlackEnd[] = { false };


		
		public static int getPosition(int keyNumber) {
			int ret;
			if (keyNumber < 0 || keyNumber > getNumKeys()) {
				throw new ArrayIndexOutOfBoundsException("Invalid key number");
			} else if (keyNumber < keyPositionStart.length) {
				ret =  keyPositionStart[keyNumber];
			} else if (keyNumber - keyPositionStart.length < keyPositionOctave.length
					* NUM_OCTAVES) {
				keyNumber -= keyPositionStart.length;
				int keyPos = keyPositionStart[keyPositionStart.length - 1]
						+ 1
						+ keyPositionOctave[keyNumber
								% keyPositionOctave.length]
						+ (keyPositionOctave[keyPositionOctave.length - 1] + 1)
						* (keyNumber / keyPositionOctave.length);
				ret = keyPos;
			} else {
				keyNumber -= keyPositionStart.length
						+ (keyPositionOctave.length) * NUM_OCTAVES;
				int keyPos = keyPositionStart[keyPositionStart.length - 1] + 1
						+ NUM_OCTAVES
						* (keyPositionOctave[keyPositionOctave.length - 1] + 1);
				ret = keyPos + keyPositionEnd[keyNumber];
			}
			System.out.print(ret + ", ");
			return ret;
		}

		private static boolean isBlack(int keyNumber) {
			if (keyNumber < 0
					|| keyNumber > isBlackStart.length + isBlackOctave.length
							* NUM_OCTAVES + isBlackEnd.length)
				throw new ArrayIndexOutOfBoundsException("Invalid key number");
			if (keyNumber < isBlackStart.length)
				return isBlackStart[keyNumber];
			keyNumber -= isBlackStart.length;
			if (keyNumber < isBlackOctave.length * NUM_OCTAVES)
				return isBlackOctave[keyNumber % isBlackOctave.length];

			keyNumber -= keyPositionOctave.length * NUM_OCTAVES;
			return isBlackEnd[keyNumber];
		}

		public static void drawKey(Graphics g, int keyNumber) {
			if (isBlack(keyNumber)) {
				g.fillRect(getPosition(keyNumber) * WHITE_KEY_WIDTH
						- BLACK_KEY_WIDTH / 2, 0, BLACK_KEY_WIDTH,
						BLACK_KEY_HEIGHT);
			} else {
				g.drawRect(getPosition(keyNumber) * WHITE_KEY_WIDTH, 0,
						WHITE_KEY_WIDTH, WHITE_KEY_HEIGHT);
			}
		}

		public static int getNumKeys() {
			return keyPositionStart.length + NUM_OCTAVES
					* keyPositionOctave.length + keyPositionEnd.length;
		}

		public static int getKeyNum(MouseEvent e)
				throws ArrayIndexOutOfBoundsException {
			// PRE: mouse event is in the key area
			int rawKeyPos = e.getPoint().x / WHITE_KEY_WIDTH;
			int verticalPos = e.getPoint().y;
			if (rawKeyPos <= keyPositionStart[keyPositionStart.length - 1]) {
				for (int i = 0; i < keyPositionStart.length; i++)
					if (rawKeyPos == keyPositionStart[i])
						if (verticalPos <= BLACK_KEY_HEIGHT)
							return i;
						else
							return i + 1;
			} else if (rawKeyPos <= keyPositionStart[keyPositionStart.length - 1]
					+ 1
					+ NUM_OCTAVES
					* (keyPositionOctave[keyPositionOctave.length - 1] + 1)) {
				for (int j = 0; j < NUM_OCTAVES; j++)
					for (int i = 0; i < keyPositionOctave.length; i++)
						if (rawKeyPos == keyPositionOctave[i]
								+ keyPositionOctave[keyPositionOctave.length - 1]
								* j)
							return keyPositionStart.length + i
									+ keyPositionOctave.length * j;
			} else {
				for (int i = 0; i < keyPositionEnd.length; i++)
					if (rawKeyPos == (keyPositionOctave[keyPositionOctave.length - 1] + 1)
							* NUM_OCTAVES
							+ keyPositionStart[keyPositionStart.length - 1]
							+ 1
							+ keyPositionEnd[i])
						return keyPositionStart.length + NUM_OCTAVES
								* keyPositionOctave.length + i;
			}
			throw new ArrayIndexOutOfBoundsException(
					"Invalid key position mouse event");
		}
	}

	KeyboardPanel() {
		super();
		Dimension size = getPreferredSize();
		this.setMinimumSize(size);
		this.setMaximumSize(size);
		this.setBackground(Color.white);
		this.addMouseListener(this);
	}

	public Dimension getPreferredSize() {
		return new Dimension(Keys.NUM_WHITE_KEYS * Keys.WHITE_KEY_WIDTH,
				Keys.WHITE_KEY_HEIGHT);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawKeys(g);
	}

	private void drawKeys(Graphics g) {
		for (int keyNumber = 0; keyNumber < Keys.getNumKeys(); keyNumber++) {
			Keys.drawKey(g, keyNumber);
		}
	}

	private void pressKey(MouseEvent e) {
		System.out.println(Keys.getKeyNum(e));
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		pressKey(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO
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
