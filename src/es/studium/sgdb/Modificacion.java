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

public class Modificacion extends Connections implements WindowListener, ActionListener{
	
	Image icon = Toolkit.getDefaultToolkit().getImage("./logo.png");  
	
	// COMPONENTES
	Frame ventana = new Frame("Modificación - Agencia Seguros S.A.");
	Label lbl_title = new Label("", Label.CENTER);
	Panel pnl_navbar_searchid = new Panel();
	Label lbl_searchid = new Label("ID: ");
	TextField tf_searchid = new TextField("",20);
	Button btn_searchid = new Button("Buscar");
	Label lbl_loading = new Label("", Label.CENTER);
	Panel pnl_form = new Panel();
	Button btn_edit = new Button("Editar");
	
	// TABLE DATA
	String table_name = "";
	String table_column_name = "";
	String sql_column_names[];
	int arr_len = 0;
	String id_delete = "";
	// first table gaps info
	String first_row[];
	
	Color WHITE_BG = new Color(245, 245, 245);
	
	// DDBB
	String sentencia = "";
    
    // DIALOG
    Dialog dialog_output = new Dialog(ventana, "Operación Inserción", true);
	Label lbl_dialog_title = new Label();
	
	// DIALO ARE YOU SURE
	Dialog dlg_areyousure = new Dialog(ventana,"¿Estás Seguro?",true);
	Label lbl_areyousure = new Label("¿Estás seguro de que quieres modificar esta fila?");
	Button btn_notsure = new Button("Cancelar");
	Button btn_suresure = new Button("Modificar");
	
	
	//FORM CORRECTOR
	Detect_form_errors form_correcter = new Detect_form_errors();
	
    public Modificacion(String table_column_name, String table_name, String[] sql_column_names, String search_id) {
    	
    	this.table_column_name = table_column_name;
		this.table_name = table_name;
		this.sql_column_names = sql_column_names;
		this.arr_len = sql_column_names.length;
		
		// CALCULAR CUANTAS COLUMAS de 3 en 3
		int add = (arr_len%2 != 0) ? 1 : 0;
		int y_grid = arr_len / 2 + add;
		
		// ARRAY de los valores del ROW
		first_row = new String[arr_len];
		
		ventana.setIconImage(icon);
		ventana.setLayout(new FlowLayout());
		ventana.setBackground( WHITE_BG );
		
		btn_searchid.addActionListener(this);
		btn_searchid.setPreferredSize( new Dimension(60, 22) );
		lbl_title.setPreferredSize( new Dimension(700, 80) );
		lbl_title.setFont( new Font("Arial", Font.BOLD ,30));
		lbl_title.setText("Modifica una fila en " + table_column_name + "s");
		ventana.add(lbl_title);
		
		
		pnl_navbar_searchid.add( lbl_searchid );
		pnl_navbar_searchid.add( tf_searchid );
		pnl_navbar_searchid.add( btn_searchid );
		
		pnl_navbar_searchid.setPreferredSize( new Dimension(700,30) );
		ventana.add(pnl_navbar_searchid);
		
		lbl_loading.setPreferredSize( new Dimension(600,30) );
		lbl_loading.setForeground(Color.red);
		ventana.add(lbl_loading);
		
		pnl_form.setPreferredSize( new Dimension(900, 240) );
		pnl_form.setLayout(new GridLayout(y_grid,2));
		ventana.add(pnl_form); 
		
		for(int x = 0; x < arr_len; x++) {
			
			String cname = sql_column_names[x].replace(table_column_name,"");
			cname = cname.toUpperCase();
			cname += ": ";
			
			if(!cname.equals("ID: ")) {
				
				Panel pnl_comp = new Panel();
				pnl_comp.setLayout( new GridLayout(2,2));
				Label lbl_current_column = new Label( cname , Label.RIGHT);
				lbl_current_column.setFont( new Font("Arial", 1, 13) );
				TextField tf_current_column = new TextField("", Label.LEFT);
				Label lbl_column_error = new Label("");
				lbl_column_error.setForeground(Color.red);
				
				pnl_comp.add(new Panel());
				pnl_comp.add(lbl_column_error);
				pnl_comp.add(lbl_current_column);
				pnl_comp.add(tf_current_column);
				
				
				pnl_form.add(pnl_comp);
				
			}
			
		}
		

		btn_edit.addActionListener(this);
		btn_edit.setPreferredSize( new Dimension(550,30) );
		ventana.add(btn_edit);
		
		
		ventana.setSize(1000, 500);
		ventana.setResizable(false);
		ventana.setLocationRelativeTo(null);
		ventana.addWindowListener(this);
		ventana.setVisible(true);
		
		// DIALOG
		dialog_output.setLayout(new FlowLayout());
		dialog_output.add(lbl_dialog_title);
		
		dialog_output.setSize(600,100);
		dialog_output.setLocationRelativeTo(null);
		dialog_output.addWindowListener(this);
		
		// ARE YOU SURE
		dlg_areyousure.setLayout(new FlowLayout());
		dlg_areyousure.add(lbl_areyousure);
		
		btn_notsure.addActionListener(this);
		btn_suresure.addActionListener(this);
		dlg_areyousure.add(btn_notsure);
		dlg_areyousure.add(btn_suresure);
		
		dlg_areyousure.setSize(350,120);
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
		
		// BUSCA CON UNA ID, y rellena los TextFields
		if(e.getSource().equals( btn_searchid )) {
			
			if(tf_searchid.getText().length() >= 1) {
				
				fill_form_data();
				
			}
			
		} else {
			
			// BOTÓN DE EDITAR
			if(e.getSource().equals( btn_edit )) {
				
				// SI HAY UNA ID PREFIJADA
				if(id_delete.length() != 0) {
					
					
					sentencia = build_sentence_modificacion();
					
					if(sentencia != null) {
						//insert();
						System.out.println("no errors");
						
						sentencias = sentencia.split("''");	

						if(sentencia.length() != 0) {
							lbl_loading.setText("");
							dlg_areyousure.setVisible(true);
						} else {
							lbl_loading.setText("No has modificado nada.");
						}
						//insert();
						
					} else {
						System.out.println("ERRORS");
						lbl_loading.setText("");
					}
					
				} else {
					lbl_loading.setText("Set an ID");
				}
				
			} else {
				
				
				
				if(e.getSource().equals( btn_suresure )) {
					
					lbl_areyousure.setText("Loading...");
					dlg_areyousure.setVisible(false);
					
					update_row();
					
				} else {
					
					if( e.getSource().equals( btn_notsure ) ) {
						lbl_loading.setText("");
						dlg_areyousure.setVisible(false);
					}
					
				}
				
			}
			
			
		}
				
	}

