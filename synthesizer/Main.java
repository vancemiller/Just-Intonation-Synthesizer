package synthesizer;

import java.io.FileNotFoundException;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Main {

	public static void main(String[] args) {
		try {
			// Set System L&F
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException e) {
			// handle exception
		} catch (ClassNotFoundException e) {
			// handle exception
		} catch (InstantiationException e) {
			// handle exception
		} catch (IllegalAccessException e) {
			// handle exception
		} finally {
			KeyboardImpl k;
			try {
				k = new KeyboardImpl(FileReader.getInstruments());
				KeyboardView v = new KeyboardView();
				new Controller(v, k);
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(null,
					    "The file " + FileReader.getFileName() + " is missing.\nPlease place the file in the same directory as this program and relaunch.",
					    "Instrument file missing",
					    JOptionPane.ERROR_MESSAGE);
			}
			
		}
	}
}