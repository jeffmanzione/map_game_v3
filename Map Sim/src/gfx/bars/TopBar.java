package gfx.bars;


import gfx.maps.MapComponent;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import nations.Nation;

public class TopBar extends MapComponent {
	
	private static final long serialVersionUID = 808161337710676006L;
	
	private JLabel money, man, admin, diplo, milit;
	private JLabel money_icon, man_icon, admin_icon, diplo_icon, milit_icon;
	
	public TopBar() throws IOException {
		super(ImageIO.read(new File("gfx/gui/top_bar_skin.png")), 600, 100);
		
		money_icon = new JLabel(new ImageIcon("gfx/gui/money.png"));
		add(money_icon);
		money_icon.setBounds(20, 8, 16, 16);
		man_icon = new JLabel(new ImageIcon("gfx/gui/manpower.png"));
		add(man_icon);
		man_icon.setBounds(20 + 100, 8, 16, 16);
		admin_icon = new JLabel(new ImageIcon("gfx/gui/adminpower.png"));
		add(admin_icon);
		admin_icon.setBounds(32 + 20 + 100 + 100, 8, 16, 16);
		diplo_icon = new JLabel(new ImageIcon("gfx/gui/diplopower.png"));
		add(diplo_icon);
		diplo_icon.setBounds(32 + 20 + 100 + 100 + 60, 8, 16, 16);
		milit_icon = new JLabel(new ImageIcon("gfx/gui/militpower.png"));
		add(milit_icon);
		milit_icon.setBounds(32 + 20 + 100 + 100 + 60 + 60, 8, 16, 16);
		
		money = new JLabel();
		add(money);
		money.setBounds(48, 6, 50, 20);
		
		man = new JLabel();
		add(man);
		man.setBounds(48 + 100, 6, 50, 20);
		
		admin = new JLabel();
		add(admin);
		admin.setBounds(32 + 40 + 100 + 100, 6, 50, 20);
		diplo = new JLabel();
		add(diplo);
		diplo.setBounds(32 + 40 + 100 + 100 + 60, 6, 50, 20);
		milit = new JLabel();
		add(milit);
		milit.setBounds(32 + 40 + 100 + 100 + 60 + 60, 6, 50, 20);
		
	}

	public void updatePlayerStats(Nation player) {
		money.setText(player.getMoney() + "");
		money.setToolTipText("Exact: " + player.getExactMoney() + "; Growth: " + player.getExactMoneyGrowthPerMonth());
		
		man.setText(player.getManpower() + "");
		man.setToolTipText("Exact: " + player.getExactManpower() + "; Growth: " + player.getExactManpowerGrowthPerMonth());
		
		admin.setText(player.getAdminPower() + "");
		
		diplo.setText(player.getDiplomaticPower() + "");
		
		milit.setText(player.getMilitaryPower() + "");
		
	}

}
