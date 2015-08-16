package maps;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class AutoGenerator {
	public static final int PANGEA = 0, CONTINENTS = 1, ARCHEPELAGO = 2;
	
	public static BufferedImage generateMap(int mapType, int width, int height) {
		switch (mapType) {
			case PANGEA:
				return generatePangea(width, height);
			case CONTINENTS:
				return generateContinents(width, height);
			case ARCHEPELAGO:
				return generateArchepelago(width, height);
			default:
				return null;
		}
	}
	
	private static BufferedImage generatePangea(int width, int height) {
		int coorsToGenerate = width * height / 10;
		//int expanse = 5;
		
		Set<Point> points = new HashSet<Point>(); 
		
		Random gen = new Random();
		
		for (int i = 0; i < coorsToGenerate; i++) {
			addPoint(gen, points, width, height);
		}
		return null;
		
	}
	
	private static void addPoint(Random gen, Set<Point> points, int width, int height) {
		Point p = new Point(gen.nextInt(width), gen.nextInt(height));
		if (points.contains(p)) {
			addPoint(gen, points, width, height);
		} else {
			points.add(p);
		}
		
	}
	
	private static BufferedImage generateContinents(int width, int height) {
		return null;
		
	}
	
	private static BufferedImage generateArchepelago(int width, int height) {
		return null;
		
	}
}
