package com.database;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import com.google.gson.JsonObject;

import oracle.sql.CLOB;

public class DatabaseUtil {
	static Statement stmtObj;
	static Connection connObj;
	static ResultSet resultSetObj;

	public static String dataSourceName;
	public static String jdbcDriver;

	public DatabaseUtil() {
	}

	public static void main(String[] args) {
//		dataSourceName = "jdbc/los";
//		jdbcDriver = "oracle.jdbc.driver.OracleDriver";
//
//		CallProcedure("SMB_CREDIT_PROCESS.INSERT_SMB_INTERATION_LOG",
//				"SMB2010021996980_03|GetDetail|{}|[]||00|Success");

//		testInsertInstance();
		testInsertInstanceReader();
	}

	@SuppressWarnings("finally")
	public static JsonObject CallProcedure(String procedureName, String value) {
		JsonObject result = new JsonObject();

		dataSourceName = "jdbc/los";
		jdbcDriver = "oracle.jdbc.driver.OracleDriver";

		try {
			// Step 1 - Register Oracle JDBC Driver (Though This Is No Longer
			// Required Since JDBC 4.0, But Added Here For Backward
			// Compatibility!
			Class.forName(jdbcDriver);

			// Step 2 - Creating Oracle Connection Object
			// Context ctx = new InitialContext();
			// DataSource dataSource = (DataSource) ctx.lookup(dataSourceName);
			// connObj = dataSource.getConnection();

			connObj = DriverManager.getConnection("jdbc:oracle:thin:@10.0.18.116:1521:orcl", "SAALOP_LOG", "Hpt123456");

			if (connObj != null) {
				System.out.println(String.format("%s - %s - %s", "DB", "INFO", "!! Connected With Oracle Database !!"));
				System.out.println(String.format("%s - %s - %s", "DB", "INFO Conn", connObj.getMetaData().getURL()));
				System.out
						.println(String.format("%s - %s - %s", "DB", "INFO Conn", connObj.getMetaData().getUserName()));
			}

			resultSetObj = Execute(connObj, procedureName, value);
			result = MappingData(resultSetObj);

		} catch (Exception sqlException) {
			sqlException.printStackTrace();
//			System.out.println(String.format("%s - %s - %s", "DB", "ERROR",
//					sqlException.toString()));
		} finally {
			try {
				if (resultSetObj != null) {
					resultSetObj.close();
				}
				if (stmtObj != null) {
					stmtObj.close();
				}
				if (connObj != null) {
					connObj.close();
				}
			} catch (final Exception sqlException) {
				sqlException.printStackTrace();
			}

			return result;
		}
	}
	
	@SuppressWarnings("finally")
	public static JsonObject CallProcedure(String procedureName, Reader value) {
		JsonObject result = new JsonObject();

		dataSourceName = "jdbc/los";
		jdbcDriver = "oracle.jdbc.driver.OracleDriver";

		try {
			// Step 1 - Register Oracle JDBC Driver (Though This Is No Longer
			// Required Since JDBC 4.0, But Added Here For Backward
			// Compatibility!
			Class.forName(jdbcDriver);

			// Step 2 - Creating Oracle Connection Object
			// Context ctx = new InitialContext();
			// DataSource dataSource = (DataSource) ctx.lookup(dataSourceName);
			// connObj = dataSource.getConnection();

			connObj = DriverManager.getConnection("jdbc:oracle:thin:@10.0.18.116:1521:orcl", "SAALOP_LOG", "Hpt123456");

			if (connObj != null) {
				System.out.println(String.format("%s - %s - %s", "DB", "INFO", "!! Connected With Oracle Database !!"));
				System.out.println(String.format("%s - %s - %s", "DB", "INFO Conn", connObj.getMetaData().getURL()));
				System.out
						.println(String.format("%s - %s - %s", "DB", "INFO Conn", connObj.getMetaData().getUserName()));
			}

			resultSetObj = Execute(connObj, procedureName, value);
			result = MappingData(resultSetObj);

		} catch (Exception sqlException) {
			sqlException.printStackTrace();
//			System.out.println(String.format("%s - %s - %s", "DB", "ERROR",
//					sqlException.toString()));
		} finally {
			try {
				if (resultSetObj != null) {
					resultSetObj.close();
				}
				if (stmtObj != null) {
					stmtObj.close();
				}
				if (connObj != null) {
					connObj.close();
				}
			} catch (final Exception sqlException) {
				sqlException.printStackTrace();
			}

			return result;
		}
	}

