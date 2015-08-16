package gfx;

import generic.Discrete;
import gfx.bars.DateBar;
import gfx.bars.SelectionBar;
import gfx.bars.TopBar;
import gfx.maps.MiniMap;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
//import java.awt.DisplayMode;
//import java.awt.Frame;
//import java.awt.GraphicsConfiguration;
//import java.awt.GraphicsDevice;
//import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.KeyStroke;

import nations.Nation;
import time.TimeManager;
import maps.GameMap;
import maps.InvalidMapSizeException;

public class GameWindow extends JFrame {

	private static final long serialVersionUID = 4598608032389126082L;

	private JLayeredPane layers;

	private GameMap map;

	private MiniMap miniMap;
	private TopBar topBar;
	private DateBar dateBar;
	private SelectionBar selectionBar;

	public GameWindow() throws InvalidMapSizeException {
		super();

		setTitle("Map Simulator");

		this.setUndecorated(true);

//		GraphicsEnvironment ge = GraphicsEnvironment
//				.getLocalGraphicsEnvironment();
//		GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
//		GraphicsConfiguratmion[] configurations = defaultScreen
//				.getConfigurations();
//		
//		defaultScreen.setFullScreenWindow(this);
//
		Dimension dim = getToolkit().getScreenSize();
		
//		defaultScreen.setDisplayMode(defaultScreen.getDisplayMode());
		
		this.setPreferredSize(dim);
		setSize(dim.width, dim.height);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		ResourceManager.initialize();

		Container pane = getContentPane();

		layers = new JLayeredPane();

		layers.setLayout(null);

		map = new GameMap(new File("maps/default/map.png"), new File("maps/default/climate.png"), dim.width,
				dim.height);
		map.setWindow(this);
		map.setBounds(0, 0, dim.width, dim.height);
		
		layers.add(map, JLayeredPane.DEFAULT_LAYER);

		pane.add(layers, BorderLayout.CENTER);

		map.loadStrucures();
		
		try {
			miniMap = new MiniMap(map);
			map.setMiniMap(miniMap);
			layers.add(miniMap, JLayeredPane.PALETTE_LAYER);

			topBar = new TopBar();
			layers.add(topBar, JLayeredPane.PALETTE_LAYER);

			TimeManager tm = new TimeManager(map);

			dateBar = new DateBar(tm);
			layers.add(dateBar, JLayeredPane.PALETTE_LAYER);

			selectionBar = new SelectionBar(map);
			selectionBar.setVisible(false);
			layers.add(selectionBar, JLayeredPane.PALETTE_LAYER);

		} catch (IOException e) {
			e.printStackTrace();
		}
		

		this.pack();

		setVisible(true);

		setLocationRelativeTo(null);

		layers.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent comp) {
				map.setSize(layers.getSize());
				map.setComponentSize(map.getWidth(), map.getHeight());
				miniMap.setBounds(map.getWidth() - 266 - 32,
						map.getHeight() - 210 - 32, 266, 210);
				topBar.setBounds(32, 8, 600, 100);
				dateBar.setBounds(map.getWidth() - 390 - 32, 32, 390, 100);
				selectionBar.setBounds(32, map.getHeight() - 32 - 644, 244, 644);
			}
		});

		map.setSize(layers.getSize());
		map.setComponentSize(map.getWidth(), map.getHeight());
		miniMap.setBounds(map.getWidth() - 266 - 32,
				map.getHeight() - 210 - 32, 266, 210);
		topBar.setBounds(32, 8, 600, 100);
		dateBar.setBounds(map.getWidth() - 390 - 32, 8, 390, 100);
		selectionBar.setBounds(32, map.getHeight() - 32 - 644, 244, 644);

		Action hud = new AbstractAction() {
			private static final long serialVersionUID = -330441128164918737L;

			public void actionPerformed(ActionEvent e) {
				toggleHUD();
			}
		};

		Action fow = new AbstractAction() {
			private static final long serialVersionUID = -330441128164918737L;

			public void actionPerformed(ActionEvent e) {
				map.toggleFOW();
			}
		};

		Action pause = new AbstractAction() {
			private static final long serialVersionUID = -330441128164918737L;

			public void actionPerformed(ActionEvent e) {
				dateBar.togglePause();
			}
		};
		
		Action fancy = new AbstractAction() {
			private static final long serialVersionUID = 7866463857855999536L;

			public void actionPerformed(ActionEvent e) {
				map.toggleFancy();
			}
			
		};

		Action unfull = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				if (GameWindow.this.isUndecorated()) {
					GameWindow.this.dispose();
					GameWindow.this.setUndecorated(false);
					
					Dimension dim = getToolkit().getScreenSize();
					GameWindow.this.setBounds(0, 0, dim.width, dim.height);
					GameWindow.this.pack();
					GameWindow.this.setVisible(true);
				}
			}
		};

		Action full = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				if (!GameWindow.this.isUndecorated()) {
					GameWindow.this.dispose();
					GameWindow.this.setUndecorated(true);
					Dimension dim = getToolkit().getScreenSize();
					System.out.println(dim.width + " " + dim.height);
					GameWindow.this.setBounds(0, 0, dim.width, dim.height);
					GameWindow.this.pack();
					GameWindow.this.setVisible(true);
				}
			}
		};
		
		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_O, 0), "fancy");
		getRootPane().getActionMap().put("fancy", fancy);

		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_H, 0), "hud");
		getRootPane().getActionMap().put("hud", hud);

		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_F, 0), "fow");
		getRootPane().getActionMap().put("fow", fow);

		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_P, 0), "pause");
		getRootPane().getActionMap().put("pause", pause);

		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "unfull");
		getRootPane().getActionMap().put("unfull", unfull);

		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_M, 0), "full");
		getRootPane().getActionMap().put("full", full);

		
	}

	private boolean visible = true;

	public void toggleHUD() {
		if (visible) {
			miniMap.setVisible(false);
			topBar.setVisible(false);
			dateBar.setVisible(false);
			selectionBar.setVisible(false);
			map.toggleUI();
			visible = false;
		} else {
			miniMap.setVisible(true);
			topBar.setVisible(true);
			dateBar.setVisible(true);
			selectionBar.setSelected(map.getSelected());
			map.toggleUI();
			visible = true;
		}
	}

	public void setSelected(Discrete selected) {
		selectionBar.setSelected(selected);
	}

	public void updatePlayerStats(Nation player) {
		if (topBar != null) {
			topBar.updatePlayerStats(player);
		}
	}

}
