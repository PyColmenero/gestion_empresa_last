package es.studium.sgdb;

import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.awt.Desktop;

public class PDF extends Connections{

	String clientes_real_columns[] = { "ID", "Nombre", "1º Appellido", "2º Apellido", "DNI", "TLF", "MAIL", "Domicilio" };
	String empleados_real_columns[] = { "ID", "Nombre", "1º Appellido", "2º Apellido", "DNI", "TLF", "MAIL", "Domicilio", "Sueldo", "Nombre Jefe" };
	String sobligatorios_real_columns[] = { "ID", "Requerimientos", "Nombre Cliente", "Nombre Empleado", "Seguro" };
	String svoluntarios_real_columns[] = { "ID", "Duración", "Nombre Cliente", "Nombre Empleado", "Seguro" };
	String[][] tablesPDF = { clientes_real_columns, empleados_real_columns, sobligatorios_real_columns, svoluntarios_real_columns };
	
	public PDF(String tableName, int tableIndex) {

		System.out.println( "Exporting in PDF..." );
		final String FILE_NAME = tableName + ".pdf";
		
		String sentencia = "";
		int TABLE_LEN = tablesPDF[tableIndex].length;

		
		try {
			Document document = new Document(PageSize.A4.rotate());
			File path = new File ( FILE_NAME );

			FileOutputStream ficheroPdf = new FileOutputStream( FILE_NAME );
			PdfWriter.getInstance(document, ficheroPdf).setInitialLeading(20);
			document.open();
			
			System.out.println(tableName);
			Paragraph title = new Paragraph( tableName.replace("_", " ").toUpperCase() ,
    				FontFactory.getFont("arial",   // fuente
    				24,                            // tamaño
    				Font.ITALIC,                   // estilo
    				BaseColor.BLACK));             // color
			document.add(title);
			
			Image img = Image.getInstance("logo64.png");
	        img.setAbsolutePosition(650f, 540f);
	        document.add(img);

			
			try {
				// conectamos a la base de datos
				connection = connect();
	            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
	            // formulamos sentencia 
	            
	            if(tableName.equals("seguros_obligatorios")) {
	            	sentencia = "SELECT "
	            			+ "    a.idSeguroObligatorio, "
	            			+ "    a.requerimientoSeguroObligatorio, "
	            			+ "    b.nombreCliente, "
	            			+ "    c.nombreEmpleado, "
	            			+ "    d.cubrimientosSeguro "
	            			+ "FROM "
	            			+ "    seguros_obligatorios AS a "
	            			+ "        JOIN "
	            			+ "    clientes AS b "
	            			+ "        JOIN "
	            			+ "    empleados AS c "
	            			+ "        JOIN "
	            			+ "    seguros AS d ON a.idClienteFK = b.idCliente "
	            			+ "        AND a.idEmpleadoFK = c.idEmpleado "
	            			+ "        AND a.idSeguroFK = d.idSeguro "
	            			+ "ORDER BY a.idSeguroObligatorio;";
	            } else if(tableName.equals("seguros_voluntarios") ) {
	            	sentencia = "SELECT "
	            			+ "    a.idSeguroVoluntario, "
	            			+ "    a.duracionSeguroVoluntario, "
	            			+ "    b.nombreCliente, "
	            			+ "    c.nombreEmpleado, "
	            			+ "    d.cubrimientosSeguro "
	            			+ "FROM "
	            			+ "    seguros_voluntarios AS a "
	            			+ "        JOIN "
	            			+ "    clientes AS b "
	            			+ "        JOIN "
	            			+ "    empleados AS c "
	            			+ "        JOIN "
	            			+ "    seguros AS d ON a.idClienteFK = b.idCliente "
	            			+ "        AND a.idEmpleadoFK = c.idEmpleado "
	            			+ "        AND a.idSeguroFK = d.idSeguro "
	            			+ "ORDER BY a.idSeguroVoluntario;";
	            } else if(tableName.equals("clientes")) {
	            	sentencia = "SELECT * FROM clientes";
	            } else if(tableName.equals("empleados")) {
	            	sentencia = "SELECT "
	            			+ "    a.idEmpleado, "
	            			+ "    a.nombreEmpleado, "
	            			+ "    a.apellido1Empleado, "
	            			+ "    a.apellido2Empleado, "
	            			+ "    a.dniEmpleado, "
	            			+ "    a.tlfEmpleado, "
	            			+ "    a.mailEmpleado, "
	            			+ "    a.domicilioEmpleado, "
	            			+ "    concat(a.sueldoEmpleado, '€'), "
	            			+ "    b.nombreEmpleado "
	            			+ " FROM "
	            			+ "    empleados AS a "
	            			+ "        LEFT JOIN "
	            			+ "    empleados AS b ON a.idJefeFK = b.idEmpleado "
	            			+ "ORDER BY a.idEmpleado;";
	            }
	            rs = statement.executeQuery(sentencia);
	            
	            PdfPTable table = new PdfPTable(TABLE_LEN);
	            for(int x = 0; x < TABLE_LEN; x++) {
	            	
	            	Paragraph text = new Paragraph( tablesPDF[tableIndex][x] ,
            				FontFactory.getFont("arial",   // fuente
            				14,                            // tamaño
            				Font.ITALIC,                   // estilo
            				BaseColor.WHITE));             // color



            		PdfPCell cell = new PdfPCell( text );
            		cell.setPadding(5);
            		cell.setBackgroundColor(new BaseColor(33, 33, 33));
            		table.addCell(cell);

            	}
	            int row_index = 0;
	            while(rs.next()) {
	            	
	            	
	            	row_index++;
	            	
	            	for(int x = 0; x < TABLE_LEN; x++) {
	            		String current_val = rs.getString(x+1);
	            		PdfPCell cell = new PdfPCell(new Paragraph( current_val ));
	            		cell.setPadding(5);
	            		if(row_index % 2 == 0) cell.setBackgroundColor(new BaseColor(244, 244, 244));
	            		table.addCell(cell);

	            	}
	            }
	            
	            table.setSpacingBefore(20f);
	            document.add(table);

			}
	        catch (SQLException sqle) {
	               System.out.println("Error 2-"+sqle.getMessage());
			} finally {
				closeConnection();
			}
			
			document.close();
			//new Log(username, "PDF", sentencia.trim());
			Desktop.getDesktop().open(path);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	

	public static void main(String[] args) {

		new PDF("seguros_obligatorios", 2);

	}

}