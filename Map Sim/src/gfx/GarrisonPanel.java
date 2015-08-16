package gfx;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;

import generic.Garrisonable;
import gfx.buttons.AbstractGameButton;
import gfx.buttons.GenericGameButton;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class GarrisonPanel extends JPanel {
	private static final long serialVersionUID = 5483352900323530194L;
	
	private JLabel title, current, max, max_tot;
	
	private AbstractGameButton up, down;
	
	private Garrisonable thing;
	
	//private Font TITLE_FONT = new Font("Century", Font.PLAIN, 20);
	private Font ID = new Font("Century", Font.BOLD, 14);
	private Font VAL = new Font("Century", Font.PLAIN, 12);
	
	
	public GarrisonPanel() throws IOException {
		setOpaque(false);
		
		title = new JLabel("Garrison");
		title.setFont(ID);
		
		current = new JLabel("");
		current.setFont(VAL);
		current.setBackground(new Color(0, 0, 0, 100));
		max = new JLabel("");
		max.setFont(ID);
		max.setForeground(Color.GREEN);
		
		max_tot = new JLabel("");
		max_tot.setFont(ID);
		max_tot.setForeground(Color.GRAY);
		
		up = GenericGameButton.createUpButton(new Action() {
			public void perform() {
				thing.addGarrison();
				update(thing);
			}
		});
		
		down = GenericGameButton.createDownButton(new Action() {
			public void perform() {
				thing.subtractGarrison();
				update(thing);
			}
		});
		
		setLayout(null);
		
		add(title);
		add(current);
		add(max);
		add(max_tot);
		add(up);
		add(down);
		
		title.setBounds(0, 0, 100, 20);
		current.setBounds(0, 22, 100, 20);
		max.setBounds(150, 6, 100, 20);
		max_tot.setBounds(150,28, 100, 20);
		up.setBounds(110, 6, 30, 20);
		down.setBounds(110, 28, 30, 20);
	
	}
	
	public boolean update(Garrisonable thing) {
		this.thing = thing;
		current.setText(thing.getGarrison() + "");
		max.setText((thing.getMaxGarrison() + thing.getRequiredGarrison() - thing.getGarrison()) + "");
		max_tot.setText((thing.getMaxGarrison() + thing.getRequiredGarrison()) + "");
		
		if (thing.canAddGarrison()) {
			up.enable();
		} else {
			up.disable();
		}
		
		if(thing.getGarrison() > 0) {
			down.enable();
		} else {
			down.disable();
		}
		
		setVisible(true);
		
		return thing.canAddGarrison();
	}

	public void hideFromView() {
		setVisible(false);
		
	}

}
