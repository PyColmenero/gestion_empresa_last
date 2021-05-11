package es.studium.sgdb;

import java.awt.Button;

public class AttributeButton {

	private String column_name;
	private String table_name;
	private String table_incolumn_name;
	private String id;
	private Button button;
	
	public AttributeButton(String column_name, String table_name, String table_incolumn_name, String id, Button button) {
		this.column_name = column_name;
		this.table_name = table_name;
		this.table_incolumn_name = table_incolumn_name;
		this.id = id;
		this.button = button;
	}

	public String getColumn_name() {
		return column_name;
	}

	public void setColumn_name(String column_name) {
		this.column_name = column_name;
	}

	public String getTable_name() {
		return table_name;
	}

	public void setTable_name(String table_name) {
		this.table_name = table_name;
	}
	
	public String getTable_incolumn_name() {
		return table_incolumn_name;
	}

	public void setTable_incolumn_name(String table_incolumn_name) {
		this.table_incolumn_name = table_incolumn_name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Button getButton() {
		return button;
	}

	public void setButton(Button button) {
		this.button = button;
	}
	
}