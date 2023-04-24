/**
 * 
 */
package org.xbase.com.object;

import java.util.ArrayList;
import java.util.List;


public class Index {

	private boolean compressed=false;
	private boolean joinIndex=false;
	private boolean reverse=false;
	private boolean unique=false;
	private List<String> columns = new ArrayList<String>();
	private String indexName;
	
	public Index() {}
	
	public Index(String indexName) {
		this.indexName = indexName;
	}
	
	public boolean isCompressed() {
		return compressed;
	}
	public void setCompressed(boolean compressed) {
		this.compressed = compressed;
	}
	public boolean isJoinIndex() {
		return joinIndex;
	}
	public void setJoinIndex(boolean joinIndex) {
		this.joinIndex = joinIndex;
	}
	public boolean isReverseIndex() {
		return reverse;
	}
	
	public void setReverse(boolean reverse) {
		this.reverse = reverse;
	}
	public boolean isUniqueIndex() {
		return unique;
	}
	public void setUnique(boolean unique) {
		this.unique = unique;
	}
	public List<String> getColumns() {
		return columns;
	}
	public void setColumns(List<String> columns) {
		this.columns = columns;
	}
	public String getIndexName() {
		return indexName;
	}
	public void setIndexName(String indexName) {
		this.indexName = indexName;
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
