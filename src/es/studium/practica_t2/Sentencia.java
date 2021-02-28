package es.studium.practica_t2;

public class Sentencia {

	private String filter = "";
	private String filterColumnName = "";
	private String order = "";
	private String orderColumnName = "";
	private String lastOrderColumnNameClicked = "";
	
	public String getFilter() {
		return filter;
	}
	public void setFilter(String filter) {
		this.filter = filter;
	}
	public String getFilterColumnName() {
		return filterColumnName;
	}
	public void setFilterColumnName(String filter_column_name) {
		this.filterColumnName = filter_column_name;
	}
	public String getOrder() {
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
	}
	public String getOrderColumnName() {
		return orderColumnName;
	}
	public void setOrderColumnName(String order_column_name) {
		this.orderColumnName = order_column_name;
	}
	public String getLastOrderColumnNameClicked() {
		return lastOrderColumnNameClicked;
	}
	public void setLastOrderColumnNameClicked(String lastOrderColumnNameClicked) {
		this.lastOrderColumnNameClicked = lastOrderColumnNameClicked;
	}

	public Sentencia() {
		this.filter = "";
		this.filterColumnName = "";
		this.order = "";
		this.orderColumnName = "";
		this.lastOrderColumnNameClicked = "";
	}
	
	public void emptyAll() {
		this.filter = "";
		this.filterColumnName = "";
		this.order = "";
		this.orderColumnName = "";
		this.lastOrderColumnNameClicked = "";
	}
	
	
	
}
