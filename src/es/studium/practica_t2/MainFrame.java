package es.studium.practica_t2;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;


public class MainFrame extends Connections implements WindowListener, ActionListener {

	Image icon = Toolkit.getDefaultToolkit().getImage("./logo.png");  
	Frame ventana = new Frame("Agencia Seguros S.A.");
	
	// MENUS
	MenuBar mnBar = new MenuBar();
	
	Menu mnuEmpleados = new Menu("Empleados");
	Menu mnuClientes = new Menu("Clientes");
	Menu mnuSeguros = new Menu("Seguros");
	Menu mnuUsuarios = new Menu("Usuarios");
	
	// MENUS ITEMS
	// CLIENTES
	MenuItem mniAltaCliente = new MenuItem("Alta");
	MenuItem mniBajaCliente = new MenuItem("Baja");
	MenuItem mniModificacionCliente = new MenuItem("Modificación");
	MenuItem mniConsultaCliente = new MenuItem("Consulta");
	// EMPLEADOS
	MenuItem mniAltaEmpleado = new MenuItem("Alta");
	MenuItem mniBajaEmpleado = new MenuItem("Baja");
	MenuItem mniModificacionEmpleado = new MenuItem("Modificación");
	MenuItem mniConsultaEmpleado = new MenuItem("Consulta");
	// SEGUROS
	Menu mniSegurosVoluntarios = new Menu("Seguros Voluntarios");
	Menu mniSegurosObligatorios = new Menu("Seguros Obligatorios");
	
	MenuItem mniAltaSeguroVoluntario = new MenuItem("Alta");
	MenuItem mniBajaSeguroVoluntario = new MenuItem("Baja");
	MenuItem mniModificacionSeguroVoluntario = new MenuItem("Modificación");
	MenuItem mniConsultaSeguroVoluntario = new MenuItem("Consulta");
	MenuItem mniAltaSeguroObligatorio = new MenuItem("Alta");
	MenuItem mniBajaSeguroObligatorio = new MenuItem("Baja");
	MenuItem mniModificacionSeguroObligatorio = new MenuItem("Modificación");
	MenuItem mniConsultaSeguroObligatorio = new MenuItem("Consulta");

	// SI NO TIENES PERMISOS
	Label lbl_noPermisos = new Label("Puedes hacer altas en el MenuBar");
	
	// TITLE
	Label lbl_title = new Label("Current Table", Label.CENTER);
	
	// NAVBAR
	Panel pnl_navbar = new Panel();
	Choice ch_searchcolumn = new Choice();
	TextField tf_selectrow = new TextField("");
	Button btn_searchrow = new Button("Buscar");
	Button btn_searchrow_exact = new Button("E");
	Checkbox cb_searchrow_invert = new Checkbox();
	Label lbl_chekbox_invert = new Label("Inverted");
	Button move_left_row = new Button("<");
	Label lbl_rows_limit = new Label("1-10");
	Button move_right_row = new Button(">");
	
	// TABLE PANEL
	Panel pnl_table = new Panel();
	Panel pnl_columns_table = new Panel();
	Panel pnl_rows_table = new Panel();
		
	// EXPORTAR PDF
	Button btn_export_pdf = new Button("Exportar a PDF");
	
		// COLORS
	Color WHITE_BG = new Color(245, 245, 245);
	Color WHITE_TEXT = new Color(251, 239, 244);
	Color GREY_ROW = new Color(54, 64, 64);
	
	
	// FRAME SIZE X
	int frame_width = 1000;
	int table_panel_width = 1000;
	
	// SHOW JUST 10 ROWS PER PAGE
	int first_row_limit = 1;
	int last_row_limit = 10;
	
	// FILTROS A LA HORA DE HACER CONSULTAS
	Sentencia sentencia = new Sentencia();
	
	// table_showing for QUERYs
	String table_name = "";			  // SegurosVoluntarios
	String table_incolumn_name = "";  // id ==> SeguroVoluntario
	
	
	// si hemos hecho consulta en otra tabla
	boolean changed_table = true;
	// almacena el ancho de cada COLUMNA para los ROWs
	int list_columns_width[] = new int[20];
	int foreign_key_columns[] = new int[5];
	
