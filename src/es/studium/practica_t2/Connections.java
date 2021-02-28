package es.studium.practica_t2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Connections {


	String driver = "com.mysql.cj.jdbc.Driver";
	String url = "jdbc:mysql://localhost:3306/agencia_seguros?serverTimezone=UTC";
	String username = "root";
	String password = "Studium2020;"; 
	
	Statement statement = null;
    ResultSet rs = null;
    Connection connection = null;

	public Connection connect() {
		
		Connection connection = null;
		
		try
		{
			//Cargar los controladores para el acceso a la BD
			Class.forName(driver);
			//Establecer la conexión con la BD Empresa
			connection = DriverManager.getConnection(url, username, password);
			
		}
		catch (ClassNotFoundException cnfe)
		{
			System.out.println("Error 1-" + cnfe.getMessage());
		}
		catch (SQLException sqle)
		{
			System.out.println("Error 2-" + sqle.getMessage());
		}
		
		return connection;
	}
	
	public void closeConnection()
	{
		try {
			rs.close();
			statement.close();
			connection.close();
		} catch(SQLException sqle) {
			System.out.println("Error al cerrar la conexion: "+sqle.getMessage());
		}
	}
}


