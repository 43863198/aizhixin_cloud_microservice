package com.aizhixin.cloud.dd;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.aizhixin.cloud.dd.Main;
import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.remote.StudentClient;
import com.aizhixin.cloud.dd.rollcall.v1.controller.StudentController;

/**
 * 
 * @author meihua.li
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Main.class)
public class StudentTest {
	
	@Autowired
	private StudentClient studentClient;

	@Autowired
	private StudentController studentController;

	@Test
	public void listTest() {
		ResponseEntity<Map<String, Object>> response = studentController
				.list(123L);

		Assert.assertEquals(Boolean.TRUE, judge(response));

	}

	public static boolean judge(ResponseEntity<Map<String, Object>> response) {
		if (null == response) {
			return false;
		}
		Map<String, Object> map = response.getBody();
		if (null == map) {
			return false;
		}

		if (Boolean.TRUE != map.get(ApiReturnConstants.RESULT)) {
			return false;
		}
		return true;
	}
	@Test
	public void test() {
		System.out.println(studentClient.findByClassId(Long.valueOf(23+"")).isEmpty());
	}
}