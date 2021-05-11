package es.studium.sgdb;

import java.awt.Button;
import java.awt.Choice;
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

public class Alta extends Connections implements WindowListener, ActionListener{

	Image icon = Toolkit.getDefaultToolkit().getImage("./logo.png");  
	Frame ventana = new Frame("Alta - Agencia Seguros S.A.");
	Label lbl_title = new Label("Date de alta");
	Panel pnl_form = new Panel();
	Button btn_clear = new Button("Borrar");
	Button btn_accept = new Button("Aceptar");
	Button btn_fill = new Button("F");
	
    // DIALOG - output
    Label lbl_operation_output_title = new Label("");
    Dialog dlg_operation_output = new Dialog(ventana, "Operación Inserción", true);

    // DIALGO - ARE YOU SURE
 	Dialog dlg_areyousure = new Dialog(ventana,"Estás Seguro?",true);
 	Label lbl_areyousure = new Label("Estás seguro de que quieres darte de Alta?\n");
 	Button btn_notsure = new Button("Cancelar");
 	Button btn_suresure = new Button("Dar de Alta");
 	
 	// OTROS DATOS
	String table_column_name = "";
	String table_name = "";
	int columns_len = 0;
 	final int FRAME_WIDTH = 750;
 	int frame_height = 0;
	
	// DDBB
	String sentencia = "";
 	
	// COLOR
 	Color WHITE_BG = new Color(245, 245, 245);
 	
 	//FORM CORRECTOR
 	Detect_form_errors form_correcter = new Detect_form_errors();
 	
 	
 	
	public Alta(String table_column_name, String table_name, String[] sql_column_names, int priv) {
	
		// ASIGNACIONES 
		this.table_name = table_name;
		this.table_column_name = table_column_name;
		this.columns_len = sql_column_names.length;
		
		// CALCULAR CUANTAS COLUMAS de 3 en 3.
		// si hay 3 columnnas, y_grid será 1
		// si hay 4 columnnas, y_grid será 2
		// si hay 6 columnnas, y_grid será 2
		// si hay 7 columnnas, y_grid será 3
		int add = (columns_len%3 != 0) ? 1 : 0;
		int y_grid = columns_len / 3 + add;
				
		// AÑADIR COMPONENTES A LA VENTANA
		ventana.setIconImage(icon);
		ventana.setLayout(new FlowLayout());
		ventana.setBackground( WHITE_BG );
		
		// TITULO
		lbl_title.setFont( new Font("Arial", Font.BOLD ,30));
		lbl_title.setText("Date de alta como " + table_column_name);
		ventana.add(lbl_title);
		
		// PANEL CON EL FORMULARIO
		pnl_form.setPreferredSize( new Dimension(FRAME_WIDTH, columns_len*30) );
		pnl_form.setLayout(new GridLayout(y_grid,3));
		
		
		for(int x = 0; x < columns_len; x++) {
			
			String nombre_columna = sql_column_names[x].replace(table_column_name,"");
			nombre_columna = nombre_columna.toUpperCase();
			nombre_columna += ":";
			
			if(!nombre_columna.equals("ID:")) {
				
				// PANEL CON TODOS LOS COMPONENTES DE ESTE FORM GAP
				Panel pnl_comp = new Panel();
				pnl_comp.setLayout( new GridLayout(3,1) );
				
				// COLUMN NAME LABEL
				Label lbl_current_column = new Label( nombre_columna , Label.CENTER);
				pnl_comp.add(lbl_current_column);
				
				
				// ========================= ES UN FOREIGN KEY, AÑADIMOS CHOICE =========================
				if(nombre_columna.substring(0,2).equals("ID")) { 
					
					Choice ch_current_column = new Choice();
					Panel pnl_ch = new Panel();
					
					// RELLENAMOS EL CHOICE CON LAS LISTAS
					fill_fkchoice(ch_current_column, nombre_columna);
					
					// Añadimos el choice
					pnl_ch.add(ch_current_column);
					pnl_comp.add(pnl_ch);
					
				} else { // ========================= NO ES UN FOREIGN KEY, AÑADIMOS TEXTFIELD y LABEL =========================
					
					// LABEL de ERROR
					Label lbl_column_error = new Label("", Label.CENTER);
					lbl_column_error.setFont( new Font("Arial", 1, 11) );
					lbl_column_error.setForeground(Color.red);
					pnl_comp.add(lbl_column_error);
					
					// añadimos TextField 
					TextField tf_current_column = new TextField(20);
					Panel pnl_tf = new Panel();
					pnl_tf.add(tf_current_column);
					pnl_comp.add(pnl_tf);
				}
				
				pnl_form.add(pnl_comp);
			}
		}
		ventana.add(pnl_form); 
		
		
		// BOTONES DE RELLENAR, BORRAR y ACEPTAR
		if(priv == 0) {
			btn_fill.addActionListener(this);
			btn_fill.setPreferredSize( new Dimension(30,30) );
			ventana.add(btn_fill);
		}
		
		btn_clear.addActionListener(this);
		btn_clear.setPreferredSize( new Dimension(200,30) );
		ventana.add(btn_clear);
		
		btn_accept.addActionListener(this);
		btn_accept.setPreferredSize( new Dimension(200,30) );
		ventana.add(btn_accept);
		
		// DAR ALTURA A LA VENTANA
		frame_height = 150+(columns_len*30);
		ventana.setSize(FRAME_WIDTH, frame_height);
		ventana.setResizable(false);
		ventana.setLocationRelativeTo(null);
		ventana.addWindowListener(this);
		ventana.setVisible(true);
		
		// DIALOG
		dlg_operation_output.setLayout(new FlowLayout());
		dlg_operation_output.add(lbl_operation_output_title);
		dlg_operation_output.setSize(300,150);
		dlg_operation_output.setLocationRelativeTo(null);
		dlg_operation_output.addWindowListener(this);
		
		
		
		// ARE YOU SURE
		dlg_areyousure.setLayout(new FlowLayout());
		dlg_areyousure.add(lbl_areyousure);
		
		btn_notsure.addActionListener(this);
		btn_suresure.addActionListener(this);
		dlg_areyousure.add(btn_notsure);
		dlg_areyousure.add(btn_suresure);
		
		dlg_areyousure.setSize(300,110);
		dlg_areyousure.setLocationRelativeTo(null);
		dlg_areyousure.addWindowListener(this);
		
	}

