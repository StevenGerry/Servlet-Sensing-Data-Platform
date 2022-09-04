import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
 
public class ReceivePassengerData extends HttpServlet {
	/**
	 * Get data from the edge devices by POSTJSON
	 */
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		//JSON Format
		request.setCharacterEncoding("UTF-8");
	    response.setContentType("text/html;charset=UTF-8");
	    String acceptjson = "";
	    //SQL Connection
	    String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
		String dbURL = "jdbc:sqlserver://192.168.3.102:1433;DatabaseName=KANACHU_TEST";
		String userName = "sa";
		String userPwd = "p#uK2eW!";
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String time = new SimpleDateFormat("hh:mm:ss").format(new Date());
        String logAPI = "ReceivePassengerData";
        String log = date + " " + time + "-" + logAPI + "-";
		Connection dbConn = null;
		ResultSet rs = null;
		try
		{
			Class.forName(driverName);
			dbConn = DriverManager.getConnection(dbURL, userName, userPwd);
			System.out.println(log+"Database Connect Success!");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.print(log+"Err:Database Connect Failure with "+e.toString());

		}
	    try {
	        BufferedReader br = new BufferedReader(new InputStreamReader((ServletInputStream) request.getInputStream(), "utf-8"));
	        StringBuffer sb = new StringBuffer("");
	        String temp;
	        String uuid;
	        String timeline;
	        String passengers;
	        String busnumber;
	        String gps;
	        while ((temp= br.readLine()) != null) {
	            sb.append(temp);
	        }
	        br.close();
	        acceptjson = sb.toString();
	        System.out.println(log+"JSON Received");
	        JSONObject raw_Data = JSONObject.parseObject(acceptjson);
	        uuid = UUID.randomUUID().toString();
	        timeline = raw_Data.getString("DATETIME");
	        busnumber = raw_Data.getString("BUS_NUMBER");
	        passengers = raw_Data.getString("PASSENGER_NUM");
	        gps = raw_Data.getString("GPS_LOCATION");
	        
	        String sql_updatepassengers = "INSERT into PASSENGER_DATA VALUES ('"+busnumber+"','"+passengers+"','"+timeline+"','"+uuid+"','"+gps+"')";
        	PreparedStatement stmt_updatepassengers = dbConn.prepareStatement(sql_updatepassengers);
        	stmt_updatepassengers.executeUpdate();
	        	
	        String jsonStr = "{\"status\":\"success\"}";
	        System.out.println(log+jsonStr);
	        response.getWriter().write(jsonStr);
	    } catch (Exception e) {
	    	String jsonStr = "{\"status\":\"error:"+e.toString()+"\"}";
	    	System.out.println(log+jsonStr);
	    	response.getWriter().write(jsonStr);
	        e.printStackTrace();
	    } finally{
	    	try {
				dbConn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	}
}