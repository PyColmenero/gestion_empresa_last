package es.studium.sgdb;

import java.awt.Button;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
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
import java.util.regex.Pattern;

public class Baja extends Connections implements WindowListener, ActionListener{
	
	Image icon = Toolkit.getDefaultToolkit().getImage("./logo.png");  
	Frame ventana = new Frame("Baja - Agencia Seguros S.A.");
	Label lbl_title = new Label("Date de alta", Label.CENTER);
	Panel pnl_navbar_searchid = new Panel();
	Label lbl_searchid = new Label("ID: ");
	TextField tf_searchid = new TextField("",20);
	Button btn_searchid = new Button("Buscar");
	Label lbl_loading = new Label("", Label.CENTER);
	Panel pnl_form = new Panel();
	Button btn_borrar = new Button("Borrar");
	
    // DIALOG
    Dialog dlg_operation_output = new Dialog(ventana, "Operación Inserción", true);
	Label lbl_operation_output_title = new Label();
	
	// DIALO ARE YOU SURE
	Dialog dlg_areyousure = new Dialog(ventana,"¿Estás seguro?",true);
	Label lbl_areyousure = new Label("¿Estás seguro de que quieres continuar?");
	Button btn_notsure = new Button("Cancelar");
	Button btn_suresure = new Button("Borrar");
	
	
	// nombres
	String table_name = "";
	String table_column_name = "";
	int columns_len = 0;
	String id_delete = "";
	
	// DDBB
	String sentencia = "";


	
	
    public Baja(String table_column_name, String table_name, String[] sql_column_names, String search_id) {
    	
    	this.table_column_name = table_column_name;
		this.table_name = table_name;
		columns_len = sql_column_names.length;
		
		// CALCULAR CUANTAS COLUMAS de 3 en 3.
		// si hay 3 columnnas, y_grid será 1
		// si hay 4 columnnas, y_grid será 2
		// si hay 6 columnnas, y_grid será 2
		// si hay 7 columnnas, y_grid será 3
		int add = (columns_len%3 != 0) ? 1 : 0;
		int y_grid = columns_len / 3 + add;
		
		// AÑADIR COMPONENTES A VENTANA
		ventana.setIconImage(icon);
		ventana.setLayout(new FlowLayout());
		ventana.setBackground( new Color(245, 245, 245) );
		
		// TITULO
		lbl_title.setPreferredSize( new Dimension(1000, 80) );
		lbl_title.setFont( new Font("Arial", Font.BOLD ,30));
		lbl_title.setText("Da de baja en " + table_column_name + "s");
		ventana.add(lbl_title);
		
		// NAVABR DE BUSCAR ID
		btn_searchid.addActionListener(this);
		btn_searchid.setPreferredSize( new Dimension(60, 22) );
		pnl_navbar_searchid.add( lbl_searchid );
		pnl_navbar_searchid.add( tf_searchid );
		pnl_navbar_searchid.add( btn_searchid );
		pnl_navbar_searchid.setPreferredSize( new Dimension(1000,30) );
		ventana.add(pnl_navbar_searchid);
		
		// LOADING/OUTPUT 
		lbl_loading.setPreferredSize( new Dimension(1000,30) );
		lbl_loading.setForeground(Color.red);
		ventana.add(lbl_loading);
		
		// PANEL CON EL FORMULARIO
		pnl_form.setPreferredSize( new Dimension(1000, 120) );
		pnl_form.setLayout(new GridLayout(y_grid,3));
		ventana.add(pnl_form); 
		
		for(int x = 0; x < columns_len; x++) {
			
			String cname = sql_column_names[x].replace(table_column_name,"");
			cname = cname.toUpperCase();
			cname += ": ";
			
			if(!cname.equals("ID: ")) {
				
				Panel pnl_comp = new Panel();
				pnl_comp.setLayout( new GridLayout(1,2));
				Label lbl_current_column = new Label( cname , Label.RIGHT);
				lbl_current_column.setFont( new Font("Arial", 1, 13) );
				Label tf_current_column = new Label("...", Label.LEFT);
				
				
				pnl_comp.add(lbl_current_column);
				pnl_comp.add(tf_current_column);
				
				pnl_form.add(pnl_comp);
				
			}
			
		}
		

		// BOTON DE BORRAR
		btn_borrar.addActionListener(this);
		btn_borrar.setPreferredSize( new Dimension(300,30) );
		ventana.add(btn_borrar);
		
		// FIN
		ventana.setSize(1100, 380);
		ventana.setResizable(false);
		ventana.setLocationRelativeTo(null);
		ventana.addWindowListener(this);
		ventana.setVisible(true);
		
		// DIALOG
		dlg_operation_output.setLayout(new FlowLayout());
		dlg_operation_output.add(lbl_operation_output_title);
		
		dlg_operation_output.setSize(350,180);
		dlg_operation_output.setLocationRelativeTo(null);
		dlg_operation_output.addWindowListener(this);
		
		// ARE YOU SURE
		dlg_areyousure.setLayout(new FlowLayout());
		dlg_areyousure.add(lbl_areyousure);
		
		btn_notsure.addActionListener(this);
		btn_suresure.addActionListener(this);
		dlg_areyousure.add(btn_notsure);
		dlg_areyousure.add(btn_suresure);
		
		dlg_areyousure.setSize(270,130);
		dlg_areyousure.setLocationRelativeTo(null);
		dlg_areyousure.addWindowListener(this);
		
		// SI SE HA ACCEDIDO AQUÍ DESDE EL BOTÓN ANONIMO
		if(search_id.length() != 0) {
			id_delete = search_id;
			tf_searchid.setText(id_delete);
			fill_form_data();
		} else {
			lbl_loading.setText("Busca una ID.");
		}
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		// BUSCAR EL ID
		if(e.getSource().equals( btn_searchid )) {
			
			if(tf_searchid.getText().length() >= 1) {
				if( Pattern.matches("\\d+", tf_searchid.getText()) ) {
					fill_form_data();
				} else {
					lbl_loading.setText("Solo digitos");
				}
				
			} else {
				lbl_loading.setText("Coloca una ID");
			}
			
		} else { // ENTRAR EN EL DIALOGO DE ¿ESTASSEGURO?
			
			if(e.getSource().equals( btn_borrar )) {
				
				if(tf_searchid.getText() != id_delete) {
					tf_searchid.setText(id_delete);
				}
				
				if(id_delete.length() != 0) {
					
					if(lbl_loading.getText().length() == 0)	dlg_areyousure.setVisible(true);
					
				} else {
					lbl_loading.setText("Coloca una ID");
				}
				
			} else { 
				
				// SI ESTOY SEGURO QUE LO QUIERO BORRAR
				if(e.getSource().equals( btn_suresure )) {
					
					lbl_areyousure.setText("Loading...");
					dlg_areyousure.setVisible(false);
					delete_row();
					
				} else { // NO ESTOY SEGURO
					
					if( e.getSource().equals( btn_notsure ) ) {
						dlg_areyousure.setVisible(false);
					}
					
				}
				
			}
			
			
		}
				
	}

