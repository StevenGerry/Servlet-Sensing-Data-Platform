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
 
public class DataServlet extends HttpServlet {
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
	        String uuid[] = new String[100];
	        String timeline[] = new String[100];
	        String sensorid[] = new String[100];
	        String sensignal[] = new String[100];
	        String senbattery[] = new String[100];
	        String sentemp[] = new String[100];
	        String senhumi[] = new String[100];
	        String senco2[] = new String[100];
	        String passengers[] = new String[100];
	        String busnumber[] = new String[100];
	        while ((temp = br.readLine()) != null) {
	            sb.append(temp);
	        }
	        br.close();
	        acceptjson = sb.toString();
	        JSONObject raw_JsonData = JSONObject.parseObject(acceptjson);
	        JSONArray JSensorDataArray = raw_JsonData.getJSONArray("sensor");
	        for(int i=0;i<JSensorDataArray.size();i++) {
	        	JSONObject JSenseDataObject = null;
	        	JSenseDataObject = JSensorDataArray.getJSONObject(i);
	        	uuid[i] = UUID.randomUUID().toString();
	        	timeline[i] = JSenseDataObject.getString("timeline");
	        	sensorid[i] = JSenseDataObject.getString("sensorid");
	        	sensignal[i] = JSenseDataObject.getString("sensignal");
	        	senbattery[i] = JSenseDataObject.getString("senbattery");
	        	sentemp[i] = JSenseDataObject.getString("sentemp");
	        	senhumi[i] = JSenseDataObject.getString("senhumi");
	        	senco2[i] = JSenseDataObject.getString("senco2");
	        	passengers[i] = JSenseDataObject.getString("passengers");
	        	busnumber[i] = JSenseDataObject.getString("busnumber");
	        	String sql_co2 = "insert into data_co2sensors (uuid,timeline,sensorid,sensignal,senbattery,sentemp,senhumi,senco2) values ('"+uuid[i]+"','"+timeline[i]+"','"+sensorid[i]+"','"+sensignal[i]+"','"+senbattery[i]+"','"+sentemp[i]+"','"+senhumi[i]+"','"+senco2[i]+"')";
	        	//System.out.println(log+sql_co2);
	        	PreparedStatement stmt_co2 = dbConn.prepareStatement(sql_co2);
	        	stmt_co2.executeUpdate();
	        	String sql_passenger = "insert into data_buspassenger (uuid,timeline,busnumber,passenger) "
	        			+ "values ('"+uuid[i]+"','"+timeline[i]+"','"+busnumber[i]+"','"+passengers[i]+"')";
	        	//System.out.println(log+sql_passenger);
	        	PreparedStatement stmt_passenger = dbConn.prepareStatement(sql_passenger);
	        	stmt_passenger.executeUpdate();
	        }
	        String jsonStr = "{\"status\":\"success\"}";
	        response.getWriter().write(jsonStr);
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