	private void fill_fkchoice(Choice ch_current_column, String column_name) {
		
		String current_row_str = "";
		String current_table_name = "";
				
		// OBJETENEMOS NOMBRE DE LA TABLA
		if(column_name.equals("IDCLIENTEFK:")) {												current_table_name = "clientes";
		} else if(column_name.equals("IDEMPLEADOFK:") || column_name.equals("IDJEFEFK:")) {		current_table_name = "empleados";
		} else if(column_name.equals("IDSEGUROFK:")) {											current_table_name = "seguros";	}
		
		// añadimos por si no quiere poner nada.
		ch_current_column.add("Ninguno.");
		
		try {
			
			// conectamos a la base de datos
			connection = connect();
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            sentencia = "SELECT * FROM " + current_table_name;
            rs = statement.executeQuery(sentencia);
                
            // recorremos el ResultSet
            while(rs.next()) {
            	
            	current_row_str = "";
                 
            	// recorremos la fila actual
            	for(int x = 0; x < 3; x++) {
            		
            		// añadimos el dato de esta columna
            		current_row_str += rs.getString(x+1) + " - ";

            	}
            	
            	// añadimos al Choice el String con los datos de las colunnas
            	current_row_str = current_row_str.substring(0,current_row_str.length()-3);
            	ch_current_column.add( current_row_str );
            	
            }
            
		}
        catch (SQLException sqle) {
               System.out.println("Error 1-"+sqle.getMessage());
		} finally {
			
			closeConnection();
			
		}
		
		
	}

