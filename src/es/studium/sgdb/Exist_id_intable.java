package es.studium.sgdb;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Exist_id_intable extends Connections {

	private String conn_sentencia = "";
    
	public Exist_id_intable() {
		
	}
	
	boolean exist_id(String column_name, String id) {
		
		String current_table_name = "";
		String current_id_name = "";
		
		// GET NOMBRE DE LA TABLA Y CAMPO PRIMARY KEY
		if(column_name.equals("IDCLIENTEFK:")) {
			current_table_name = "clientes";
			current_id_name = "idCliente";
		} else if(column_name.equals("IDEMPLEADOFK:") || column_name.equals("IDJEFEFK:")) {
			current_table_name = "empleados";
			current_id_name = "idEmpleado";
		} else if(column_name.equals("IDSEGUROFK:")) {
			current_table_name = "seguros";
			current_id_name = "idSeguro";
		}
		
		// COMPROBAR SI EXISTE ESA ID
		
		connection = connect();
		try {
		
			statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            
			conn_sentencia = "SELECT * FROM " + current_table_name + " WHERE " + current_id_name + " = " + id;
            System.out.println(conn_sentencia);
            
            rs = statement.executeQuery(conn_sentencia);
            
            if(rs.next()) {
            	System.out.println("NEXT: " + rs.getString(1));
            	return true;
            }
            
		}
        catch (SQLException sqle) {
               System.out.println("Error 2-"+sqle.getMessage());
		} finally {
			closeConnection();
		}

		return false;
	}
	
}