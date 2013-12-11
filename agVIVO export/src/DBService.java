import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBService {

	//Configure this so you can connect to you database
	private static Connection conn = null;
	private static final String url = "jdbc:mysql://localhost:8889/";
	private static final String dbName = "The name of your database";
	private static final String driver = "com.mysql.jdbc.Driver";
	private static final String userName = "Your username for the database"; 
	private static final String password = "Your password for the database";

	 /**
     * create Database object
     */
    public DBService() {
    }

    public static Connection loadDriver() throws SQLException {
    	//We create the connection to the database
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
        conn = DriverManager.getConnection(url+dbName, userName, password);
        System.out.println("Connection to database");
        return conn;
    }

    //The following queries are used for our example case. Users interested in adding other information
    //or using other databases need to implement their own queries.
    
    public static ResultSet getTitles() throws SQLException {
    	//We obtain all the titles of the papers we want to add information from the database.
        Connection con = loadDriver();
        ResultSet rs;
        String query = "SELECT query FROM queries";
        PreparedStatement st = con.prepareStatement(query);
        rs = st.executeQuery();

        return rs;
    }
    
    public static ResultSet getData(String title) throws SQLException {
    	//We obtain the references we are adding to the paper
    	System.out.println("Title to retrieve: "+title);
        Connection con = loadDriver();
        ResultSet rs;
        String query = "SELECT DISTINCT resources.citation, resources.snippetTitle FROM queries, queries_result, resources " +
        		"WHERE queries.query=\""+title+"\" AND queries.idQUERIES=queries_result.idQUERIES " +
        				"AND queries_result.idRESOURCES=resources.idRESOURCES";
        PreparedStatement st = con.prepareStatement(query);
        rs = st.executeQuery();

        return rs;
    }

    public static void runQuery(String query) throws SQLException {
        Connection con = loadDriver();
        ResultSet rs;
        PreparedStatement st = con.prepareStatement(query);
        st.executeUpdate();
    }
}