	private String build_sentence_modificacion() {
		
		String int_str_error = "";
		boolean has_errors = false;
		sentencia = "";
		
		
		// RECORREMOS LAS COLUMNAS DE LA TABLA
		for(int x = 1; x < arr_len; x++) {          
			
			// SACAMOS LOS COMPONENTES
    		Panel pnl_root = 		(Panel) pnl_form.getComponent(x-1);
    		// TF del que sacar los datos
    		TextField tf_current = 	(TextField) pnl_root.getComponent(3);
    		// LBL donde poner los errores
    		Label error_label =		(Label)	pnl_root.getComponent(1);
    		
    		//
    		String sql_column_name = sql_column_names[x];
    		String show_column_name = sql_column_name.replace(table_column_name,"");
    		show_column_name = show_column_name.toUpperCase() + ":";
    		
    		// le quitamos la quotes
    		String data_in_tf = tf_current.getText();
			data_in_tf = data_in_tf.replace("'","").replace("\"","");
			tf_current.setText(data_in_tf);
			
			// si devuelve "e", hay un error en los datos puestos
			// si devuelve "s", el dato es String, por lo tanto va entre comillas
			// si devuelve "i", el dato es numérico, sin comillas
			// si devuelve "n", el dato es AUTO_INCREMENT, es NULL
			// si devuelve "id", la id no existe
			int_str_error = form_correcter.detect_errors( data_in_tf, error_label, show_column_name );
			if(!first_row[x].equals(data_in_tf)) {
				if(int_str_error.equals("s")) sentencia += "UPDATE " + table_name + " SET " + sql_column_name + " = '" + data_in_tf + "' WHERE id" + table_column_name + " = " + tf_searchid.getText() + ";''";
				if(int_str_error.equals("i")) sentencia += "UPDATE " + table_name + " SET " + sql_column_name + " = " + data_in_tf + " WHERE id" + table_column_name + " = " + tf_searchid.getText() + ";''";
				if(int_str_error.equals("n")) sentencia += "UPDATE " + table_name + " SET " + sql_column_name + " = NULL WHERE id" + table_column_name + " = " + tf_searchid.getText() + ";''";
			}
			if(int_str_error.equals("e")) has_errors = true;
			if(int_str_error.equals("id")) has_errors = true;
			
		}					
		
		return (has_errors) ? null : sentencia;
	}

	String sentencias[];
	
	private void update_row() {

		String output = "";
				
		try {
			
			connection = connect();
			statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			for(int x = 0; x < sentencias.length; x++) {
				
	            statement.executeUpdate(sentencias[x]);
	            new Log(username, "MODIFICACIÓN", sentencias[x]);
	            
	            output += "Campo acutalizado correctamente. ";
			}
            
		}
        catch (SQLException e) {
        	output += "Error 2-" + e.getMessage();
		} finally {
			
			closeConnection();
			
			lbl_dialog_title.setText(output);
			dialog_output.setVisible(true);
			
			lbl_loading.setText("");
			dlg_areyousure.setVisible(false);
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
            
            if(rs.next()) {
            	id_not_found = false;
            	
            	for(int x = 1; x < arr_len; x++) {            		
            		Panel pnl_root = 		(Panel) pnl_form.getComponent(x-1);
            		TextField tf_current = 	(TextField) pnl_root.getComponent(3);
            		Label error_label =		(Label) pnl_root.getComponent(1);
            		tf_current.setText( rs.getString(x+1) );
            		
            		// añadir al Array, los valores de esta fila, 
            		// para detectar si se ha hecho o no cambios
            		first_row[x] = (rs.getString(x+1) != null) ? rs.getString(x+1) : "";
            		
            		error_label.setText("");
            	}
            }
		}
        catch (SQLException e) {
                System.out.println("Error 2-" + e.getMessage());
		} finally {
			
			if(id_not_found) {
				// DELETE FORM DATA
				for(int x = 1; x < arr_len; x++) {            		
            		Panel pnl_root = (Panel) pnl_form.getComponent(x-1);
            		TextField lbl_editable = (TextField) pnl_root.getComponent(3);
            		lbl_editable.setText( "" );
            	}
				lbl_loading.setText("ID not found.");
			} else {
				lbl_loading.setText("");
			}
			
		}
		
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		
		if(dialog_output.isActive()) {
			dialog_output.setVisible(false);
		} else if(dlg_areyousure.isActive()){
			dlg_areyousure.setVisible(false);
		} else {
			ventana.setVisible(false);
		}
		
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

}