package es.studium.sgdb;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {

	public Log(String userName, String action, String sentence) {
		
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
		try {
			FileWriter file_writer = new FileWriter("log.log", true);
			BufferedWriter buffer = new BufferedWriter(file_writer);
			PrintWriter printer = new PrintWriter(buffer);
			
			printer.print("[" + format.format(date) + "]");
			printer.print("[" + userName + "]");
			printer.print("[" + action + "]");
			printer.println("[" + sentence + "]");
			
			
			printer.close();
			buffer.close();
			file_writer.close();
			
			System.out.println("Log Succes.");
			
			
		} catch(IOException i){
			System.out.println("Error_01: " + i.getMessage());
		}
	}
	
}