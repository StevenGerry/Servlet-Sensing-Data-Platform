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
 
public class DataServletV2 extends HttpServlet {
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
		String dbURL = "jdbc:sqlserver://192.168.3.102:1433;DatabaseName=KANACHU";
		String userName = "sa";
		String userPwd = "p#uK2eW!";
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String time = new SimpleDateFormat("hh:mm:ss").format(new Date());
        String logAPI = "DataServlet";
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
	        String sensorid;
	        String sensignal;
	        String senbattery;
	        String sentemp;
	        String senhumi;
	        String senco2;
	        String passengers;
	        String busnumber;
	        while ((temp = br.readLine()) != null) {
	            sb.append(temp);
	        }
	        br.close();
	        acceptjson = sb.toString();
	        JSONObject raw_JsonData = JSONObject.parseObject(acceptjson);
	        JSONArray JSensorDataArray = raw_JsonData.getJSONArray("sensor");
	        	JSONObject JSenseDataObject = raw_JsonData;

	        	uuid = UUID.randomUUID().toString();
	        	timeline = JSenseDataObject.getString("DATE")+" "+JSenseDataObject.getString("TIME");
	            timeline = timeline.replaceAll("/", "-");
	        	sensorid = JSenseDataObject.getString("IDENTIFIER");
	        	sensignal = JSenseDataObject.getString("SENSOR_STATUS");
	        	senbattery = "nan";
	        	sentemp = JSenseDataObject.getString("TEMPERATURE1");
	        	senhumi = JSenseDataObject.getString("HUMIDITY1");
	        	senco2 = JSenseDataObject.getString("CO2");
	        	passengers = "0";
	        	busnumber = "SKK";
	        	String sql_confirm = "SELECT sensnumber FROM data_bus WHERE sensnumber = '"+sensorid+"'";
	        	PreparedStatement stmt_conf = dbConn.prepareStatement(sql_confirm);
	        	rs = stmt_conf.executeQuery();
	        	if (!rs.next())
	        	{
	        		String sql_upsensor = "INSERT into data_bus VALUES ('"+busnumber+"','"+sensorid+"','NEW')";
		        	PreparedStatement stmt_upsensor = dbConn.prepareStatement(sql_upsensor);
		        	stmt_upsensor.executeUpdate();
	        	}
	        	String sql_co2 = "insert into data_co2sensors (uuid,timeline,sensorid,sensignal,senbattery,sentemp,senhumi,senco2) values ('"+uuid+"','"+timeline+"','"+sensorid+"','"+sensignal+"','"+senbattery+"','"+sentemp+"','"+senhumi+"','"+senco2+"')";
	        	//System.out.println(log+sql_co2);
	        	PreparedStatement stmt_co2 = dbConn.prepareStatement(sql_co2);
	        	stmt_co2.executeUpdate();
	        	String sql_passenger = "insert into data_buspassenger (uuid,timeline,busnumber,passenger) "
	        			+ "values ('"+uuid+"','"+timeline+"','"+busnumber+"','"+passengers+"')";
	        	//System.out.println(log+sql_passenger);
	        	PreparedStatement stmt_passenger = dbConn.prepareStatement(sql_passenger);
	        	stmt_passenger.executeUpdate();
	        	
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