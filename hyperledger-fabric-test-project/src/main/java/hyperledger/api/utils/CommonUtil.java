package hyperledger.api.utils;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class CommonUtil {
	
	private static Log logger = LogFactory.getLog(CommonUtil.class);

	@SuppressWarnings("unchecked")
	public static JSONArray jsonStringToObject(String data) throws Exception {
		JSONArray jsonArray = new JSONArray();
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(data);
		
		if(obj instanceof JSONArray){
			jsonArray = (JSONArray) obj;
		} else if (obj instanceof JSONObject) {
			jsonArray.add(obj);
		}
		return jsonArray;
	}
	
	/**
     * JsonObject를 Map<String, String>으로 변환한다.
     *
     * @param jsonObj JSONObject.
     * @return Map<String, Object>.
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> getMapFromJsonObject( JSONObject jsonObj ) throws JsonParseException, JsonMappingException, IOException
    {
        Map<String, Object> map = null;

        map = new ObjectMapper().readValue(jsonObj.toString(), Map.class) ;

 
        return map;
    }
}

