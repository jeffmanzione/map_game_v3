package gfx;

public class ImageWithSameNameAlreadyExistsException extends Exception {
	private static final long serialVersionUID = 5631786173003534089L;

	public ImageWithSameNameAlreadyExistsException(String name) {
		super(name);
	}
}
