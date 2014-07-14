package instrumentEditor;

public class Main {
	public static void main(String[] args) {
		InstrumentView iv = InstrumentView.getInstrumentView();
		new Controller(iv);
	}

}
