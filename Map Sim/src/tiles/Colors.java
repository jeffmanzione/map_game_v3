package tiles;

import java.awt.Color;

public interface Colors {
	public static final int 
			GRASS_COLOR    = 0xFF00FF00, 
			FOREST_COLOR   = 0xFF007D00,
			LIGHT_COLOR    = 0xFFFF0000, 
			DARK_COLOR     = 0xFFFFFF00, 
			BEACH_COLOR    = 0xFFFFFFFF, 
			PLAIN_COLOR    = 0xFF7DFF7D, 
			DESERT_COLOR   = 0xFF000000, 
			SWAMP_COLOR    = 0xFFFF00FF, 
			MOUNTAIN_COLOR = 0xFFFFC87D,
			MT_LOW_COLOR   = 0xFFFFFF00,
			MT_PEAK_COLOR  = 0xFFFF7D7D,
			MT_CAP_COLOR   = 0xFFFF007D,
			RIVER_COLOR    = 0xFF00FFFF,
			NONE_COLOR 	   = 0xFF000000, 
			SEA_COLOR 	   = 0xFF0000FF,
			DEEP_SEA_COLOR = 0xFF00007D,
			SHALLOW_COLOR  = 0xFF007DFF;
	
	public static final int
			GRASS_DISP 	   = 0xFFA7D762, 
			FOREST_DISP    = 0xFF75A450,
			GRASSL_DISP    = 0xFFe5ef8f, 
			FORESTL_DISP   = 0xFF1F6630,
			BEACH_DISP 	   = 0xFFf8F4CA, 
			PLAIN_DISP 	   = 0xFF7DFF7D, 
			DESERT_DISP    = 0xFFF1EED2, 
			SWAMP_DISP 	   = 0xFFFF00FF, 
			MOUNTAIN_DISP  = 0xFFB1A39A,
			MT_LOW_DISP    = 0xFFA4948A,
			MT_PEAK_DISP   = 0xFFCFC0B7,
			MT_CAP_DISP    = 0xFFF6EBE5,
			RIVER_DISP 	   = 0xFF5E91CF,
			NONE_DISP 	   = 0xFF000000, 
			SEA_DISP 	   = 0xFF559DF6,
			DEEP_SEA_DISP  = 0xFF4D94EC,
			SHALLOW_DISP   = 0xFF89b9f3;
	
	public static final Color 
			NORM_BG				= new Color(0, 0, 0, 80),
			NORM_FG				= new Color(255, 255, 255),
			HOVER_BG			= new Color(0, 0, 0, 180),
			HOVER_FG			= NORM_FG,
			SELECT_BG			= NORM_BG,
			SELECT_FG			= Color.GREEN,
			HOVER_SELECT_BG		= HOVER_BG,
			HOVER_SELECT_FG		= SELECT_FG,
			HOVER_SELECT_HIGH 	= new Color(0, 255, 0 , 125),
			SELECT_HIGH			= new Color(0, 255, 0, 75),
			HOVER_HIGH			= new Color(0, 255, 0, 40),

			TRANS_GREEN = new Color(0, 255, 0, 30);
			
			
}
