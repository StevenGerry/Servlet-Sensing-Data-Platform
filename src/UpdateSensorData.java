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
 
public class UpdateSensorData extends HttpServlet {
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
        String logAPI = "UpdateSensorData";
        String log = date + " " + time + "-" + logAPI + "-";
		Connection dbConn = null;
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
	        String sensorid;
	        String busnumber;
	        String info;
	        while ((temp = br.readLine()) != null) {
	            sb.append(temp);
	        }
	        br.close();
	        acceptjson = sb.toString();
	        //System.out.println(log+acceptjson);
	        JSONObject raw_JsonData = JSONObject.parseObject(acceptjson);
	        	JSONObject JSenseDataObject = raw_JsonData;
	        	sensorid = JSenseDataObject.getString("SENSOR_ID");
	        	busnumber = JSenseDataObject.getString("BUS_NUMBER");
	        	info = JSenseDataObject.getString("INFO");
	        	
	        	String sql_update = "UPDATE BUS_DATA SET BUS_NUMBER = '"+ busnumber +"', INFO = '"+ info +"' WHERE SENSOR_ID = '"+ sensorid +"'";
	        	//System.out.println(log+sql_co2);
	        	PreparedStatement stmt_update = dbConn.prepareStatement(sql_update);
	        	stmt_update.executeUpdate();
	        	
	        String jsonStr = "{\"status\":\"success\"}";
	        System.out.println(log+jsonStr+sensorid+"-Updated!");
	        response.getWriter().write(jsonStr);
	    } catch (Exception e) {
	    	String jsonStr = "{\"status\":\"error:"+e.toString()+"\"}";
	    	System.out.println(log+jsonStr+"-Updated ERROR");
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