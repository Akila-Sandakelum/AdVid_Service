package com.eovendo.service.dbconnector;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CheckSession {
	
	private static Connection conn;
	private static Statement stm;
	
	public static int checkCurrentSession(String userId,String sessionId) throws ClassNotFoundException, SQLException{
		
		conn=DBConnector.getdbConnection();
		stm=conn.createStatement();
		
		String query="SELECT session_id FROM session_info WHERE user_id='"+userId+"' ORDER BY id DESC LIMIT 1";
		System.out.println("Inside checksession");
		try {
			
			ResultSet rst=stm.executeQuery(query);
			
			if(rst.next()){
				
				//System.out.println("In");
				
				if(rst.getString("session_id").equals(sessionId)){
					
					return 1;
					
				}
			}
			
	
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return 0;
		
	}

}
