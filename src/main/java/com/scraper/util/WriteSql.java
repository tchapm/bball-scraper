package com.scraper.util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Set;

import com.csvreader.CsvReader;

public class WriteSql {
	private ResourceBundle resource; 
	public String dbUrl;
	public String dbUser;
	public String dbPassword;
	
	public static String getDriverName() {
		return "com.mysql.jdbc.Driver";
	}

	public void storeInDb(String csvFilePath, HashMap<String,String> queryColumns){
		Connection conn = null;
		String tableName = this.resource.getString("TableName");
		int keysLength = 0;
		boolean addOnDuplicate = true;
		try {
			conn = DriverManager.getConnection(this.dbUrl, this.dbUser, this.dbPassword);
			String keysLengthStr = this.resource.getString("KeysLength");
			
			if (keysLengthStr != null && keysLengthStr.length() > 0) {
				keysLength = Integer.parseInt(keysLengthStr);
			}
			String addOnDuplicateStr = getTableProp(this.resource,"addOnDuplicate");
			addOnDuplicate = Boolean.parseBoolean(addOnDuplicateStr);
			insertRowsToTable(conn, csvFilePath, queryColumns, tableName, keysLength, addOnDuplicate);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					// Do nothing
				}
			}
		}
	}
	
	static String getTableProp(ResourceBundle tableProps, String propName) {
		if(tableProps.containsKey(propName)) {
			try {
				return tableProps.getString(propName);
			} catch (Exception e) {
				;// do nothing
			}
		}
		return null;
	}
	
	private void insertRowsToTable(Connection conn, String inFilePath, HashMap<String, String> queryColumns, String tableName,
			int keysLength, boolean addOnDuplicate) {
		try {
			PreparedStatement preparedStatement = 
					conn.prepareStatement(getInsertString(queryColumns.keySet(), tableName, keysLength, addOnDuplicate));
			int rowNum = 0;
			CsvReader reader = new CsvReader(new FileReader(inFilePath));
            while (reader.readRecord()){
            	int j=0, i=0;
            	for(String colName : queryColumns.keySet()){
            		int type = java.sql.Types.INTEGER;
    				try {
    					if (j>=reader.getColumnCount()){
        					preparedStatement.setNull(i+1, type);
    					} else if (queryColumns.get(colName).equals("INTEGER")) {
    						type = java.sql.Types.INTEGER;
//    						System.out.println("Row: " + rowNum + "Input: " + Integer.parseInt(colVals[j]));
    						preparedStatement.setInt(i+1, WriteSql.parseTheVal(reader.get(j)));
    						j++;
    					} else if (queryColumns.get(colName).equals("FLOAT")) {
    						type = java.sql.Types.DOUBLE;
//    						System.out.println("Row: " + rowNum + "Input: " + new BigDecimal(colVals[j]));
    						String value = WriteSql.stripQuotes(reader.get(j));
    						preparedStatement.setBigDecimal(i+1, new BigDecimal(Double.parseDouble(value)));
    						j++;
    					} else if (queryColumns.get(colName).equals("STRING")) {
    						type = java.sql.Types.VARCHAR;
    						String colStrVal = reader.get(j);
//    						System.out.println("Row: " + rowNum + "Input: " + colStrVal);
    						colStrVal = (colStrVal.startsWith("\"")) ? colStrVal.substring(1, colStrVal.length()-1) : colStrVal;
    						preparedStatement.setString(i+1, colStrVal);
    						j++;
//    					}else if (queryColumns.get(colName).equals("DATE")) {
//    						type = java.sql.Types.VARCHAR;
////    						System.out.println("Row: " + rowNum + " Input: " + this.day);
//    						preparedStatement.setString(i+1, this.day);
    					}else if (queryColumns.get(colName).equals("NULL")) {
    						type = java.sql.Types.VARCHAR;
    						preparedStatement.setNull(i+1, type);
    					}
    				} catch (Exception e) {
    					e.printStackTrace();
    					preparedStatement.setNull(i+1, type);
    				}
    				i++;
            	}
            	preparedStatement.addBatch();
				if (rowNum%100 == 0) {
					int[] successArr = preparedStatement.executeBatch();
					System.out.println("Table: "+tableName+", Rows inserted: "+rowNum);
					preparedStatement.clearBatch();
				}
				rowNum++;
            }
            int[] successArr = preparedStatement.executeBatch();
            System.out.println("Total rows inserted: "+rowNum);
    		preparedStatement.clearBatch();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static int parseTheVal(String inputString) {
		String value = WriteSql.stripQuotes(inputString);
		int intVal;
		try{
			intVal = Integer.parseInt(value);
		}catch(NumberFormatException e){
			Double val = Double.parseDouble(value);
			intVal = val.intValue();
		}
		return intVal;
	}
	private static String stripQuotes(String value){
    	if(value.contains("\"")){
    		value = value.replace("\"", "");
		}
    	return value;
	}
	private static String getInsertString(Set<String> columnNames, String tableName, int keysLength, boolean addOnDuplicate) {
//		@TODO take out ignore when figure out how to insert longer strings
		String ignoreStr = "IGNORE";
		StringBuilder insertString = new StringBuilder("INSERT IGNORE INTO " + tableName + "(");
		Iterator<String> it = columnNames.iterator();
		int k = 0;
		while(k < columnNames.size()-1){
			insertString.append(it.next()+ ", ");
			k++;
		}
		insertString.append(it.next());
		insertString.append(") VALUES (");
		for(int i = 0; i < columnNames.size(); i++) {
			insertString.append(" ? ");
			if (i != columnNames.size()-1) {
				insertString.append(",");
			}
		}
		insertString.append(")");
		int j = 1;
		if (keysLength != 0) {
			insertString.append(" ON DUPLICATE KEY UPDATE ");
			for(String colName : columnNames){
				if (addOnDuplicate) {
					insertString.append(colName + "= "+ colName +" + values(" + colName + ")");

				} else {
					insertString.append(colName + "=values(" + colName + ")");
				}
				if (j<columnNames.size()) {
					insertString.append(",");
					j++;
				}
			}
		}
		return insertString.toString();
	}
	
}
