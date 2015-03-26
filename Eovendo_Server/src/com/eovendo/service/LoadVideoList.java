package com.eovendo.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.eovendo.service.dbconnector.DBConnector;
import com.eovendo.service.entity.UserVideo;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Servlet implementation class LoadVideoList
 */
@WebServlet("/LoadVideoList")
public class LoadVideoList extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection conn;
	private Statement stm;
	private PrintWriter out;   
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoadVideoList() {
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
        //System.out.println(jsonString);
        
        JsonParser ulParser = new JsonParser();
        JsonObject ulReqJobj = (JsonObject)ulParser.parse(jsonString);
        System.out.println(ulReqJobj.get("user_id")); 
        System.out.println(ulReqJobj.get("session_id")); 
        
        checkSession(ulReqJobj.get("user_id"),ulReqJobj.get("session_id"));
        
    }
    
	private void checkSession(JsonElement userId, JsonElement sessionId) {
		
		String query="SELECT session_id FROM session_info WHERE user_id='"+userId.getAsString()+"' ORDER BY id DESC LIMIT 1";
		System.out.println("Inside checksession");
		try {
			
			ResultSet rst=stm.executeQuery(query);
			
			if(rst.next()){
				
				System.out.println("Before loadVideoList after rst.next()"+rst.getString("session_id"));
				
				if(rst.getString("session_id").equals(sessionId.getAsString())){
					System.out.println("Before loadVideoList");
					loadVideolist(userId.getAsString());
					
				}
			}
			
	
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	private void loadVideolist(String userId){
		ResultSet rst1=null;
		 
		
		System.out.println("Inside loadVideolist"+userId);
		String query3="SELECT title,path,description,uploader_id,video_id FROM video WHERE video_id IN (SELECT video_id FROM user_video WHERE user_id='"+userId+"' AND commented='0' )";                     
		ArrayList<UserVideo> videolist=new ArrayList<UserVideo>(); 
		try {
			Statement stm1=conn.createStatement();
			ResultSet rst=stm1.executeQuery(query3);
			while(rst.next()){
				UserVideo video=new UserVideo();
				video.setTitle(rst.getString("title"));
				System.out.print(video.getTitle()+" ");
				video.setVideoPath(rst.getString("path"));
				System.out.print(video.getVideoPath()+" ");
				video.setDescription(rst.getString("description"));
				System.out.println(video.getDescription()+" ");
				video.setVideoId(rst.getString("video_id"));
				System.out.println(video.getVideoId()+" ");
				
				String query1="SELECT company_name FROM uploader_details WHERE uploader_id='"+rst.getString("uploader_id")+"'";
				rst1=stm.executeQuery(query1);
				if(rst1.next()){
					video.setAuthor(rst1.getString("company_name"));
					System.out.println(rst1.getString("company_name"));
				}
				videolist.add(video);
			}
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		
		Gson gson=new Gson();
		String videoList=gson.toJson(videolist);
		System.out.println(videoList);	
		out.print(videoList);
		
		
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
