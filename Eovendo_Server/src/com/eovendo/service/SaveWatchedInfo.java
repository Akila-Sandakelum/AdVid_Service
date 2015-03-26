package com.eovendo.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.eovendo.service.dbconnector.DBConnector;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Servlet implementation class SaveWatchedInfo
 */
@WebServlet("/SaveWatchedInfo")
public class SaveWatchedInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	
	private Connection conn;
	private Statement stm;
	private PrintWriter out;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SaveWatchedInfo() {
        super();
        try {
        	
			conn=DBConnector.getdbConnection();
			stm=conn.createStatement();
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException, ClassNotFoundException {
        response.setContentType("text/html;charset=UTF-8");
        
        out=response.getWriter();
        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String jsonString = "";
        jsonString = br.readLine();
        System.out.println("Inside SaveWatchedInfo");
        
        JsonParser ulParser = new JsonParser();
        JsonObject ulReqJobj = (JsonObject)ulParser.parse(jsonString);
        String userId=ulReqJobj.get("user_id").getAsString();
        String sessionId=ulReqJobj.get("session_id").getAsString();
        String videoId=ulReqJobj.get("video_id").getAsString();
        
        int result=checkSession(userId,sessionId);
        if(result==1){
        	saveWatchedVds(videoId,userId);
        }
    }
    
    private int checkSession(String userId, String sessionId) {
		
		String query="SELECT session_id FROM session_info WHERE user_id='"+userId+"' ORDER BY id DESC LIMIT 1";
		System.out.println("Inside checksession");
		try {
			
			ResultSet rst=stm.executeQuery(query);
			
			if(rst.next()){
				
				System.out.println("Before loadVideoList after rst.next()"+rst.getString("session_id"));
				
				if(rst.getString("session_id").equals(sessionId)){
					System.out.println("Before loadVideoList");
					//saveComments(userId.getAsString(),checkedCommnt.getAsString(),moreCommnt.getAsString());
					return 1;
				}
			}
		
	
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
		
	}
    
    private void saveWatchedVds(String videoId,String userId){
    	String query1="UPDATE user_video SET wached='1',commented='0' WHERE user_id='"+Integer.parseInt(userId)+"' AND video_id='"+Integer.parseInt(videoId)+"'";
    	try {
    		
    		
			stm=conn.createStatement();
			int res=stm.executeUpdate(query1);
			
			if(res==1){								
				JsonObject result=new JsonObject();
		        result.addProperty("result", 1);			        		       	       
		        out.print(result);
			}else{
				JsonObject result=new JsonObject();
		        result.addProperty("result", -1);
		        out.print(result);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    }

    
    /**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			processRequest(request,response);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			processRequest(request,response);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
