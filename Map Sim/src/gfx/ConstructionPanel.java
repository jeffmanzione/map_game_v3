package gfx;


import generic.Constructable;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JPanel;

import maps.GameMap;

public class ConstructionPanel extends JPanel {

	private static final long serialVersionUID = 7817988304580577283L;


	private JButton construct;

	private Constructable thing;


	public ConstructionPanel(final GameMap map) throws IOException {
		setOpaque(false);
		setLayout(null);

		construct = new JButton("Construct");
		this.add(construct);
		construct.setBounds(0, 0, 100, 20);

		construct.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (construct.getText().equals("Construct")) {
					thing.setConstructing(true);
					construct.setText("Cancel");
				} else {
					thing.setConstructing(false);
					construct.setText("Construct");
				}
			}
		});

	}

	public boolean update(Constructable thing) {
		this.thing = thing;

		setVisible(true);

		return true;
	}

	public void hideFromView() {
		setVisible(false);
		if (thing != null) {
			thing.setConstructing(false);
		}
		construct.setText("Construct");
	}

}
