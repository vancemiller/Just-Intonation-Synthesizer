package synthesizer;

public class Main {

	public static void main(String[] args) {
		KeyboardImpl k = new KeyboardImpl();
		KeyboardView v = new KeyboardView();
		new Controller(v, k);
	}
}