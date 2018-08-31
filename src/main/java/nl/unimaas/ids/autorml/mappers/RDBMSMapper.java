package nl.unimaas.ids.autorml.mappers;

import java.io.PrintStream;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RDBMSMapper extends AbstractMapper implements MapperInterface {

	public RDBMSMapper(String jdbcUrl, String username, String password, String outputGraph, String baseUri) throws SQLException, ClassNotFoundException {
		Class.forName("org.sqlite.JDBC");
		Class.forName("org.postgresql.Driver");
		connection = DriverManager.getConnection(jdbcUrl, username, password);
		if (this.outputGraph == null)
			this.outputGraph = "http://kraken/graph/default";
		else
			this.outputGraph = outputGraph;
		this.baseUri = baseUri;
	}


	@Override
	public void generateMapping(PrintStream out, boolean recursive, String baseDir) throws Exception {
		DatabaseMetaData md = connection.getMetaData();
		ResultSet rs = md.getTables(null, null, "%", new String[] { "TABLE" });
		while (rs.next()) {
		  String table = rs.getString(3);
		  ResultSet rs2 = md.getColumns(null, null, table, null);
		  List<String> columns = new ArrayList<>();
		  while(rs2.next())
			  columns.add(rs2.getString(4));
		  
		  String[] col = (String[]) columns.toArray(new String[0]);
		  generateMapping(table, col, System.out, "Mapping");
		  
		}
		
	}

}