package es.studium.practica_t2;

import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginFrame extends Connections implements WindowListener, ActionListener{

	Image icon = Toolkit.getDefaultToolkit().getImage("./logo.png");    

	
	Frame ventana = new Frame("LOG IN - Agencia Seguros S.A.");
	
	
	Panel pnl_title = new Panel();
	Label lbl_title = new Label("LOG IN", Label.CENTER);
	TextField tf_username = new TextField("", 40);
	TextField tf_password = new TextField("", 40);
	Label lbl_errormsg = new Label("", Label.CENTER);
	Button btn_accept = new Button("Aceptar");
	
	String sentencia = "";

	public LoginFrame() {
		
		ventana.setIconImage(icon);
		ventana.setLayout(new FlowLayout());
		ventana.setBackground( Color.DARK_GRAY );
		
		// TITLE
		lbl_title.setFont( new Font( Font.SANS_SERIF , Font.PLAIN, 35) );
		lbl_title.setForeground( Color.white );
		lbl_title.setPreferredSize( new Dimension(390,70) );
		pnl_title.add(lbl_title);
		ventana.add(pnl_title);
		
		// INPUTS
		tf_password.setEchoChar('*');
		ventana.add(tf_username);
		ventana.add(tf_password);
		lbl_errormsg.setForeground( Color.red );
		lbl_errormsg.setPreferredSize( new Dimension(390,20) );
		ventana.add(lbl_errormsg);
		
		// SPACE
		Panel pnl_space = new Panel();
		pnl_space.setPreferredSize( new Dimension(390,20) );
		ventana.add(pnl_space);
		
		// BUTTON
		btn_accept.setPreferredSize( new Dimension(304, 40) );
		btn_accept.addActionListener(this);
		ventana.add(btn_accept);
		
		
		ventana.setSize(450, 300);
		ventana.setResizable(false);
		ventana.setLocationRelativeTo(null);
		ventana.addWindowListener(this);
		ventana.setVisible(true);
		
	}
	
	public static void main(String[] args) {
		
		new LoginFrame();
		
	}
	
	
	String username = "";
	String password = "";
	int privilegio = 0;
	
	private boolean login() {
		
				
		try {
			// conectamos a la base de datos
			connection = connect();
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            // formulamos sentencia
            sentencia = "SELECT * FROM usuarios WHERE nombreUsuario = '"+username+"' AND contraseniaUsuario = SHA2('"+password+"',256);";
            rs = statement.executeQuery(sentencia);
            
            // SI NO ES NULL SIGNIFICA QUE EXISTE LA COMBINACION ENTRE nombre Y contraseña
            if(rs.next()) {
            	privilegio = rs.getInt("tipoUsuario");
            	return true;
            }
            
		}
        catch (SQLException sqle) {
               System.out.println("Error 2-"+sqle.getMessage());
		} finally {
			try {
				rs.close();
				statement.close();
				connection.close();
			} catch(SQLException sqle) {
				System.out.println("Error al cerrar conexiones: "+sqle.getMessage());
			}
		}
		
		return false;
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource().equals( btn_accept )) {
			
			username = tf_username.getText();
			password = tf_password.getText();
			
			username = username.replaceAll("'", "").replaceAll("\"", "");
			tf_username.setText(username);
			
			password = password.replaceAll("'", "").replaceAll("\"", "");
			tf_password.setText(password);
			// si no estan vacias
			
			lbl_errormsg.setText( "Loading..." );
			
			if( username.length() != 0 && password.length() != 0 ) {
				if(login()) {
					ventana.setVisible(false);
					new MainFrame(privilegio);
				} else {
					lbl_errormsg.setText( "Username or Password are not correct" );
				}
			} else {
				lbl_errormsg.setText( "Username or Password might not be empty" );
			}
			
			
		}		
	}


	@Override
	public void windowClosing(WindowEvent e) {
		System.exit(0);
	}

	@Override
	public void windowOpened(WindowEvent e) {}
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
