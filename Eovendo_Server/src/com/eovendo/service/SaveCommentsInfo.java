package com.eovendo.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.eovendo.service.dbconnector.DBConnector;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Servlet implementation class SaveCommentsInfo
 */
@WebServlet("/SaveCommentsInfo")
public class SaveCommentsInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private Connection conn;
	private Statement stm;
	private PrintWriter out;
	
	/**
     * @see HttpServlet#HttpServlet()
     */
	
	public SaveCommentsInfo() {
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
        System.out.println("Inside SaveCommentsInfo");
        
        JsonParser ulParser = new JsonParser();
        JsonObject ulReqJobj = (JsonObject)ulParser.parse(jsonString);
        String userId=ulReqJobj.get("user_id").getAsString();
        String sessionId=ulReqJobj.get("session_id").getAsString();
        String checkedCommnt=ulReqJobj.get("checkedCommnt").getAsString();
        String moreCommnt=ulReqJobj.get("moreCommnt").getAsString();
        String rate=ulReqJobj.get("rate").getAsString();
        String videoId=ulReqJobj.get("video_id").getAsString();
        
        System.out.println(ulReqJobj.get("user_id")); 
        System.out.println(ulReqJobj.get("session_id")); 
        System.out.println(ulReqJobj.get("checkedCommnt"));
        System.out.println(ulReqJobj.get("moreCommnt"));
        System.out.println(ulReqJobj.get("rate"));
        System.out.println(ulReqJobj.get("video_id"));
        
        int result=checkSession(userId,sessionId);
        if(result==1){
        	saveComments(videoId,userId,checkedCommnt,moreCommnt,rate);
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
	
	private void saveComments(String videoId,String userId,String checkedCommnt,String moreCommnt,String rating){
		
		String query="INSERT INTO transaction_details (Transaction_ID,Video_ID,User_ID,date_time,Comment,Rate,more_comments) VALUES(?,?,?,?,?,?,?)";
		String query1="UPDATE user_video SET wached='1',commented='1' WHERE user_id='"+Integer.parseInt(userId)+"' AND video_id='"+Integer.parseInt(videoId)+"'";
		try {
			
			PreparedStatement pstm=conn.prepareStatement(query);
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date();
			System.out.println(dateFormat.format(date));
			
			pstm.setInt(1, 0);
			pstm.setInt(2, Integer.parseInt(videoId));
			pstm.setInt(3, Integer.parseInt(userId));
			pstm.setString(4, dateFormat.format(date));
			pstm.setString(5,checkedCommnt );
			pstm.setString(6, rating);
			pstm.setString(7, moreCommnt);
			
			int res=pstm.executeUpdate();
			
			stm=conn.createStatement();
			int res1=stm.executeUpdate(query1);
			System.out.println(res+" "+res1);
			
			if(res==1 && res1==1){								
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
