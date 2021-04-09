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
 
public class SearchBusServlet extends HttpServlet {
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
		String dbURL = "jdbc:sqlserver://192.168.3.102:1433;DatabaseName=***";
		String userName = "sa";
		String userPwd = "******";
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String time = new SimpleDateFormat("hh:mm:ss").format(new Date());
        String logAPI = "SearchBusServlet";
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
	        String bus_id = raw_JsonData.getString("search");
	        String businfo = "";
			try {
				String sql_bus = "select businfo from data_bus where busnumber = '"+bus_id+"'";
				ResultSet rs_bus = null;
				PreparedStatement stmt_bus = null;
				stmt_bus = dbConn.prepareStatement(sql_bus);
				rs_bus = stmt_bus.executeQuery();
				while(rs_bus.next()) {
					businfo = rs_bus.getString("businfo");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			JSONObject result = new JSONObject();  
	        result.put("success", true);  
	        result.put("businfo", businfo); 
			
			//String sql = "select TOP(20) data_buspassenger.uuid,data_buspassenger.busnumber,data_buspassenger.timeline,sensorid,sensignal,senbattery,sentemp,senhumi,senco2,data_buspassenger.passenger from data_buspassenger,data_co2sensors where sensorid in (select sensnumber from data_bus where busnumber = '"+bus_id+"') and data_buspassenger.uuid=data_co2sensors.uuid order by timeline DESC ";
	        String sql = "select c.busnumber,b.*,c.passenger from (SELECT sensorid, max(timeline) as timeline from data_co2sensors where sensorid in (select sensnumber from data_bus where busnumber = '"+bus_id+"') group by sensorid) a, data_co2sensors b,data_buspassenger c where a.sensorid = b.sensorid and a.timeline = b.timeline and c.uuid = b.uuid order by timeline DESC";
	        JSONArray jsonArray = new JSONArray();
	        try {
				ResultSet rs = null;
				PreparedStatement stmt = null;
				stmt = dbConn.prepareStatement(sql);
				rs = stmt.executeQuery();
				int i=0;
				while(rs.next()) {
					 JSONObject sensor = new JSONObject();  
					 sensor.put("uuid", rs.getString("uuid")); 
					 sensor.put("busnumber", rs.getString("busnumber"));  
					 sensor.put("timeline", rs.getString("timeline"));  
				     sensor.put("sensorid", rs.getString("sensorid"));
				     sensor.put("sensignal", rs.getString("sensignal"));
				     sensor.put("senbattery", rs.getString("senbattery"));
				     sensor.put("sentemp", rs.getString("sentemp"));
				     sensor.put("senhumi", rs.getString("senhumi"));
				     sensor.put("senco2", rs.getString("senco2"));
				     sensor.put("passenger", rs.getString("passenger"));
				     jsonArray.add(i++, sensor);
				     //System.out.println(log+""+i+""+jsonArray.toString());	
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
	        
	        result.put("data", jsonArray);
	        
	        String Message = result.toJSONString();
	        String jsonStr = "{\"status\":\"success\"}";
	        System.out.println(log+"Search_POST:"+bus_id+", RETURN:"+jsonStr);
	        response.getWriter().write(Message);
	    } catch (Exception e) {
	    	String jsonStr = "{\"status\":\"error\"}";
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
