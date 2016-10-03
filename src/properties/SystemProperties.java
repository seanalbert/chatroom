package properties;

/**
 * Interface to define system variables
 */

public interface SystemProperties {

	//public static final String URL = "https://192.168.1.100:8443/WebChatroom/"; // work
	public static final String URL = "https://localhost:8443/WebChatroom/";	// home
	//public static final String URL = "https://smoran.it.nuigalway.ie:8443/WebChatroom/";	// college
	
	public static final String DRIVER = "com.mysql.jdbc.Driver";
	public static final String DBURL = "jdbc:mysql://localhost:3306/tomcat_realm";
	public static final String DBUSER = "realm_access";
	public static final String DBPASS = "realmpass";
	
//	public static final String DBURL = "jdbc:mysql://danu6.it.nuigalway.ie:3306/mydb2114";
//	public static final String DBUSER = "mydb2114ms";
//	public static final String DBPASS = "cu2nip";
}
