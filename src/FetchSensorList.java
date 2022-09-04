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
 
public class FetchSensorList extends HttpServlet {
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
        String logAPI = "FetchSensorList";
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
	        String received = raw_JsonData.getString("FINDTOREG");
	        int find_to_register = Integer.parseInt(received);
	        
	        JSONObject result = new JSONObject();  
	        result.put("success", true);
	        String sql="";
	        
	        if (find_to_register == 1)
	        {
	        	sql = "SELECT * FROM BUS_DATA WHERE BUS_NUMBER = 'NEW_FINDED'";
	        }
	        else
	        {
	        	sql = "SELECT * FROM BUS_DATA";
	        }

	        JSONArray jsonArray = new JSONArray();
	        try {
				ResultSet rs = null;
				PreparedStatement stmt = null;
				stmt = dbConn.prepareStatement(sql);
				rs = stmt.executeQuery();
				int i=0;
				while(rs.next()) {
					 JSONObject pass_num = new JSONObject();  
					 pass_num.put("BUS_NUMBER", rs.getString("BUS_NUMBER"));  
					 pass_num.put("SENSOR_ID", rs.getString("SENSOR_ID"));  
				     pass_num.put("INFO", rs.getString("INFO"));
				     jsonArray.add(i++, pass_num);
				     //System.out.println(log+""+i+""+jsonArray.toString());	
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
	        
	        result.put("data", jsonArray);
	        
	        String Message = result.toJSONString();
	        String jsonStr = "{\"status\":\"success\"}";
	        System.out.println(log+"FINDTOREG:"+received+", RETURN:"+jsonStr);
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
