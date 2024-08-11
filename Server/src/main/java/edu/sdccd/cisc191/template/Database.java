package edu.sdccd.cisc191.template;
import java.sql.*;

public class Database {
    //establish connection to postgresql using specific url, port, and db name
    private static final String url = "jdbc:postgresql://localhost:3030/StudyQuestions";
    //credentials used to pass authorization and connect
    private static final String username = "postgres";
    private static final String password = "0000";

    //method that returns the connection to db
    public static Connection getConnection () throws SQLException {

        return DriverManager.getConnection(url, username, password);

    }

}
