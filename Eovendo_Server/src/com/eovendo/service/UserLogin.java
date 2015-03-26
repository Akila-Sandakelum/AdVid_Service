package com.eovendo.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
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
 * Servlet implementation class UserLogin
 */
@WebServlet("/UserLogin")
public class UserLogin extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private Connection conn;
	private Statement stm;
	private PrintWriter out;
	
    public UserLogin() {
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
        
        /*BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String jsonString = "";
        jsonString = br.readLine();
        System.out.println(jsonString);
        
        JsonParser ulParser = new JsonParser();
        //System.out.println(jsonString);
        JsonObject ulReqJobj = (JsonObject)ulParser.parse(jsonString);// create json object from REQUEST
        System.out.println(" going to login ...");
        System.out.println(ulReqJobj.get("username")); 
        System.out.println(ulReqJobj.get("password")); 
        
        userLogin(ulReqJobj.get("username"),ulReqJobj.get("password"));*/
    	System.out.println(request.getParameter("username"));
    	System.out.println(request.getParameter("password"));
    	userLogin(request.getParameter("username"), request.getParameter("password"));
       
                      
       }
    
	private void userLogin(String username, String pass) {
		//System.out.println(name.getAsString());
		String query="SELECT user_password_hash FROM user WHERE user_name='"+username+"'";
		
		try {
					
			ResultSet rst=stm.executeQuery(query);
			
			if(rst.next()){
				if(rst.getString("user_password_hash").equals(pass)){
					System.out.println("Inside if");
					saveSession(username);
					
					
				}
			}
			System.out.println("outside if");
			System.out.println("dispatch to LoadVideoList");
			/*JsonObject result=new JsonObject();
	        result.addProperty("user_id", "100");
	        result.addProperty("session_id", "A0001");
	        System.out.println(result);
	        out.print(result);*/
	
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void saveSession(String name){
		
		String query1="SELECT user_id FROM user WHERE user_name='"+name+"'";
		String query2="INSERT INTO session_info (id,user_id,session_id,time,flag) VALUES(?,?,?,?,?)";
		int user_id;

				
		try {
			
			ResultSet rst=stm.executeQuery(query1);
			PreparedStatement pstm=conn.prepareStatement(query2);
			Date date=new Date();
			Timestamp timestamp=new Timestamp(date.getTime());
			
			if(rst.next()){
				user_id=rst.getInt("user_id");
				if(user_id!=-1){
					
					String session=user_id+name+date.getTime();
					System.out.println(session+" "+System.currentTimeMillis());
					String sesHash=md5Generater(session);
					
					pstm.setInt(1, 0);
					pstm.setInt(2, user_id);
					pstm.setString(3, sesHash);
					pstm.setTimestamp(4, timestamp);
					pstm.setBoolean(5, true);
					
					int res=pstm.executeUpdate();
					
					if(res==1){
						//make this {"user_id":"17","session_id":"2d147cf5f7f46f4799c8a789611e8c3b"}
						ResultSet rst2 = stm.executeQuery("SELECT session_id FROM session_info WHERE user_id='"+user_id+"'");
						//if(rst2.next()){
							System.out.println("inside res2");
							//System.out.println(rst2.getString("session_id")+" "+user_id);
							JsonObject result=new JsonObject();
					        result.addProperty("user_id", Integer.toString(user_id));
					        result.addProperty("session_id", sesHash);
					        System.out.println(result);
					        out.print(result);
						//}
						
					}
					
					//System.out.println(res);
					
					
					
				}
			}
			//conn.commit();
			//conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	private void getSession(){
		
		
		
	}
	
	private String md5Generater(String session){
		
		StringBuffer hexString = new StringBuffer();
		
		try {
			
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(session.getBytes());
			
			byte byteData[] = md.digest();
					
			
	    	for (int i=0;i<byteData.length;i++) {
	    		String hex=Integer.toHexString(0xff & byteData[i]);
	   	     	if(hex.length()==1) hexString.append('0');
	   	     	hexString.append(hex);
	    	}
			
	    	System.out.println("Digest(in hex format):: " + hexString.toString());
	    	
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return hexString.toString();
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
/*
Connection con=DBConnector.getdbConnection();
        //String query="INSERT INTO users (user_name,user_email,user_password_hash) VALUES(?,?,?)" ;
        String query="SELECT * FROM users";
        PreparedStatement stm=con.prepareStatement(query);

        /*stm.setString(1,"akila");
        stm.setString(2,"sand.akgl");
        stm.setString(3,"$2y$10$CstWABtMz/Vw0C2m04BNruwPuzPVjghjjkDXyxIsuL3xK/IbP156s");
       
        ResultSet set=stm.executeQuery(query);

        while(set.next()){
        	out.println(set.getString("user_name")+"</br>");
        
        //int result= stm.executeUpdate();

        //out.println(result);
*/