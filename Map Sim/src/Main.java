import maps.InvalidMapSizeException;
import gfx.GameWindow;


public final class Main {
	
	private Main() {
		throw new RuntimeException("Why are you constructing Main?");
	}
	
	public static void main(String[] args) throws InvalidMapSizeException {
		new GameWindow();
	}
}
