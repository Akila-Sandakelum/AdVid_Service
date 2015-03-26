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

import com.eovendo.service.dbconnector.CheckSession;
import com.eovendo.service.dbconnector.DBConnector;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Servlet implementation class LoadCommentsInfo
 */
@WebServlet("/LoadCommentsInfo")
public class LoadCommentsInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private Connection conn;
	private Statement stm;
	private PrintWriter out;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoadCommentsInfo() {
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
        System.out.println("Inside LoadCommentsInfo servlet........");
    
        JsonParser ulParser = new JsonParser();
        JsonObject ulReqJobj = (JsonObject)ulParser.parse(jsonString);
        String userId=ulReqJobj.get("user_id").getAsString();
        String sessionId=ulReqJobj.get("session_id").getAsString();
        String videoId=ulReqJobj.get("video_id").getAsString();
        System.out.println(ulReqJobj.get("user_id")); 
        System.out.println(ulReqJobj.get("session_id")); 
        
        int res=CheckSession.checkCurrentSession(userId,sessionId);
        
        if(res==1){
        	 System.out.println("Befor calling loadcommentsInfo........");
        	
        	loadCommentInfo(userId,videoId);

        }
    
    }
    
    private void loadCommentInfo(String userId, String videoId){
    	
    	String query="SELECT Rate,Comment,more_comments FROM transaction_details WHERE User_ID='"+userId+"' AND Video_ID='"+videoId+"'";
    	System.out.println("Inside loadCommentInfo ........");
    	
    	try {
    		
			ResultSet rst=stm.executeQuery(query);
			JsonObject job=new JsonObject();
			if(rst.next()){
				
				job.addProperty("rate", rst.getString("Rate"));
				job.addProperty("chk_comment", rst.getString("Comment"));
				job.addProperty("comment", rst.getString("more_comments"));
				System.out.println(job);
			    out.print(job);
				
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
