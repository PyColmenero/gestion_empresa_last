package es.studium.sgdb;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Label;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class Ayuda extends Connections implements WindowListener, ActionListener {

	Image icon = Toolkit.getDefaultToolkit().getImage("./logo.png");
	Frame ayuda = new Frame("Ayuda - Agencia Seguros S.A.");
	Label lbl_title = new Label("Ayuda", Label.CENTER);
	
	public Ayuda(int privilegio) {
		
		ayuda.setIconImage(icon);
		ayuda.setLayout(new FlowLayout());
		ayuda.setBackground( Color.DARK_GRAY );
		
		// TITLE
		lbl_title.setFont( new Font( Font.SANS_SERIF , Font.PLAIN, 35) );
		lbl_title.setForeground( Color.white );
		lbl_title.setPreferredSize( new Dimension(390,70) );
		ayuda.add(lbl_title);
		
		
		
		ayuda.setSize(450, 300);
		ayuda.setResizable(false);
		ayuda.setLocationRelativeTo(null);
		ayuda.addWindowListener(this);
		ayuda.setVisible(true);
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {}

	@Override
	public void windowOpened(WindowEvent e) {}

	@Override
	public void windowClosing(WindowEvent e) {ayuda.setVisible(false);}

	@Override
	public void windowClosed(WindowEvent e) {}

	@Override
	public void windowIconified(WindowEvent e) {}

	@Override
	public void windowDeiconified(WindowEvent e) {}

	@Override
	public void windowActivated(WindowEvent e) {}

	@Override
	public void windowDeactivated(WindowEvent e) {}

}