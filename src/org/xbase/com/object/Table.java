/**
 * 
 */
package org.xbase.com.object;

import java.util.List;

public class Table {

	private List<Column> columns;
	private boolean compressed=false;
	private long rowCount=0;
	private String name;
	
	public Table() {}
	
	public Table(String tableName) {
		this.name = tableName;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isCompressed() {
		return compressed;
	}
	
	public void setCompressed(boolean compressed) {
		this.compressed = compressed;
	}
	public List<Column> getColumns() {
		return columns;
	}
	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

	public long getRowCount() {
		return rowCount;
	}

	public void setRowCount(long rowCount) {
		this.rowCount = rowCount;
	}

}