	// total de columnas de esta tabla.
	int table_columns_ammount = 0;
	// cuantas filas ha traido el QUERY
	int rows_ammount = 0;
	
    
    // DETECTAR BOTONES INSTANCIADOS ANONIMAMENTE
    AttributeButton list_FK_buttons[] = new AttributeButton[40];
    AttributeButton list_delete_buttons[] = new AttributeButton[10];
    AttributeButton list_update_buttons[] = new AttributeButton[10];
    
    
    // COLUMNS LIST
    int index_tables_list = 0;
    String clientes_real_columns[] = {"idCliente", "nombreCliente","apellido1Cliente","apellido2Cliente","dniCliente","tlfCliente","mailCliente","domicilioCliente"};	
	String empleados_real_columns[] = {"idEmpleado", "nombreEmpleado","apellido1Empleado","apellido2Empleado","dniEmpleado","tlfEmpleado","mailEmpleado","domicilioEmpleado", "sueldoEmpleado","idJefeFK"};    
	String sobligatorios_real_columns[] = {"idSeguroObligatorio", "requerimientoSeguroObligatorio", "idClienteFK", "idEmpleadoFK","idSeguroFK"};	
	String svoluntarios_real_columns[] = {"idSeguroVoluntario","duracionSeguroVoluntario", "idClienteFK", "idEmpleadoFK","idSeguroFK"};
	String seguros_real_columns[] = {"idSeguro", "precioSeguro","cubrimientoSeguro"};
	String[][] tables = { clientes_real_columns, empleados_real_columns, sobligatorios_real_columns, svoluntarios_real_columns, seguros_real_columns };	
	
	// PRIVILEGIOS
	final int ADMIN = 0;
	final int TEST = -1;
	private int privilegio = 0;
	
	
	// ==================================================== FUNCIONES ====================================================
	public MainFrame(int privilegio) {
				
		
		ventana.setIconImage(icon);
		ventana.setLayout(new FlowLayout());
		ventana.setBackground( new Color(45, 45, 45) );
		
		this.privilegio = privilegio;
		System.out.println(privilegio);
		// MENU BAR
		add_minimenubars();
		
		if(privilegio == ADMIN || privilegio == TEST) {
			
			// TITLE
			lbl_title.setFont( new Font( "Arial", 0, 35 ) );
			lbl_title.setForeground( WHITE_TEXT );
			lbl_title.setPreferredSize( new Dimension(1200, 80));
			ventana.add(lbl_title);
			
			
			// NAVBAR =============================================
			btn_searchrow.addActionListener(this);
			btn_searchrow_exact.addActionListener(this);
			move_left_row.addActionListener(this);
			move_right_row.addActionListener(this);
			
			tf_selectrow.setPreferredSize( new Dimension(400, 32) );
			btn_searchrow.setPreferredSize( new Dimension(100, 32) );
			btn_searchrow_exact.setPreferredSize( new Dimension(40, 32) );
			cb_searchrow_invert.setMinimumSize( new Dimension(32, 32));
			lbl_chekbox_invert.setForeground( new Color(251, 239, 244) );
			move_left_row.setPreferredSize( new Dimension(40, 32) );
			lbl_rows_limit.setForeground( new Color(251, 239, 244));
			move_right_row.setPreferredSize( new Dimension(40, 32) );
			
			lbl_chekbox_invert.setForeground(WHITE_TEXT);
			
			pnl_navbar.add( ch_searchcolumn );
			pnl_navbar.add( tf_selectrow );
			pnl_navbar.add( btn_searchrow );
			pnl_navbar.add( btn_searchrow_exact );
			pnl_navbar.add( cb_searchrow_invert );
			pnl_navbar.add( lbl_chekbox_invert );
			pnl_navbar.add( move_left_row );
			pnl_navbar.add( lbl_rows_limit);
			pnl_navbar.add( move_right_row );
			
			pnl_navbar.setBackground( new Color(50,70,70));
			ventana.add( pnl_navbar);
			
			
			// PANEL TABLE
			pnl_table.add(pnl_columns_table);
			pnl_table.add(pnl_rows_table);
			ventana.add(pnl_table);
			
			// EXPORT PDF
			btn_export_pdf.addActionListener(this);
			btn_export_pdf.setPreferredSize( new Dimension(550,30));
			btn_export_pdf.setBackground( new Color(240,240,240) );
			ventana.add(btn_export_pdf);
			
			// LOAD CLIENTES TABLE
			first_table_load("clientes", "Cliente", 0);
			
		} else {
			
			lbl_noPermisos.setForeground( WHITE_TEXT );
			lbl_noPermisos.setFont( new Font("Console", 1, 20));
			ventana.add( lbl_noPermisos );
			ventana.setSize(600,300);
			
		}
		
		
		
		//END
		ventana.setResizable(false);
		ventana.setLocationRelativeTo(null);
		ventana.addWindowListener(this);
		ventana.setVisible(true);

	}
	private void add_minimenubars() {
		
		// =========================== LISTENERS ===========================
		mniAltaCliente.addActionListener(this);
		mniAltaEmpleado.addActionListener(this);
		mniAltaSeguroVoluntario.addActionListener(this);
		mniAltaSeguroObligatorio.addActionListener(this);
		
		
		// ====== ALTAS ======
		mnuClientes.add(mniAltaCliente);
		mnuEmpleados.add(mniAltaEmpleado);
		mniSegurosVoluntarios.add(mniAltaSeguroVoluntario);
		mniSegurosObligatorios.add(mniAltaSeguroObligatorio);

		
		if(privilegio == ADMIN || privilegio == TEST) {
			
			// =========================== LISTENERS ===========================
			mniBajaCliente.addActionListener(this);
			mniBajaEmpleado.addActionListener(this);
			mniBajaSeguroVoluntario.addActionListener(this);
			mniBajaSeguroObligatorio.addActionListener(this);
			
			mniModificacionCliente.addActionListener(this);
			mniModificacionEmpleado.addActionListener(this);
			mniModificacionSeguroVoluntario.addActionListener(this);
			mniModificacionSeguroObligatorio.addActionListener(this);
			
			mniConsultaCliente.addActionListener(this);
			mniConsultaEmpleado.addActionListener(this);
			mniConsultaSeguroVoluntario.addActionListener(this);
			mniConsultaSeguroObligatorio.addActionListener(this);
			
			// CLIENTES
			mnuClientes.add(mniBajaCliente);
			mnuClientes.add(mniModificacionCliente);
			mnuClientes.add(mniConsultaCliente);
			
			// EMPLEADOS
			mnuEmpleados.add(mniBajaEmpleado);
			mnuEmpleados.add(mniModificacionEmpleado);
			mnuEmpleados.add(mniConsultaEmpleado);
			
			//  VOLUNTARIOS
			mniSegurosVoluntarios.add(mniBajaSeguroVoluntario);
			mniSegurosVoluntarios.add(mniModificacionSeguroVoluntario);
			mniSegurosVoluntarios.add(mniConsultaSeguroVoluntario);
			
			// OBLIGATORIOS
			mniSegurosObligatorios.add(mniBajaSeguroObligatorio);
			mniSegurosObligatorios.add(mniModificacionSeguroObligatorio);
			mniSegurosObligatorios.add(mniConsultaSeguroObligatorio);
		}
		
		
		mnBar.add(mnuClientes);
		mnBar.add(mnuEmpleados);
		mnuSeguros.add(mniSegurosVoluntarios);
		mnuSeguros.add(mniSegurosObligatorios);
		
		// FIN
		mnBar.add(mnuSeguros);
		ventana.setMenuBar(mnBar);
		
	}