	String testData[] = {"Candela","Amor","Martin","30254931Q","623535742","candelaAmorMartin@gmail.com","Calle Álvaro Mutís ático Nº1"};
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if( e.getSource().equals(btn_clear) ) { // ============== BOTÓN DE BORRAR ==============
			// recorremos las columnas
			for(int x = 0; x < columns_len-1; x++) {
				// EN EL CASO DE QUE SEA UN CHOICE, QUE NO PETE
				try {
					// accedemos a cada TextField
					Panel current_pnl_form = (Panel) pnl_form.getComponent(x);
					Panel pnl_tf = 			 (Panel) current_pnl_form.getComponent(2);
					TextField current_tf = 	 (TextField) pnl_tf.getComponent(0);
					Label lbl_current_error = (Label) current_pnl_form.getComponent(1);
					lbl_current_error.setText("");
					
					current_tf.setText("");
					
				} catch( ArrayIndexOutOfBoundsException error) { // Dará error si es un CHOICE
					Panel current_pnl_form = (Panel) pnl_form.getComponent(x);
					Panel pnl_tf = 			 (Panel) current_pnl_form.getComponent(1);
					Choice current_ch = 	 (Choice) pnl_tf.getComponent(0);
					
					current_ch.select(0);
				}
				
			}
		} else if(e.getSource().equals(btn_fill)) { // ============== RELLENAR TF CON DATOS PARA MAYOR VELOCIDAD DE PRUEBAS ==============
		
			for(int x = 0; x < columns_len-1; x++) {
				
				// EN EL CASO DE QUE SEA UN CHOICE, QUE NO PETE
				try {
					if(x < testData.length) {
						Panel current_pnl_form 	= (Panel) pnl_form.getComponent(x);
						Panel pnl_tf = 			 (Panel) current_pnl_form.getComponent(2);
						TextField current_tf 	= (TextField) pnl_tf.getComponent(0);
						
						current_tf.setText(testData[x]);
					}
				} catch( ArrayIndexOutOfBoundsException error) {
					System.out.println("Error ALTA 1: " + error.getMessage());
				}
				
			}
			
		} else if(e.getSource().equals(btn_accept)) {
		
			sentencia = build_sentence_alta();
			
			if(sentencia != null) {
				dlg_areyousure.setVisible(true);
			}
			
			
			
			
		} else if(e.getSource().equals(btn_notsure)) {
			
			dlg_areyousure.setVisible(false);

		} else if( e.getSource().equals(btn_suresure) ) {		
			lbl_areyousure.setText("Loading...");
			
			dlg_areyousure.setVisible(false);
			
			insert(sentencia);
		}
	}
	
	
	/**
	 * Esta función creará una sentencia según si los datos del formualario
	 * están correctamente escritos
	 * 
	 * @return una sentencia SQL de INSERT o NULL
	 */
	private String build_sentence_alta() {
		
		boolean has_errors = false;
		String column_name = "";
		String data_in_tf = "";
		String str_error;
		// START SENTENCE
		sentencia = "INSERT INTO " + table_name + " VALUES(NULL";
		
		// BUILD VALUES
		for(int x = 0; x < columns_len-1; x++) {
			
			// Panel con el TF y LBLs
			Panel current_pnl_form = 		(Panel) pnl_form.getComponent(x);
			
			// ==================================== SI CONTIENE UN TEXTFIELD ====================================
			if(current_pnl_form.getComponent(1).getName().indexOf("panel") == -1) {
				
				/*  ESTRUCTURA DE FORMULARIO
				 * PANEL ROOT
				 * 		LABEL NombreColumna
				 * 		LABEL error
				 * 		PANEL
				 * 			TextTield
				 * */
				Label current_lbl_form = 		(Label) current_pnl_form.getComponent(0);
				Label current_lbl_error_form = 	(Label) current_pnl_form.getComponent(1);
				Panel pnl_tf = 					(Panel)	current_pnl_form.getComponent(2);
					TextField tf_current = 			(TextField) 	pnl_tf.getComponent(0);
				
				// NOMBRES
				column_name = current_lbl_form.getText();
				// QUITAR COMILLAS
				data_in_tf = tf_current.getText();
				data_in_tf = data_in_tf.replace("'","").replace("\"","");
				tf_current.setText(data_in_tf);
								
				// si devuelve "e", hay un error en los datos puestos
				// si devuelve "s", el dato es String, por lo tanto va entre comillas
				// si devuelve "i", el dato es numérico, sin comillas
				// si devuelve "n", el dato es AUTO_INCREMENT, es NULL
				str_error = form_correcter.detect_errors( data_in_tf, current_lbl_error_form, column_name);
				
				switch(str_error) {
					case "i":	sentencia += ", " + data_in_tf;
					break;
					case "s":	sentencia += ", '" + data_in_tf + "'";
					break;
					case "n":	sentencia += ", NULL";
					break;
					case "e":	has_errors = true;
					break;
				}
				
			} else { // ==================================== SI CONTIENE UN choice ====================================
				
				Panel pnl_ch = 		(Panel)	 current_pnl_form.getComponent(1);
				Choice ch_current = (Choice) pnl_ch.getComponent(0);
				
				String choice_value = ch_current.getSelectedItem();
				
				if(!choice_value.equals("Ninguno.")) {
					
					String current_choice_id = choice_value.split(" - ")[0];
					sentencia += ", " + current_choice_id;
					
				} else {
					sentencia += ", NULL";
				}
			}
		}
		
		sentencia += ");";
		
		return (has_errors) ? null : sentencia;
	}

	private void insert(String sentencia) {
		
		// DO QUERY
		String output = "";
		
		try {
			connection = connect();
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            statement.executeUpdate(sentencia);
            
            new Log(username, "ALTA", sentencia);

            output = "Te has dado de alta correctamente";
		}
        catch (SQLException sqle) {
        	output = "Error 3-"+sqle.getMessage();
		} finally {
			
			closeConnection();
			
			System.out.println(": " + output);
			lbl_operation_output_title.setText(output);
			dlg_operation_output.setVisible(true);
		}
		
	}
	
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