package com.eovendo.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.eovendo.service.dbconnector.CheckSession;
import com.eovendo.service.dbconnector.DBConnector;
import com.eovendo.service.entity.UserVideoHistory;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Servlet implementation class LoadHistoryInfo
 */
@WebServlet("/LoadHistoryInfo")
public class LoadHistoryInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	private Connection conn;
	private Statement stm;
	private PrintWriter out;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoadHistoryInfo() {
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
        System.out.println("Inside LoadHistoryInfo servlet........");
        
        JsonParser ulParser = new JsonParser();
        JsonObject ulReqJobj = (JsonObject)ulParser.parse(jsonString);
        String userId=ulReqJobj.get("user_id").getAsString();
        String sessionId=ulReqJobj.get("session_id").getAsString();
        System.out.println(ulReqJobj.get("user_id")); 
        System.out.println(ulReqJobj.get("session_id")); 

        int res=CheckSession.checkCurrentSession(userId,sessionId);
        
        if(res==1){
        	 System.out.println("Befor calling loadHistoryInfo........");
        	
        	loadHistoryInfo(userId);

        }
      
    }
    
    private void loadHistoryInfo(String userId){
    	
    	ResultSet rst1=null;
    	
    	String query="SELECT title,path,video_id FROM video WHERE video_id IN (SELECT video_id FROM user_video WHERE user_id='"+userId+"' AND commented='1' )";                     
    	ArrayList<UserVideoHistory> historyList=new ArrayList<UserVideoHistory>();
    	
    	System.out.println("Inside loadHistoryInfo......");
    	
		try {
			
			stm = conn.createStatement();
			ResultSet rst=stm.executeQuery(query);
			
			while(rst.next()){
				UserVideoHistory videoHist=new UserVideoHistory();
				videoHist.setTitle(rst.getString("title"));		
				videoHist.setVideoId(rst.getString("video_id"));
				videoHist.setVideoPath(rst.getString("path"));
				
				System.out.print(rst.getString("title")+" "+rst.getString("video_id")+" "+rst.getString("path")+" ");
				
				String query1="SELECT date_time FROM transaction_details WHERE Video_ID='"+rst.getString("video_id")+"'";
				
				Statement stm1=conn.createStatement();
				rst1=stm1.executeQuery(query1);
				
				if(rst1.next()){
					Date date=rst1.getDate("date_time");
					System.out.println("Date::::"+date);
					long period=calculatePeriod(date);
					System.out.println("Period::::"+period);
					videoHist.setPeriod(Long.toString(period)+" "+"days ago");
				}
				
				historyList.add(videoHist);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Gson gson=new Gson();
		String historylist=gson.toJson(historyList);
		System.out.println(historylist);
		out.print(historylist);
    }
    
    private long calculatePeriod(Date date){
    	
    	Date oldDate=null;
    	Date currntDate=null;
    	
    	
    	long diffDays=0;
    	
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
    	try {
    		
    		oldDate=date;
    		Date today = dateFormat.parse(dateFormat.format(new Date()));
			
			System.out.println("CurrntDate:::::"+today);
			long diff = today.getTime() - oldDate.getTime();
			
			diffDays = diff / (24 * 60 * 60 * 1000);
			
		} catch (ParseException e) {
		
			e.printStackTrace();
		}
		return diffDays;
    	
    	
    	
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