	/**
	 * 
	 * @param rs
	 * @return el Length del resultset
	 */
	private int get_rs_len(ResultSet rs){
		
		int size = 0;
		
		// vamos a la ultima posicion del RS y obtenemos el indice.
		try {
			if(rs != null) {
				rs.last(); 
				size = rs.getRow();
				rs.first(); 
			}
		} catch(SQLException e) {
			System.out.println("Error GET RS LENGHT: " + e.getMessage());
		}
		
		// retornamos el Lenght del ResulSet
		return size;
	}
	
	/**
	 * Indica si un String acaba en otro String
	 * @param str
	 * @param end
	 * @return true si sí acaba, false si no
	 */
	public boolean str_ends_in(String str, String end) {
		// para que no de error, comprobamos 
		// que el primer String es mayor que el segundo
		if(str.length() >= end.length()) {
			//comprobamos
			if(str.substring( str.length() - end.length(), str.length()).equals(end) ) {
				return true;
			} else{
				return false;
			}
		} else {
			return false;
		}
	}
	/**
	 * itera un Array de objetos AttributeButtons
	 * para comprobar que el botón pasado como parámetro
	 * fue generado anonimamente como botón de Foreign Key
	 * de la casilla de una fila.
	 * @param button
	 * @return String vacio si no es un boton de FK, nombre de la columna en la que está si sí es un GK
	 */
	private String is_button_FK(Object button) {
		for(int y = 0; y < list_FK_buttons.length; y++) {
			
			if(list_FK_buttons[y] != null) {
										
				if(button.equals( list_FK_buttons[y].getButton() )) {
					return list_FK_buttons[y].getColumn_name();
				}
				
			}
		}
		return "";
	}

