
package com.aizhixin.cloud.school.schoolinfo.util;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/** 
 * @ClassName: JsonUtil 
 * @Description: 
 * @author xiagen
 * @date 2017年5月15日 下午2:54:46 
 *  
 */

public class JsonUtil {
    @SuppressWarnings("unchecked")
	public static Map<String,Object> Json2Object(String json) throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String,Object> map=mapper.readValue(json, Map.class);
        return map;
    }
}
