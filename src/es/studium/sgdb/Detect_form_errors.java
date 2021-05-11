package es.studium.sgdb;

import java.awt.Label;
import java.util.regex.Pattern;

public class Detect_form_errors {

	public Detect_form_errors() {
		
	}
	
	public String detect_errors(String data_for_column, Label error_lbl, String column_name ) {
		
		String str_error = "";
				
		System.out.println(column_name);
		
		if(data_for_column.length() == 0) { // ================ CAMPO VACIO ================
						
			// SI ES UN FK
			if(column_name.substring(0,2).equals("ID")) {
				str_error = "n";
			} else {
				str_error = "e";
				error_lbl.setText("Campo vacío");
			}
			
			
		} else { // ================ CAMPO RELLENO ================
			
			// EMPIEZA POR "ID"
			if(column_name.substring(0,2).equals("ID")) {
				if( Pattern.matches("\\d+", data_for_column) ) { // SON SOLO DÍGITOS
					
					error_lbl.setText("");
					str_error = "i";
					
					// SI ES FK
					if(column_name.substring(0,2).equals("ID")) {
						
						Exist_id_intable exist_id_intable = new Exist_id_intable();
						boolean exist_id = exist_id_intable.exist_id(column_name, data_for_column);

						if(!exist_id) {
							str_error = "id";
							error_lbl.setText("Esa ID no existe");
						}
						
					}
					
				} else {
					error_lbl.setText("Patrón erroneo: solo digitos");
					str_error = "e";
				}
			} 
			else if(column_name.equals("TLF:")) 
			{
				if( Pattern.matches("\\d{9}", data_for_column) ) {
					error_lbl.setText("");
					str_error = "i";
				} else {
					error_lbl.setText("Patrón erroneo: 9 digitos");
					str_error = "e";
				}
			} 
			else if( column_name.equals("SUELDO:") ) 
			{
				if( Pattern.matches("\\d+", data_for_column) ) {
					str_error = "s";
					error_lbl.setText("");
				} else {
					error_lbl.setText("Patrón erroneo: solo digitos");
					str_error = "e";
				}
			}
			else if( column_name.equals("DNI:") ) 
			{
				if( Pattern.matches("\\d{8}[A-Za-z]", data_for_column) ) {
					str_error = "s";
					error_lbl.setText("");
				} else {
					error_lbl.setText("Patrón erroneo: 8 digitos y 1 letra");
					str_error = "e";
				}
			} 
			else if( column_name.equals("MAIL:") ) 
			{
				if( Pattern.matches("[\\w-]+@[a-z]+\\.[a-z]{2,}", data_for_column) ) {
					str_error = "s";
					error_lbl.setText("");
				} else {
					error_lbl.setText("Patrón erroneo: email@mail.com");
					str_error = "e";
				}
			} else {
				str_error = "s";
				error_lbl.setText("");
			}
		}
		
		
		return str_error;
	}

}