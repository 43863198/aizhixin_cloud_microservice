
package com.aizhixin.cloud.jedis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import redis.clients.jedis.Jedis;

/** 
 * @ClassName: testj 
 * @Description: 
 * @author xiagen
 * @date 2017年8月28日 上午9:24:32 
 *  
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class testj {
	         @Test
    public void ttt(){
	        	 Jedis je=new Jedis("172.16.23.117", 6379);
	        	 je.select(5);
	        	 je.lpush("add:teachingclass", "add:teachingclass");
    	
    }
}