	/**
	 * Construye los botones de las columnas de la tabla.
	 * Almacena el ancho de cada una.
	 * Almacena cual columna es FK.
	 * Añade las columnas al Choice del Navbar si se ha cambiado de tabla.
	 * 
	 */
	private void build_table_columns() {
		
		int column_size = 0;
		
		// EMPTY array
		Arrays.fill(foreign_key_columns,-1);
		
		frame_width = 0;
		table_panel_width = 0;
			
		//GET table_columns_ammount
		table_columns_ammount = tables[ index_tables_list ].length;
		
		// ITERAR SOBRE LAS COLUMAS 
		for(int x = 0; x < table_columns_ammount; x++) {
			
			String current_columnname = tables[ index_tables_list ][x];
			String new_columnname = current_columnname;
							
			// CAMBIAR EL NOMBRE A LA COLUMNA => idSeguroObligatorio => ID
			int substring_tablename = new_columnname.indexOf(table_incolumn_name);
			if(substring_tablename > 0) {
				new_columnname = new_columnname.substring( 0 , substring_tablename);
				new_columnname = new_columnname.toUpperCase();
			}
			
			
			// SI ES LA PRIMERA VEZ QUE CARGAS ESTA TABLA
			if(changed_table) ch_searchcolumn.addItem(new_columnname);
			
			
			// dar un ancho a esta columna concreta
			column_size = 120;
			if(new_columnname.equals("DOMICILIO") || new_columnname.equals("MAIL") || new_columnname.equals("REQUERIMIENTO") ) 		column_size = 250;
			if(new_columnname.equals("ID")) 																						column_size = 50;
			if( str_ends_in(new_columnname ,"FK") ) { // SI ES UN FOREIGN KEY
				
				column_size = 80;
				
				// añadir que esta column es FOREIGN KEY
				// aydará a la hora de crear las ROWs, para hacer la GAP, BOTON en vez de LABEL
				for(int y = 0; y < foreign_key_columns.length; y++) {
					if(foreign_key_columns[y] == -1) {
						
						foreign_key_columns[y] = x+1;
						y = foreign_key_columns.length;
						
					}
				}
				
			}
			
			// añadir al Ancho del frame
			frame_width += column_size+8;
			
			// guardar el Ancho de esta columna
			
			list_columns_width[x] = column_size;
			
			Button current_column = new Button( " " + new_columnname );
			current_column.addActionListener(this);
			current_column.setPreferredSize( new Dimension( column_size , 30) );
			current_column.setBackground( new Color(240, 240, 240) );
			current_column.setForeground( new Color(37, 39, 30) );
			pnl_columns_table.add(current_column);
			
		}
						
		Panel delete_column = new Panel();
		delete_column.setPreferredSize( new Dimension(64,30));
		pnl_columns_table.add(delete_column);
		
	}
	/**
	 * Construye las filas de las tablas.
	 * Según los filtros textuales y de orden
	 * Según el número de filas que se muestren (11/20 de 230)
	 * Si el campo a construir es FK, pone un botón y no un Label
	 * Al final de cada fila coloca un boton de BORRAR y de ACTUALIZAR
	 */
	private void build_table_rows() {
		
		String current_row_id = "";
		boolean current_gap_fk = false;
		
		Arrays.fill(list_FK_buttons,null);
		Arrays.fill(list_delete_buttons,null);
		Arrays.fill(list_update_buttons,null);
		
		String sentence = "";
		
		try {
			sentence = "SELECT * FROM "+table_name + " " + sentencia.getFilter() + " " + sentencia.getOrder();
			System.out.println(sentence);
			rs = statement.executeQuery( sentence );
			
			//GET SIZE
			rows_ammount = get_rs_len(rs);
			
			// RECORREMOS TODAS LAS FILAS
			do {
				
				// si las filas, son entre los limites de 10
				if(rs.getRow() >= first_row_limit && rs.getRow() <= last_row_limit) {
					
					// RECORREMOS TODAS LAS gaps DE ESTA FILA
					for(int x = 0; x < table_columns_ammount; x++) {
						
						String current_columndata = rs.getString(x+1);
						
						// añadir CURRENCY
						if(current_columndata != null) {
							if(tables[index_tables_list][x].indexOf("sueldo") != -1) current_columndata += "€";
						}
						
						
						current_gap_fk = false;
						
						// DETECTAMOS SI ESTA gap ES fk
						if(current_columndata != null) {
							for(int y = 0; y < foreign_key_columns.length; y++) {
								if(foreign_key_columns[y] == x+1) {
									current_gap_fk = true;
									y = foreign_key_columns.length;
								}
							}
						}
						
						// SI NO ES FK
						if(!current_gap_fk) {
							
							// AÑADIMOS UN LABEL CON LA DATA
							Label lbl_current_columndata = new Label( current_columndata );
							lbl_current_columndata.setPreferredSize( new Dimension( list_columns_width[x] , 30) );
							lbl_current_columndata.setForeground( Color.white );
							lbl_current_columndata.setBackground( GREY_ROW );
							
							// obtener el de esta fila, que es el primer campo, para los botones de ACTUALIZAR Y BORRAR.
							if(current_row_id.length() == 0) current_row_id = current_columndata;
							
							pnl_rows_table.add(lbl_current_columndata);
						} else {
							
							// AÑADIMOS UN BOTÓN PQ ES fk, PARA ACCEDER A LA INFORMACION DEL REFERENCES
							Button btn_current_columndata = new Button( current_columndata );
									
							// LO AÑADIMOS AL ARRAY DE buttons con atributos
							for(int y = 0; y < list_FK_buttons.length; y++) {
								if(list_FK_buttons[y] == null) {
									list_FK_buttons[y] = new AttributeButton( tables[index_tables_list][x], "", "", "", btn_current_columndata);
									y = list_FK_buttons.length;
								}
							}
							
							// AÑADIMOS EL BOTON
							btn_current_columndata.addActionListener(this);
							btn_current_columndata.setPreferredSize( new Dimension( list_columns_width[x] , 30) );
							btn_current_columndata.setForeground( Color.black);
							btn_current_columndata.setBackground( Color.LIGHT_GRAY);
							
							pnl_rows_table.add(btn_current_columndata);
							
						}
					}
					
					// AÑADIMOS LOS BOTONES DE BORRAR Y ACTUALIZAR
					Button btn_update_row = new Button( " » " );
					Button btn_delete_row = new Button( " X " );
					
					btn_delete_row.addActionListener(this);
					btn_update_row.addActionListener(this);
					
					btn_delete_row.setPreferredSize( new Dimension(30 , 30) );
					btn_delete_row.setForeground( Color.black);
					btn_delete_row.setBackground( WHITE_TEXT );
					btn_update_row.setPreferredSize( new Dimension( 30 , 30) );
					btn_update_row.setForeground( Color.black);
					btn_update_row.setBackground( WHITE_TEXT );
					
					pnl_rows_table.add(btn_delete_row);
					pnl_rows_table.add(btn_update_row);
					
					// AÑADIMOS DICHOS BOTONES A SUS RESPECTIMOS ARRAYS
					for(int y = 0; y < list_delete_buttons.length; y++) {
						if(list_delete_buttons[y] == null) {
							list_delete_buttons[y] = new AttributeButton( "", table_name, table_incolumn_name, current_row_id , btn_delete_row);
							y = list_delete_buttons.length;
						}
					}
					
					for(int y = 0; y < list_update_buttons.length; y++) {
						if(list_update_buttons[y] == null) {
							list_update_buttons[y] = new AttributeButton( "", table_name, table_incolumn_name, current_row_id , btn_update_row);
							y = list_update_buttons.length;
						}
					}
					
					// RESET id FOR NEXT ROW
					current_row_id = "";
					
				}
			} while (rs.next());
			
		}  catch (SQLException e) {
			System.out.println("MAIN Error 2-" + e.getMessage());
		}
		
	}
	/**
	 * Función que decora a build_table_rows() y build_table_columns().
	 * Borra el choice del Navbar y el interiore de la tabla
	 * Redimensiona el Frame.
	 */
	private void load_table() {
					
		// Borramos el interior del Choice del NavBar
		if(changed_table) {
			ch_searchcolumn.removeAll();
			pnl_columns_table.removeAll();
		}
		// Borramos las columnas
		
		// borramos las filas
		pnl_rows_table.removeAll();
		
		
		if(changed_table) lbl_title.setText(table_name.toUpperCase().replace("_", " "));
		
		try {
			
			// OPEN STATEMENT
			connection = connect();		
			
			statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			
			// BUILD TABLE COLUMNS
			if(changed_table) build_table_columns();
			
			// BUILD TABLE DATA
			build_table_rows();				
			
			
		} catch (SQLException e) {
			System.out.println("Except_06: " + e.getMessage());
			frame_width = 1000;
		} finally {

			closeConnection();
			
		}

		
		// ACTUALIZAR LABEL LIMIT
		if(last_row_limit > rows_ammount) {
			lbl_rows_limit.setText( first_row_limit + "-" + rows_ammount + " de " + rows_ammount );
		} else {
			lbl_rows_limit.setText( first_row_limit + "-" + last_row_limit + " de " + rows_ammount );
		}
		
		
		// RESIZE FRAME AND PANEL
		// añadir margen de 90 pxls
		
		
		if(changed_table) {
			frame_width += 90;
			table_panel_width = frame_width;
			// Minimo de WIDTH de FRAME para no romper el NAVBAR
			if(frame_width <= 1000) {
				frame_width = 1000;
			}
			
			// RESIZE NAVBAR
			pnl_navbar.setPreferredSize( new Dimension(frame_width, 42) );
			
			// RESIZE PANELES DE LA TABLA
			pnl_columns_table.setPreferredSize( new Dimension(table_panel_width, 34) );
			pnl_rows_table.setPreferredSize( new Dimension(table_panel_width, 400) );
			pnl_table.setPreferredSize( new Dimension(table_panel_width, 410) );
		}
		
		
		// ACTUALIZAR LA TABLA
		pnl_table.revalidate();
		
		// resize el frame
		ventana.setSize(frame_width, 660);	
	}
	/**
	 * Para diferenciar entre si ha cargado la tabla, por un filtro de texto o de orden, o si ha cambiado de tabla.
	 * Asigna variables de la tabla actual.
	 * @param table_name
	 * @param table_incolumn_name
	 * @param index_columns_names
	 */
	private void first_table_load(String table_name, String table_incolumn_name, int index_columns_names) {
		
		// set row variables "por defecto"
		tf_selectrow.setText("");
		
		// borrar todos los datos de filtros
		sentencia.emptyAll();

		first_row_limit = 1;
		last_row_limit = 10;
		
		// asignar variables de la tabla
		this.table_name = table_name;
		this.table_incolumn_name = table_incolumn_name;
		this.index_tables_list = index_columns_names;
		
		// cargar tabla como nueva
		changed_table = true;
		load_table();
		changed_table = false;
		
	}

