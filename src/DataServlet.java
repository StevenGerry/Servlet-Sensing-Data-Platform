import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
 
public class DataServlet extends HttpServlet {
	/**
	 * Get data from the edge devices by POSTJSON
	 */
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.setCharacterEncoding("UTF-8");
	    response.setContentType("text/html;charset=UTF-8");
	    String acceptjson = "";
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
	        
	        
	        
	        
	        String jsonStr = "{\"status\":\"success\"}";
	        response.getWriter().write(jsonStr);
	    } catch (Exception e) {
	    	String jsonStr = "{\"status\":\"error\"}";
	    	response.getWriter().write(jsonStr);
	        e.printStackTrace();
	    }
	System.out.println(acceptjson);
	}
}