package database;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;

import properties.SystemProperties;

/**
 * Class responsible for managing the connections of the associated connection
 * pool. It uses Apache Commons DBCP to manage the pooling and JDBC for connectivity.
 *
 */

public class DatabaseConnectionPool implements SystemProperties {

	private static DatabaseConnectionPool datapool; // create static reference
													// to the
													// DatabaseConnectionPool	
	
	private BasicDataSource datasource;	// connection pool

	/* private constructor so it can only be accessed from within the class */
	private DatabaseConnectionPool() {
		datasource = new BasicDataSource();

		/* set database connection pool properties */
		datasource.setDriverClassName(DRIVER);
		datasource.setUrl(DBURL);
		datasource.setUsername(DBUSER);
		datasource.setPassword(DBPASS);

		datasource.setInitialSize(20);
		datasource.setMaxTotal(150);
		datasource.setMaxIdle(30);
		datasource.setMaxConnLifetimeMillis(60000);
	}

	public static DatabaseConnectionPool getInstance() {
		if (datapool == null) {
			datapool = new DatabaseConnectionPool();
			return datapool; // get an instance of connection pool
		} else
			return datapool;
	}

	public Connection getConnection() throws SQLException {
		return this.datasource.getConnection(); // get a connection
	}

	public void closeDatapool() throws SQLException {
		if (datasource != null) {
			datasource.close(); // close the connection pool
		}
	}
}
