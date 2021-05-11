package es.studium.sgdb;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class FK implements WindowListener{
	
	// DIALOG FOREIGN KEY
	private Dialog dialogo = null;
	private Panel pnl_data = null;
	private Color WHITE_BG = new Color(245, 245, 245);
	
	public FK(Frame ventana) {
		
		dialogo = new Dialog(ventana, "Foreign Key - Agencia Seguros S.A.", true);
		pnl_data = new Panel();
		
		// =================== DIALOG ===============================
		dialogo.add(pnl_data);
		dialogo.setBackground( WHITE_BG );
		
		dialogo.setSize(400, 200);
		dialogo.setResizable(false);
		dialogo.setLocationRelativeTo(null);
		dialogo.addWindowListener(this);
		dialogo.setVisible(false);
	}
	
	public Panel getDataPanel() {
		return this.pnl_data;
	}
	public Dialog getDialogo() {
		return this.dialogo;
	}

	@Override
	public void windowOpened(WindowEvent e) {}
	@Override
	public void windowClosing(WindowEvent e) {
		dialogo.setVisible(false);
	}
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