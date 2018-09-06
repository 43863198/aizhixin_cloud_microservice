package com.aizhixin.cloud.dd;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.aizhixin.cloud.dd.approve.services.AdjustCourseScheduleRecordService;
import com.aizhixin.cloud.dd.communication.jdbcTemplate.StudentSignRollJdbc;
import com.aizhixin.cloud.dd.orgStructure.repository.UserInfoRepository;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.aizhixin.cloud.dd.common.services.DistributeLock;
import com.aizhixin.cloud.dd.remote.PaycallbackClient;
import com.aizhixin.cloud.dd.remote.StudentClient;
import com.aizhixin.cloud.dd.questionnaire.repository.QuestionnaireAssginStudentsRepository;
import com.aizhixin.cloud.dd.messege.service.PushService;
import com.aizhixin.cloud.dd.rollcall.service.SmsService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Main.class)
public class xgTest {
	@Autowired
	private QuestionnaireAssginStudentsRepository questionnaireAssginStudentsRepository;
	@Autowired
	private UserInfoRepository userInfoRepository;
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	@Autowired
	private RedisTemplate< String, String> redisTemplate;
	@Autowired
	private StudentClient studentClient;
	@Autowired
	private PaycallbackClient paycallbackClient;
	@Autowired
	private DistributeLock dis;
	@Autowired
	private PushService pushService;
	@Autowired
	private StudentSignRollJdbc studentSignRollJdbc;
	@Autowired
	private SmsService smsService;
	@Autowired
	private AdjustCourseScheduleRecordService adjustCourseScheduleRecordService;
	@Autowired
	private OrgManagerRemoteClient orgManagerRemoteService;
	@Test
	public void tt() {
		smsService.sendSms("18328029743", "[知新教师] 夏根发起了调停课审批申请，等待您的审批");
	}
	@Data
	class TeachingClassTeachersDomain{
		List<Long> ids;
		Long teachingClassId;
		
	}
	
	@Test
	public void TestHttpClient() throws ClientProtocolException, IOException {
		HttpClient hc=HttpClients.createDefault();
		HttpPost hp=new HttpPost("http://gateway.aizhixintest.com:80/org-manager/v1/teachingclassstudent/add");
		TeachingClassTeachersDomain t=new TeachingClassTeachersDomain();
		List<Long> ids=new ArrayList<>();
		ids.add(1l);
		t.setTeachingClassId(138L);
		t.setIds(ids);
		ObjectMapper om=new ObjectMapper();
		StringEntity s=new StringEntity(om.writeValueAsString(t), Charset.forName("UTF-8"));
		s.setContentType("application/json");
		hp.setEntity(s);
		HttpResponse re=hc.execute(hp);
		System.out.println(re.getStatusLine().getStatusCode());
		System.out.println(re.getEntity());
	}
	
	@Test
	public void ttt() throws InterruptedException {
		Long a=System.currentTimeMillis();
		stringRedisTemplate.opsForList().rightPush("ffff","12312");
		stringRedisTemplate.opsForList().rightPush("ffff","12");
		Long b=System.currentTimeMillis();
		System.out.println(b-a);
		System.out.println(stringRedisTemplate.opsForList().leftPop("ffff"));
	}
}
