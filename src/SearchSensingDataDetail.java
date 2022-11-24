import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.UUID;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
 
public class SearchSensingDataDetail extends HttpServlet {
	/**
	 * Do the request of search from web page
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
        String logAPI = "SearchSensingData";
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
	        while ((temp = br.readLine()) != null) {
	            sb.append(temp);
	        }
	        br.close();
	        acceptjson = sb.toString();
	        JSONObject raw_JsonData = JSONObject.parseObject(acceptjson);
	        String sensor_id = raw_JsonData.getString("IDENTIFIER");
	        String datetime = raw_JsonData.getString("DATETIME");
			
			JSONObject result = new JSONObject();  
	        result.put("success", true);  
			
	        //SELECT TOP(50) b.* FROM (SELECT IDENTIFIER, MAX(DATETIME) AS DATETIME FROM SENSOR_DATA WHERE IDENTIFIER IN (SELECT IDENTIFIER FROM BUS_DATA WHERE BUS_NUMBER = '"+bus_id+"') GROUP BY IDENTIFIER) a, SENSOR_DATA b WHERE a.IDENTIFIER = b.IDENTIFIER AND a.DATETIME = b.DATETIME ORDER BY b.DATETIME DESC
			String sql = "SELECT * FROM [dbo].[SENSOR_DATA] WHERE IDENTIFIER = '" + sensor_id + "' AND DATETIME LIKE '" + datetime + "%' ORDER BY DATETIME DESC";
			JSONArray jsonArray = new JSONArray();
	        try {
				ResultSet rs = null;
				PreparedStatement stmt = null;
				stmt = dbConn.prepareStatement(sql);
				rs = stmt.executeQuery();
				int i=0;
				while(rs.next()) {
					 JSONObject sensor = new JSONObject();  
					 sensor.put("UUID", rs.getString("UUID")); 
					 sensor.put("BUS_NUMBER", rs.getString("BUS_NUMBER"));  
					 sensor.put("DATETIME", rs.getString("DATETIME"));  
				     sensor.put("IDENTIFIER", rs.getString("IDENTIFIER"));
				     sensor.put("SENSOR_STATUS", rs.getString("SENSOR_STATUS"));
				     sensor.put("TEMPERATURE", rs.getString("TEMPERATURE"));
				     sensor.put("HUMIDITY", rs.getString("HUMIDITY"));
				     sensor.put("CO2", rs.getString("CO2"));
				     jsonArray.add(i++, sensor);
				     //System.out.println(log+""+i+""+jsonArray.toString());	
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
	        
	        result.put("data", jsonArray);
	        
	        String Message = result.toJSONString();
	        String jsonStr = "{\"status\":\"success\"}";
	        System.out.println(log+"RETURN:"+jsonStr);
	        response.getWriter().write(Message);
	    } catch (Exception e) {
	    	String jsonStr = "{\"status\":\"error:"+e.toString()+"\"}";
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
