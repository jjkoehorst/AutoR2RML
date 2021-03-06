package nl.unimaas.ids.autorml.mappers;

import java.io.PrintStream;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RDBMSMapper extends AbstractMapper implements MapperInterface {

	public RDBMSMapper(String jdbcUrl, String userName, String passWord, String baseUri, String graphUri) throws SQLException, ClassNotFoundException {
		super(jdbcUrl, userName, passWord, baseUri, graphUri);
		Class.forName("org.sqlite.JDBC");
		Class.forName("org.postgresql.Driver");
		Class.forName("org.mariadb.jdbc.Driver");
		connection = DriverManager.getConnection(jdbcUrl, userName, passWord);
	}


	@Override
	public void generateMapping(PrintStream out, boolean recursive, String baseDir) throws Exception {
		DatabaseMetaData md = connection.getMetaData();
		ResultSet rs = md.getTables(null, null, "%", new String[] { "TABLE" });
		
		generateNamespaces(out);
		while (rs.next()) {
		  String table = rs.getString(3);
		  
		  // Maybe add schema pattern here?
		  ResultSet rs2 = md.getColumns(null, null, table, null);
		  List<String> columns = new ArrayList<>();
		  while(rs2.next())
			  columns.add(rs2.getString(4));
		  
		  if (rs.getString(2) != null) {
			  // If schema name returned then we use it for postgresql
			  table = rs.getString(2) + "." + table; 
		  }
		  String[] col = (String[]) columns.toArray(new String[0]);
		  generateMappingForTable(table, col, out, table);
		}
		
	}
	
	@Override
	public String getColumnName(String column) {
		return column;
	}
	
 	@Override
	public String getSqlForRowNum() {
 		// this is SQL standard, and needs to be overridden for databases that don't support this standard
		return "row_number() over () as " + ROW_NUM_NAME;
	}
 	
 	@Override
	public String getSqlForColumn(String column, int index) {
		return column;
	}

}
