package gfx.bars;

import generic.Constructable;
import generic.Discrete;
import generic.Garrisonable;
import gfx.ConstructionPanel;
import gfx.GarrisonPanel;
import gfx.maps.MapComponent;
import item.structures.cities.Settlement;

import java.awt.Font;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import maps.GameMap;
import tiles.Tile;

public class SelectionBar extends MapComponent {
	private static final long serialVersionUID = -1037777208358915427L;

	private GameMap map;
	
	public SelectionBar(GameMap map) throws IOException {
		super(ImageIO.read(new File("gfx/gui/selection_bar_skin.png")), 244, 644);
		this.map = map;
		add(gp);
		gp.setBounds(22, 160, 222, 100);
		add(cp);
		cp.setBounds(22, 260, 222, 100);
		
	}

	private GarrisonPanel gp = new GarrisonPanel();
	private ConstructionPanel cp = new ConstructionPanel(map);
	
	private boolean draw_population = false, draw_man_growth = false, draw_type = true;
	private String population, type, man_growth;
	
	private Font TITLE_FONT = new Font("Century", Font.PLAIN, 20);
	private Font ID = new Font("Century", Font.BOLD, 14);
	private Font VAL = new Font("Century", Font.PLAIN, 12);
	
	private String title = "Title";
	

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		g.setFont(TITLE_FONT);
		
		g.drawString(title, 22, 38);
		
		if (draw_type) {
			g.setFont(TITLE_FONT);
			g.drawString(type, 22, 75);
			
		}
		
		if (draw_population) {
			g.setFont(ID);
			g.drawString("Population", 22, 100);
			g.setFont(VAL);
			g.drawString(population, 122, 100);
		}

		if (draw_man_growth) {
			g.setFont(ID);
			g.drawString("Manpower", 22, 120);
			g.setFont(VAL);
			g.drawString(man_growth, 122, 120);
		}
		
	}


	public void setSelected(Discrete selected) {
		if (selected instanceof Tile) {
			this.setVisible(false);
		} else if (selected != null) {
			this.setVisible(true);
			title = selected.toString();
			type = selected.getType();
			
			if (selected instanceof Garrisonable) {
				gp.update((Garrisonable) selected); 
			} else {
				gp.hideFromView();
			}
			
			if (selected instanceof Constructable) {
				//System.out.println("OK");
				cp.update((Constructable) selected); 
			} else {
				cp.hideFromView();
			}
			
			if (selected instanceof Settlement) {
				Settlement city = (Settlement) selected;
				population = new Integer(city.getPopulation()).toString();
				man_growth = "" + city.getExactManpowerGrowth();
				
				draw_population = true;
				draw_man_growth = true;
			} else {
				draw_population = false;
				draw_man_growth = false;
			}
		} else {
			setVisible(false);
		}
	}
	
}