	private void delete_row() {

		connection = connect();
		sentencia = "DELETE FROM " + table_name + " WHERE id"+ table_column_name +" = " + tf_searchid.getText() + ";";
						
		// DO QUERY
		connection = connect();
		
		String output = "";
		
		
		try {
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            statement.executeUpdate(sentencia);
            new Log(username, "BAJA", sentencia);
            
            output = table_column_name + " dado de baja correctamente.";
		}
        catch (SQLException sqle) {
        	output = "Error 2-"+sqle.getMessage();
		} finally {
			
			lbl_operation_output_title.setText(output);
			dlg_operation_output.setVisible(true);
			
			lbl_loading.setText("");
			
		}
		
	}

	private void fill_form_data() {

		boolean id_not_found = true;
		
		lbl_loading.setText("Cargando...");
		
		id_delete = tf_searchid.getText();
		
		try {
			
			connection = connect();
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            sentencia = "SELECT * FROM " + table_name + " WHERE id"+ table_column_name +" = " + id_delete + ";";
            rs = statement.executeQuery(sentencia);
            
            // LE DA VALOR A LOS LABELS SEGUN LO DEVUELTO
            if(rs.next()) {
            	id_not_found = false;
            	
            	for(int x = 1; x < columns_len; x++) {            		
            		Panel pnl_root = (Panel) pnl_form.getComponent(x-1);
            		Label lbl_editable = (Label) pnl_root.getComponent(1);
            		lbl_editable.setText( rs.getString(x+1) );
            		
            	}
            	
            }
		}
        catch (SQLException sqle) {
                System.out.println("BAJA Error 2-"+sqle.getMessage());
		} finally {
			
			if(id_not_found) {
				// DELETE FORM DATA
				for(int x = 1; x < columns_len; x++) {            		
            		Panel pnl_root = (Panel) pnl_form.getComponent(x-1);
            		Label lbl_editable = (Label) pnl_root.getComponent(1);
            		lbl_editable.setText( "" );
            	}
				lbl_loading.setText("ID not found.");
			} else {
				lbl_loading.setText("");
			}
			
		}
		
	}

	@Override
	public void windowOpened(WindowEvent e) {}
	@Override
	public void windowClosing(WindowEvent e) {

		if(dlg_operation_output.isActive()) {
			dlg_operation_output.setVisible(false);
		} else if(dlg_areyousure.isActive()) {
			dlg_areyousure.setVisible(false);
		} else {
			ventana.setVisible(false);
		}

		
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