/**
 * 
 */
package org.xbase.com.object;

import java.util.ArrayList;
import java.util.List;
public class View {
	
	private String baseTableName;
	private String viewName;
	private List<String> columns = new ArrayList<String>();
	
	public View() {}
	public View(String viewName) {
		this.viewName = viewName;
	}
	public String getBaseTableName() {
		return baseTableName;
	}
	public void setBaseTableName(String baseTableName) {
		this.baseTableName = baseTableName;
	}
	public String getName() {
		return viewName;
	}
	public void setName(String name) {
		this.viewName = name;
	}
	public String getViewName() {
		return viewName;
	}
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}
	public List<String> getColumns() {
		return columns;
	}
	public void setColumns(List<String> columns) {
		this.columns = columns;
	}
	public void addColumn(String columnName) {
		columns.add(columnName);
	}
	public void clearColumns() {
		columns.clear();
	}
	public void deleteColumn(String columnName) {
		columns.remove(columnName);
	}
}