	public static ResultSet Execute(Connection connObj, String procedureName, String value) {
		ResultSet resultSet = null;

		if (value == null || value.equals("")) {
			return null;
		}

		try {
			String[] value_split = value.split("\\#");
			String var = "";
			for (int i = 0; i < value_split.length; i++)
				var += "?,";

			String sql = "{call " + procedureName + " (" + var + "?)}";
			CallableStatement call = connObj.prepareCall(sql);

			int count = 1;
			String log = "";
			for (int i = 0; i < value_split.length; i++) {

//				call.setString
//				
//				Clob clob = CLOB.createTemporary(connObj, false, oracle.sql.CLOB.DURATION_SESSION);
//				clob.setString(value_split[i]);
//				call.setClob(i, clob);
				call.setClob(count, new StringReader(value_split[i]));
				log += count + "_" + value_split[i] + "::";
				count++;
			}
			call.registerOutParameter(count, oracle.jdbc.OracleTypes.CURSOR);
//			log += count + "_OracleTypes.CURSOR";

			System.out.println(String.format("%s - %s - %s", "DB_Execute", "INFO", sql));
//			System.out.println(String.format("%s - %s - %s", "DB_Execute", "INFO", log));

			call.execute();
			resultSet = (ResultSet) call.getObject(count);

		} catch (Exception sqlException) {
			sqlException.printStackTrace();
//			System.out.println(String.format("%s - %s - %s", "DB_Execute",
//					"ERROR", sqlException.toString()));
		}

		return resultSet;
	}
	
	public static ResultSet Execute(Connection connObj, String procedureName, Reader value) {
		ResultSet resultSet = null;

		if (value == null || value.equals("")) {
			return null;
		}

		try {
			
			String sql = "{call " + procedureName + " (?,?)}";
			CallableStatement call = connObj.prepareCall(sql);

			int count = 1;
			String log = "";
			
			call.setClob(1, value);
			call.registerOutParameter(2, oracle.jdbc.OracleTypes.CURSOR);
//			log += count + "_OracleTypes.CURSOR";

			System.out.println(String.format("%s - %s - %s", "DB_Execute", "INFO", sql));
//			System.out.println(String.format("%s - %s - %s", "DB_Execute", "INFO", log));

			call.execute();
			resultSet = (ResultSet) call.getObject(2);

		} catch (Exception sqlException) {
			sqlException.printStackTrace();
//			System.out.println(String.format("%s - %s - %s", "DB_Execute",
//					"ERROR", sqlException.toString()));
		}

		return resultSet;
	}

	public static JsonObject MappingData(ResultSet resultSet) {

		JsonObject resultJsonObject = null;

		try {
			resultJsonObject = new JsonObject();

			while (resultSet.next()) {
				for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
					resultJsonObject.addProperty(resultSet.getMetaData().getColumnName(i + 1),
							resultSet.getString(i + 1));
				}
			}
		} catch (Exception sqlException) {
			System.out.println(String.format("%s - %s - %s", "DB_MappingData", "ERROR", sqlException.toString()));
		}
		return resultJsonObject;
	}

	public static void testInsertInstance() {

		String instanceInfo = "{\"state\":\"STATE_RUNNING\",\"piid\":\"1220\",\"processTemplateName\":\"LOS AA\",\"variables\":\"\"}";
		String sp = "PROCESS_INSTANCE.INSERT_BPM_INSTANCES";
		JsonObject result = CallProcedure(sp, instanceInfo);
		System.out.println(result);
	}
	
	public static void testInsertInstanceReader() {

		String instanceInfo = "{\"state\":\"STATE_RUNNING\",\"piid\":\"1220\",\"processTemplateName\":\"LOS AA\",\"variables\":\"\"}";
		InputStream initialStream = new ByteArrayInputStream(instanceInfo.getBytes());
		Reader reader = new InputStreamReader(initialStream);
		String sp = "PROCESS_INSTANCE.INSERT_BPM_INSTANCES";
		JsonObject result = CallProcedure(sp, reader);
		System.out.println(result);
	}
}