	/**
	 * Ejecutado cuando se hace click en un botón de FK.
	 * Rellena los datos de la fila referida del FK.
	 * 
	 * @param sentence sentencia de ejecución.
	 * @param index indice de la tabla
	 */
	private void show_dataFK_dialog(String sentence, int index) {
		
		// nuevo FK
		FK fk_dialog = new FK(ventana);
				
		try {
			
			// CONECTAMOS CON LA BBDD
			connection = connect();
			statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = statement.executeQuery(sentence);
					
			// cuantas ROWs colocamos para cambiar el Layout del FK
			int rows_ammount_dialog = 0;
			String column_name_dialog = "";
			int current_columns_ammount = 0;
			
			while(rs.next()) {
				
				// get lista nombres columnas
				current_columns_ammount = tables[index].length;
				
				// ITERAR LAS COLUMNAS
				for(int x = 0; x < current_columns_ammount; x++) {
					
					// CURRENT column name
					column_name_dialog = tables[index][x];
					
					// SI NO ESTÁ VACIO EL CAMPO
					if(rs.getString(x+1) != null) {
						
						// LABEL con el nombre de la columna
						Label lbl_column_name_dialog = new Label( column_name_dialog + ":   " , Label.RIGHT);
						lbl_column_name_dialog.setFont( new Font("Arial", Font.BOLD, 15) );
						
						//ADD TO PANEL
						fk_dialog.getDataPanel().add( lbl_column_name_dialog );
						
						// LABEL-BUTTON con el contenido del ROW
						if( str_ends_in(column_name_dialog, "FK") ) { // =================== SI ES FK ===================
							
							// añadimos un BUTTON 
							Button btn_column_data_dialog = new Button( rs.getString(x+1)  );
							btn_column_data_dialog.addActionListener(this);
							btn_column_data_dialog.setFont( new Font("Arial", 0, 12) );
							
							fk_dialog.getDataPanel().add( btn_column_data_dialog );
							
							// ===================  guardamos este FK en la lista =================== 
							for(int y = 0; y < list_FK_buttons.length; y++) {
								if(list_FK_buttons[y] == null) {
									
									// añadimos el FK recien añadido en este FK
									list_FK_buttons[y] = new AttributeButton( column_name_dialog, "", "", "" , btn_column_data_dialog);
																		
									y = list_FK_buttons.length;
								}
							}
							
						} else { // =================== NO ES FK ===================
							
							// añadir un mero LABEL
							Label lbl_column_data_dialog = new Label( rs.getString(x+1)  );
							
							lbl_column_data_dialog.setFont( new Font("Arial", 0, 12) );
							
							fk_dialog.getDataPanel().add( lbl_column_data_dialog );
						}
						
						
						rows_ammount_dialog++;
						
					}
				}
				
			}
			
			// cambias dimensiones y Layout del panel
			if(rows_ammount_dialog == 0) {

				fk_dialog.getDataPanel().add(new Label("ID no encontrada", Label.CENTER));
				
				fk_dialog.getDataPanel().setLayout( new GridLayout(1,2));
				fk_dialog.getDialogo().setSize(500, 100);
				fk_dialog.getDialogo().setVisible(true);
			} else {
				fk_dialog.getDataPanel().setLayout( new GridLayout(rows_ammount_dialog,2));
				fk_dialog.getDialogo().setSize(500, 40*rows_ammount_dialog);
				fk_dialog.getDialogo().setVisible(true);
			}
			
			

						
		} catch (SQLException e) {
			System.out.println("Error FK-" + e.getMessage());
		} finally {
			closeConnection();
		}
	}
	/**
	 * Se dedica a cargar al tabla, con un filtro textual sobre una columna
	 * @param exact indica si es una busqueda normal o exacta
	 */
	private void search_filter(boolean exact) {
		// transformar el nombre sin comillas
		String tf_value =  tf_selectrow.getText();
		tf_value = tf_value.replace("'","").replace("\"","");
		// ponemos en el TF el nombre sin comillas
		tf_selectrow.setText(tf_value);
		
		// nombre de la columna en la que buscar
		sentencia.setFilterColumnName(ch_searchcolumn.getSelectedItem());
					
		// NO SI ES FK convertir a nombre de columna FK
		if(!str_ends_in(sentencia.getFilterColumnName(),"FK")) {
			// LO CONVERTIMOS EN SQL COLUMN NAME
			// lo ponemos otra vez en minuscula
			// Le añadimos el nombre de la tabla, TJ: de "ID" --> "id" --> "idCliente"
			sentencia.setFilterColumnName( sentencia.getFilterColumnName().toLowerCase() );
			sentencia.setFilterColumnName( sentencia.getFilterColumnName() + table_incolumn_name );
		}
		
		
		// SI ESTA EL CheckBox INVERTED ACTIVADO  // WHERE LIKE con %%
		if(tf_selectrow.getText().length()!=0) {
			
			// si la busqueda es exacta
			if(exact) {
				if(cb_searchrow_invert.getState()) { 	sentencia.setFilter( "WHERE " + sentencia.getFilterColumnName() + " NOT LIKE '" 	+ tf_value + "'" );
				} else {								sentencia.setFilter( "WHERE " + sentencia.getFilterColumnName() + " LIKE '" 		+ tf_value + "'");}
			} else {
				if(cb_searchrow_invert.getState()) { 	sentencia.setFilter( "WHERE " + sentencia.getFilterColumnName() + " NOT LIKE '%" + tf_value + "%'");
				} else {								sentencia.setFilter( "WHERE " + sentencia.getFilterColumnName() + " LIKE '%" + tf_value + "%'");}
			}
			
		} else {
			sentencia.setFilter("");
		}
		
		// set row limites por defecto
		first_row_limit = 1;
		last_row_limit = 10;
					
		// ACTUALIZAR LA TABLA
		load_table();
	}
	@Override
	public void actionPerformed(ActionEvent e) {
				
		if(e.getSource().equals(btn_searchrow)) // ======================= BOTÓN DE BUSCAR =======================
		{
			search_filter(false);
		}
		else if( e.getSource().equals(btn_searchrow_exact) ) // ======================= BOTÓN DE BUSCAR EXACTO =======================
		{
			search_filter(true);
			
		} 
		else if(e.getSource().equals(move_right_row)) // ===================== MOVER DERECHA =====================
		{
			// si el maximo del limite, es menor que la cantidad de ROWs
			if(last_row_limit < rows_ammount) {
				first_row_limit += 10;
				last_row_limit += 10;			
				load_table();
			}
		} 
		else if(e.getSource().equals(move_left_row)) // ===================== MOVER IZQUIERDA =====================
		{
			// si el minimo del limite supera los 10
			if(first_row_limit > 10) {
				first_row_limit -= 10;
				last_row_limit -= 10;
				load_table();
			}
		} 
		else if( e.getSource().equals(btn_export_pdf)) {  // ===================== EXPORTAR PDF =====================
			
			// WORK IN PROGRESS
			System.out.println("Exporting in PDF...");
			
		} // ================================ ALTAS ================================
		else if(e.getSource().equals(mniAltaCliente))
		{
			new Alta("Cliente", "clientes", tables[0], privilegio);
		}
		else if(e.getSource().equals(mniAltaEmpleado))
		{			
			new Alta("Empleado", "empleados", tables[1], privilegio);
		}
		else if(e.getSource().equals(mniAltaSeguroObligatorio))
		{			
			new Alta("SeguroObligatorio", "seguros_obligatorios", tables[2], privilegio);
		}
		else if(e.getSource().equals(mniAltaSeguroVoluntario))
		{			
			new Alta("SeguroVoluntario", "seguros_voluntarios", tables[3], privilegio);
			
		} // ================================ BAJAS ================================
		else if(e.getSource().equals(mniBajaCliente))
		{
			new Baja("Cliente", "clientes", tables[0], "");
		}
		else if(e.getSource().equals(mniBajaEmpleado))
		{			
			new Baja("Empleado", "empleados", tables[1], "");
		}
		else if(e.getSource().equals(mniBajaSeguroObligatorio))
		{			
			new Baja("SeguroObligatorio", "seguros_obligatorios", tables[2], "");
		}
		else if(e.getSource().equals(mniBajaSeguroVoluntario))
		{			
			new Baja("SeguroVoluntario", "seguros_voluntarios", tables[3], "");
			
		} // ================================ MODIFICACIONES ================================
		else if(e.getSource().equals(mniModificacionCliente))
		{			
			new Modificacion ("Cliente", "clientes", tables[0], "");
		}
		else if(e.getSource().equals(mniModificacionEmpleado))
		{			
			new Modificacion ("Empleado", "empleados", tables[1], "");
		} 
		else if(e.getSource().equals(mniModificacionSeguroObligatorio))
		{			
			new Modificacion ("SeguroObligatorio", "seguros_obligatorios", tables[2], "");
		} 
		else if(e.getSource().equals(mniModificacionSeguroVoluntario))
		{			
			new Modificacion ("SeguroVoluntario", "seguros_voluntarios", tables[3], "");
			
		}  // ================================ CONSULTAS ================================
		else if(e.getSource().equals(mniConsultaCliente))
		{
			first_table_load("clientes", "Cliente", 0);
		} 
		else if(e.getSource().equals(mniConsultaEmpleado))
		{
			first_table_load("empleados", "Empleado", 1);
		} 
		else if(e.getSource().equals(mniConsultaSeguroVoluntario))
		{
			first_table_load("seguros_voluntarios", "SeguroVoluntario", 3);
		} 
		else if(e.getSource().equals(mniConsultaSeguroObligatorio))
		{
			first_table_load("seguros_obligatorios", "SeguroObligatorio", 2);
		}		
		else  // EL BOTÓN HA SIDO GENERADO ANONIMAMENTE ============================================
		{
			
			// STR DEL BOTÓN
			String current_btn_str = e.getActionCommand();
			
			// SI EL BOTON ES DE BORRAR =============================================================
			if(current_btn_str.equals(" X ")) {
				
				// recorremos la lista de botones de borrado
				for(int x = 0; x < list_delete_buttons.length; x++) {
					
					// si este no está vacio
					if(list_delete_buttons[x] != null) {
						
						// si el buton de esta lista, es el mismo que el boton pulsado
						if(e.getSource().equals( list_delete_buttons[x].getButton() )) {
							
							// nueva clase BAJA
							String current_table_column_name = list_delete_buttons[x].getTable_incolumn_name();
							String current_table_name = list_delete_buttons[x].getTable_name();
							String current_row_id = list_delete_buttons[x].getId();
							
							new Baja( current_table_column_name, current_table_name, tables[index_tables_list], current_row_id);
							// salimos del bucle
							x = list_update_buttons.length;
							
						}
					}
				}
				
			} else if(current_btn_str.equals(" » ")) { // BOTON DE ACTUALIZAR ======================
				
				// recorremos la lista de botones de actualizado
				for(int x = 0; x < list_update_buttons.length; x++) {
					
					// si este no está vacio
					if(list_update_buttons[x] != null) {
						
						// si el buton de esta lista, es el mismo que el boton pulsado
						if(e.getSource().equals( list_update_buttons[x].getButton() )) {
							
							// nueva clase UPDATE
							String current_table_column_name = list_delete_buttons[x].getTable_incolumn_name();
							String current_table_name = list_delete_buttons[x].getTable_name();
							String current_row_id = list_delete_buttons[x].getId();
							new Modificacion( current_table_column_name, current_table_name, tables[index_tables_list], current_row_id);
							// salimos del bucle
							x = list_update_buttons.length;
						}
					}
				}
				
			} else { // ==================  BOTON ES DE ordenar o FK ==================
			
				
				// DETECTAR SI EL BOTON ES DE UN FOREIGN KEY
				String fkcolumn_name = is_button_FK(e.getSource());
				String fkcolumn_intable_name = "";
				
				
				// ================== SI ES BOTON DE FK ==============================
				// si "is_fk()" no devuelve nada, no es FK
				if( fkcolumn_name.length() != 0 ) {
					
					
					// obtenemos los nombres de las tablas
					int index_table = 0;
					
					switch(fkcolumn_name){
						case "idClienteFK":	fkcolumn_name = "clientes";
											fkcolumn_intable_name = "Cliente";
											index_table = 0;
						break;
						
						case "idJefeFK": 	fkcolumn_name = "empleados";
											fkcolumn_intable_name = "Empleado";
											index_table = 1;
						break;
						
						case "idEmpleadoFK":	fkcolumn_name = "empleados";
												fkcolumn_intable_name = "Empleado";
												index_table = 1;
						break;
						
						case "idSeguroFK":	fkcolumn_name = "seguros";
											fkcolumn_intable_name = "Seguro";
											index_table = 4;
						break;
					}
					
					// Construir sentencia
					String sentence =  "SELECT * FROM "+fkcolumn_name+" WHERE id" + fkcolumn_intable_name + " = " + current_btn_str;
					
					// DISPLAY A DIALOG, WITH CURRENT FK DATA
					show_dataFK_dialog(sentence, index_table);
					
					
				} else { // ================== ES UN BOTÓN DE COLUMNA DE ORDEN ==================
					
					// TRIMEAR
					current_btn_str = current_btn_str.trim();
					
					// SI ESTA TABLA NO ES DE FK
					if(!str_ends_in(current_btn_str,"FK")) {
						// LO CONVERTIMOS EN SQL COLUMN NAME
						// lo ponemos otra vez en minuscula
						current_btn_str = current_btn_str.toLowerCase();
						// Le añadimos el nombre de la tabla, TJ: de "ID" --> "id" --> "idCliente"
						current_btn_str += table_incolumn_name;
					}
					
					
					// get nombre de la columna de SQL
					sentencia.setOrderColumnName( current_btn_str );
					
					// si he clickado por segunda vez en la misma columna
					if(sentencia.getLastOrderColumnNameClicked().equals( sentencia.getOrderColumnName() )) {
						
						sentencia.setOrder( "ORDER BY " + sentencia.getOrderColumnName() + " DESC");
						sentencia.setLastOrderColumnNameClicked("");
						
					} else {
						
						sentencia.setOrder( "ORDER BY " + sentencia.getOrderColumnName() + " ASC" );
						sentencia.setLastOrderColumnNameClicked( sentencia.getOrderColumnName() );
						
					}
					
					// restauramos los limites
					first_row_limit = 1;
					last_row_limit = 10;
					
					// actualizamos la tabla
					load_table();				
					
				}
				
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
