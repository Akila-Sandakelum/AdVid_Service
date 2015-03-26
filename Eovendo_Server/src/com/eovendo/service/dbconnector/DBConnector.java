package com.eovendo.service.dbconnector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnector {
	
	private Connection conn;
    private static DBConnector dbconnector;

    private DBConnector() throws ClassNotFoundException, SQLException{

        Class.forName("com.mysql.jdbc.Driver");
        conn=DriverManager.getConnection("jdbc:mysql://localhost:3306/eovendo","root","");

    }

    private  Connection getCon(){
    return conn;
    }
    private static DBConnector getdbconnector() throws ClassNotFoundException, SQLException{
        if(dbconnector==null){
        dbconnector=new DBConnector();
        }

        return dbconnector;
    }

    public static Connection getdbConnection() throws ClassNotFoundException, SQLException{
    return  getdbconnector().getCon();
    }
	
	
	
}
