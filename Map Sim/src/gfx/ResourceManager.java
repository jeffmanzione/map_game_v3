package gfx;

import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;


public class ResourceManager {
	private Map<String, Map<Integer, BufferedImage>> images;
	
	private int[] scales;
	
	private Component component;
	
	/** The singleton */
	private static ResourceManager SINGLETON;
	
	public static void initialize() {
		try {
			SINGLETON = new ResourceManager(new File("gfx"), 
							new int[]{2, 4, 8, 16, 32, 64, 128, 256});
			
		} catch (FileIsNotADirectoryException | ImageFileIsWrongFormatException
				| IOException | ImageWithSameNameAlreadyExistsException e) {
			e.printStackTrace();
		}
	}
	
	private ResourceManager(File dir, int[] scales) 
			throws FileIsNotADirectoryException, 
			ImageFileIsWrongFormatException, IOException, 
			ImageWithSameNameAlreadyExistsException {
		
		images = new HashMap<>();
		
		this.scales = scales;
		
		if (!dir.exists()) {
			throw new FileNotFoundException();
		}
		if (!dir.isDirectory()) {
			throw new FileIsNotADirectoryException();
		} else {
			checkIn(dir);
		}
	}
	
	private void checkIn(File file) throws FileIsNotADirectoryException, 
			ImageFileIsWrongFormatException, IOException, 
			ImageWithSameNameAlreadyExistsException {
		if (!file.exists()) {
			throw new FileNotFoundException();
		} else if (file.isDirectory()) {
			checkInDirectory(file);
		} else {
			checkInFile(file);
		}
	}

	private void checkInFile(File file) throws ImageFileIsWrongFormatException, 
			IOException, ImageWithSameNameAlreadyExistsException {
		
		if (!isPNG(file)) {
			throw new ImageFileIsWrongFormatException();
		} else {
			String name = file.getName().split("\\.")[0];
			if (images.containsKey(name)) {
				throw new ImageWithSameNameAlreadyExistsException(name);
			} else {
				BufferedImage img = ImageIO.read(file);
				
				Map<Integer, BufferedImage> scaled = new HashMap<>();
				
				for (int scale : scales) {
					AffineTransform at = 
							AffineTransform.getScaleInstance(
									(double) scale / img.getWidth() * 2, 
									(double) scale / img.getHeight() * 2);
				
					AffineTransformOp scaleOp = new AffineTransformOp(at, 
							   AffineTransformOp.TYPE_BILINEAR);
					
					scaled.put(scale, 
							scaleOp.filter(img, new BufferedImage(scale * 2, scale * 2, 
									BufferedImage.TYPE_INT_ARGB)));
				}
				
				images.put(name, scaled);
			}
			
		}
	}

	private void checkInDirectory(File dir) throws FileIsNotADirectoryException, 
			ImageFileIsWrongFormatException, IOException, 
			ImageWithSameNameAlreadyExistsException {
		
		if (!dir.exists()) {
			throw new FileNotFoundException();
		} else if (!dir.isDirectory()) {
			throw new FileIsNotADirectoryException();
		} else {
			
			File[] children = dir.listFiles(PNG_FILTER);
			
			for (File child : children) {
				checkInFile(child);
			}
			
			File[] childDir = dir.listFiles(new FileFilter() {
				public boolean accept(File file) {
					return file.isDirectory();
				}
			});
			
			for (File child : childDir) {
				checkInDirectory(child);
			}
			
		}
	}
	
	public static FileFilter PNG_FILTER = new FileFilter() {
		public boolean accept(File file) {
			return isPNG(file);
		}
		
	};
	
	public static boolean isPNG(File file) {
		return file.getName().toLowerCase().endsWith(".png");
	}
	
	/** Gets the BufferedImage for the requested name and scale. 
	 * Returns null if there is no entry with this name and scale.
	 * 
	 * @param name					The Image name
	 * @param scale					The Image scale
	 * @return						The Image requested
	 */
	public static BufferedImage get(String name, int scale) {
		if (SINGLETON.images.containsKey(name)) {
			return SINGLETON.images.get(name).get(scale);
		} else {
			return null;
		}
	}

	public static void importComponent(Component component) {
		SINGLETON.component = component;
	}
	
	public static FontMetrics getFontMetrics(Font font) {
		return SINGLETON.component.getFontMetrics(font);
	}
	
}
