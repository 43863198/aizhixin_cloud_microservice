package com.aizhixin.cloud.studentpractice.common.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.aizhixin.cloud.studentpractice.common.PageData;
import com.aizhixin.cloud.studentpractice.common.PageDomain;
import com.aizhixin.cloud.studentpractice.common.domain.AccountDTO;
import com.aizhixin.cloud.studentpractice.common.domain.IdNameDomain;
import com.aizhixin.cloud.studentpractice.common.domain.MessageDTOV2;
import com.aizhixin.cloud.studentpractice.common.domain.PushMessageDTO;
import com.aizhixin.cloud.studentpractice.common.domain.QueryCommentTotalDomain;
import com.aizhixin.cloud.studentpractice.common.domain.TrainingRelationInfoDTO;
import com.aizhixin.cloud.studentpractice.common.domain.UserInfoDomain;
import com.aizhixin.cloud.studentpractice.common.rest.RestUtil;
import com.aizhixin.cloud.studentpractice.task.domain.StuInforDomain;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class AuthUtilService {
	private final static Logger log = LoggerFactory
			.getLogger(AuthUtilService.class);

	@Value("${dl.dledu.back.userInfoUrl}")
	private String userInfoUrl;

	@Value("${dl.dledu.back.host}")
	private String dleduBackHost;

	@Value("${dl.dledu.back.userAvatarUrl}")
	private String userAvatarUrl;

	@Value("${dl.org.back.host}")
	private String orgBackHost;

	@Value("${dl.org.back.dbname}")
	private String orgBackDbName;

	@Value("${dl.org.back.querymentor}")
	private String queryMentorUrl;

	@Value("${dl.org.back.querystudent}")
	private String queryStudentUrl;

	@Value("${dl.org.back.querygroupformentor}")
	private String queryGroupForMentor;

	@Value("${dl.dd.back.host}")
	private String ddHost;
	
	@Value("${dl.dd.back.savemsg}")
	private String savemsgUrl;
	
	@Value("${dl.org.back.querygroupinfolist}")
	private String querygroupinfolist;
	
	@Value("${dl.org.back.queryclassteacher}")
	private String queryclassteacher;
	
	@Value("${dl.em.back.querycommenttotal}")
	private String queryCommentTotal;
	
	@Value("${dl.em.back.host}")
	private String emHost;
	
	@Value("${dl.dd.back.dbname}")
	private String ddBackDbName;
	
	@Autowired
	private RestUtil restUtil;

	public String getOrgDbName() {
		return orgBackDbName;
	}
	
	public String getDdDbName() {
		return ddBackDbName;
	}

	@Cacheable(value = "authorCache")
	public AccountDTO getSsoUserInfo(String token) {
		RestTemplate rest = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		List<MediaType> mt = new ArrayList<MediaType>();
		mt.add(MediaType.APPLICATION_JSON_UTF8);
		headers.set(HttpHeaders.AUTHORIZATION, token);// {"access_token":"42c5852c-bdaf-4cea-a09c-0cab54988120","token_type":"bearer","refresh_token":"e4fa3c77-6041-482a-bc5a-2e0d2e5c48e4","expires_in":13933,"scope":"read
		headers.set("Encoding", "UTF-8");
		headers.setAccept(mt);// {"access_token":"eba06c1c-84b9-4706-a27f-0dd11bcc30bc","token_type":"bearer","refresh_token":"384aba1d-925c-4ffb-94f3-c674e550c79e","expires_in":85989,"scope":"read
								// write"}
		System.out.println(token);
		AccountDTO dto = null;
		HttpEntity<byte[]> entity = new HttpEntity<byte[]>(headers);
		try {
			ResponseEntity<AccountDTO> response = rest.exchange(dleduBackHost
					+ userInfoUrl, HttpMethod.GET, entity, AccountDTO.class);
			if (HttpStatus.SC_OK != response.getStatusCode().value()) {
				System.out.println(response.getStatusCode().value());
			} else {
				dto = response.getBody();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dto;
	}
	
	

	public AccountDTO getavatarUserInfo(Long id) {
		RestTemplate rest = new RestTemplate();
		AccountDTO dto = new AccountDTO();
		try {
			String str = rest.getForObject(dleduBackHost + userAvatarUrl
					+ "?ids=" + id, String.class);
			JSONObject jsonObject = new JSONObject(str);
			Iterator<?> iterator = jsonObject.keys();
			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				String value = jsonObject.get(key).toString();
				value = value.replaceAll("null", "\"\"");
				JSONObject jsonUser = new JSONObject(value);
				dto.setName(jsonUser.get("userName").toString());
				dto.setAvatar(jsonUser.get("avatar").toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dto;
	}

	public HashMap<Long, AccountDTO> getavatarUsersInfo(String ids) {

		RestTemplate rest = new RestTemplate();
		HashMap<Long, AccountDTO> map = new HashMap<Long, AccountDTO>();
		try {
			String str = rest.getForObject(dleduBackHost + userAvatarUrl
					+ "?ids=" + ids, String.class);
			JSONObject jsonObject = new JSONObject(str);
			Iterator<?> iterator = jsonObject.keys();
			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				String value = jsonObject.get(key).toString();
				value = value.replaceAll("null", "\"\"");
				log.info("获取知新用户信息-->" + value);
				JSONObject jsonUser = new JSONObject(value);
				AccountDTO dto = new AccountDTO();
				dto.setId(jsonUser.getLong("id"));
				dto.setName(jsonUser.getString("userName"));
				dto.setAvatar(jsonUser.getString("avatar"));
				dto.setPhone(jsonUser.getString("phoneNumber"));
				map.put(dto.getId(), dto);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 根据企业导师id查询当前时间有效的实践小组信息
	 * 
	 * @param id
	 * @return
	 */
	public List<IdNameDomain> getMentorGroupInfo(Long id) {
		RestTemplate rest = new RestTemplate();
		List<IdNameDomain> list = new ArrayList<IdNameDomain>();
		try {
			String str = rest.getForObject(orgBackHost + queryGroupForMentor
					+ "?accountId=" + id, String.class);
			if (!StringUtils.isEmpty(str) && !str.equals("[]")) {
				List<HashMap> dataInfor = JSON.parseArray(str.toString(),
						HashMap.class);
				for (int i = 0; i < dataInfor.size(); i++) {
					if (null != dataInfor.get(i).get("id")) {
						IdNameDomain dto = new IdNameDomain();
						dto.setId(Long.valueOf(dataInfor.get(i).get("id")
								.toString()));
						list.add(dto);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	

	public StuInforDomain getMentorInfo(Long id) {
		RestTemplate rest = new RestTemplate();
		StuInforDomain dto = new StuInforDomain();
		try {
			String str = rest.getForObject(orgBackHost + queryMentorUrl
					+ "?accountId=" + id, String.class);
			com.alibaba.fastjson.JSONObject jsonObj = JSON.parseObject(str);
			if (null != jsonObj.get("accountId")) {
				dto.setMentorId(Long.valueOf(jsonObj.get("accountId")
						.toString()));
			}
			if (null != jsonObj.get("cname")) {
				dto.setMentorName(jsonObj.get("cname").toString());
			}
			if (null != jsonObj.get("companyAddress")) {
				dto.setMentorCompanyAddr(jsonObj.get("companyAddress")
						.toString());
			}
			if (null != jsonObj.get("enterpriseName")) {
				dto.setMentorCompanyName(jsonObj.get("enterpriseName")
						.toString());
			}
			if (null != jsonObj.get("phone")) {
				dto.setMentorPhone(jsonObj.get("phone").toString());
			}
			if (null != jsonObj.get("sname")) {
				dto.setName(jsonObj.get("sname").toString());
			}
			if (null != jsonObj.get("sex")) {
				dto.setSex(jsonObj.get("sex").toString());
			}
			if (null != jsonObj.get("classesName")) {
				dto.setStuClassName(jsonObj.get("classesName").toString());
			}
			if (null != jsonObj.get("collegeName")) {
				dto.setStuCollege(jsonObj.get("collegeName").toString());
			}
			if (null != jsonObj.get("professionalName")) {
				dto.setStuProfession(jsonObj.get("professionalName").toString());
			}
			if (null != jsonObj.get("sphone")) {
				dto.setStuPhone(jsonObj.get("sphone").toString());
			}
			if (null != jsonObj.get("sid")) {
				dto.setId(Long.valueOf(jsonObj.get("sid").toString()));
			}
			if (null != jsonObj.get("sjobNumber")) {
				dto.setJobNum(jsonObj.get("sjobNumber").toString());
			}
			if (null != jsonObj.get("tjobNumber")) {
				dto.setCounselorJobNum(jsonObj.get("tjobNumber").toString());
			}
			if (null != jsonObj.get("trainingGroupId")) {
				dto.setTrainingGroupId(Long.valueOf(jsonObj.get(
						"trainingGroupId").toString()));
			}
			if (null != jsonObj.get("trainingGroupName")) {
				dto.setTrainingGroupName(jsonObj.get(
						"trainingGroupName").toString());
			}
			if (null != jsonObj.get("teacherId")) {
				dto.setCounselorId(Long.valueOf(jsonObj.get(
						"teacherId").toString()));
			}
			if (null != jsonObj.get("teacherName")) {
				dto.setCounselorName(jsonObj.get(
						"teacherName").toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dto;
	}

	/**
	 * 根据导师id查询学生信息列表
	 * 
	 * @param id
	 *            (导师id必填)
	 * @param stuName
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public PageData getStudentInfo(Long id, String stuName, Integer pageNumber,
			Integer pageSize) {

		RestTemplate rest = new RestTemplate();
		PageData pageData = new PageData();
		try {
			String url = orgBackHost + queryStudentUrl + "?accountId=" + id;
			if (!StringUtils.isEmpty(stuName)) {
				url += "&name=" + stuName;
			}
			if (null != pageNumber && pageNumber > 0) {
				url += "&pageNumber=" + pageNumber;
			}
			if (null != pageSize && pageSize > 0) {
				url += "&pageSize=" + pageSize;
			}
			String str = rest.getForObject(url, String.class);
			com.alibaba.fastjson.JSONObject jsonObj = JSON.parseObject(str);

			String enterpriseName = "";
			if (null != jsonObj.get("enterpriseName")) {
				enterpriseName = jsonObj.get("enterpriseName").toString();
			}
			Long orgId = null;
			if (null != jsonObj.get("orgId")) {
				orgId = Long.valueOf(jsonObj.get("orgId").toString());
			}
			Long groupId = null;
			if (null != jsonObj.get("trainingGroupId")) {
				groupId =Long.valueOf(jsonObj.get("trainingGroupId").toString());
			}
			Object result = jsonObj.get("studentDTOList");
			if (null != result) {
				com.alibaba.fastjson.JSONObject studentDTOList = JSON
						.parseObject(result.toString());
				Object pageObj = studentDTOList.get("page");
				com.alibaba.fastjson.JSONObject pageInfor = JSON
						.parseObject(pageObj.toString());
				PageDomain page = new PageDomain();
				page.setPageSize(Integer.valueOf(pageInfor.get("pageSize")
						.toString()));
				page.setPageNumber(Integer.valueOf(pageInfor.get("pageNumber")
						.toString()));
				if (page.getPageNumber().intValue() == 0) {
					page.setPageNumber(1);
				}
				page.setTotalPages(Integer.valueOf(pageInfor.get("totalPages")
						.toString()));
				page.setTotalElements(Long.valueOf(pageInfor.get(
						"totalElements").toString()));
				pageData.setPage(page);

				Object data = studentDTOList.get("data");
				List<HashMap> dataInfor = JSON.parseArray(data.toString(),
						HashMap.class);
				if (null != dataInfor && dataInfor.size() > 0) {
					List<StuInforDomain> stuList = new ArrayList<StuInforDomain>();
					for (int i = 0; i < dataInfor.size(); i++) {
						StuInforDomain stu = new StuInforDomain();
						if (null != dataInfor.get(i).get("accountId")) {
							stu.setId(Long.valueOf(dataInfor.get(i)
									.get("accountId").toString()));
						}
						if (!StringUtils.isEmpty(dataInfor.get(i).get("name"))) {
							stu.setName(dataInfor.get(i).get("name").toString());
						}
						if (!StringUtils.isEmpty(dataInfor.get(i).get(
								"jobNumber"))) {
							stu.setJobNum(dataInfor.get(i).get("jobNumber")
									.toString());
						}
						if (null != dataInfor.get(i).get("orgId")) {
							stu.setOrgId(Long.valueOf(dataInfor.get(i)
									.get("orgId").toString()));
						}
						if (null != dataInfor.get(i).get("classesId")) {
							stu.setClassId(Long.valueOf(dataInfor.get(i)
									.get("classesId").toString()));
						}
						if (null != dataInfor.get(i).get("professionalId")) {
							stu.setProfessionalId(Long.valueOf(dataInfor.get(i)
									.get("professionalId").toString()));
						}
						if (null != dataInfor.get(i).get("collegeId")) {
							stu.setCollegeId(Long.valueOf(dataInfor.get(i)
									.get("collegeId").toString()));
						}
						if (null != groupId) {
							stu.setTrainingGroupId(groupId);
						}
						stu.setMentorCompanyName(enterpriseName);
						stu.setOrgId(orgId);
						stuList.add(stu);
					}
					pageData.setData(stuList);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pageData;
	}

	/**
	 * 发送消息
	 * @param dto
	 */
	public void saveMsgInfor(PushMessageDTO dto){
		try{
			RestTemplate restTemplate=new RestTemplate();
			restTemplate.exchange(ddHost + savemsgUrl, HttpMethod.POST,new HttpEntity<PushMessageDTO>(dto), String.class);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	
    public void pushMsg(MessageDTOV2 dto) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String inJson = mapper.writeValueAsString(dto);
            String outJson = restUtil.postBody(ddHost + "/api/v1/message/pushMessage", inJson, "pushMsg");
//            System.out.println(outJson);
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
    }
	
	/**
	 * 根据实践小组id集合查询实践小组企业导师和学生对应关系
	 * @param ids
	 * @return
	 */
	public List<TrainingRelationInfoDTO> getGroupInfoListByIds(List<Long> ids){
		RestTemplate rest = new RestTemplate();
		List<TrainingRelationInfoDTO> list = null;
		ParameterizedTypeReference<List<TrainingRelationInfoDTO>> typeRef = new ParameterizedTypeReference<List<TrainingRelationInfoDTO>>(){};  
			ResponseEntity<List<TrainingRelationInfoDTO>> result = rest.exchange(orgBackHost + querygroupinfolist, HttpMethod.PUT, new HttpEntity<List<Long>>(ids),typeRef);
        	list = result.getBody();
        	if(null != list){
        		log.info("查询实践小组数量："+list.size());
        	}else{
        		log.info("查询实践小组数量为0!");	
        	}
		return list;
	}
	
	
//	public List<IdNameDomain> getClassTeacherId(Long id) {
//		RestTemplate rest = new RestTemplate();
//		List<IdNameDomain> list = new ArrayList<IdNameDomain>();
//		try {
//			String str = rest.getForObject(orgBackHost + queryclassteacher
//					+ "?classesId=" + id, String.class);
//			if (!StringUtils.isEmpty(str) && !str.equals("[]")) {
//				List<HashMap> dataInfor = JSON.parseArray(str.toString(),
//						HashMap.class);
//				for (int i = 0; i < dataInfor.size(); i++) {
//					if (null != dataInfor.get(i).get("id")) {
//						IdNameDomain dto = new IdNameDomain();
//						dto.setId(Long.valueOf(dataInfor.get(i).get("id")
//								.toString()));
//						list.add(dto);
//					}
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return list;
//	}
	
	public IdNameDomain getClassTeacherId(Long id) {
		RestTemplate rest = new RestTemplate();
		IdNameDomain domain = new IdNameDomain();
		try {
			String str = rest.getForObject(orgBackHost + queryclassteacher
					+ "?classesId=" + id, String.class);
			if (!StringUtils.isEmpty(str) && !str.equals("[]")) {
				com.alibaba.fastjson.JSONObject jsonObj = JSON.parseObject(str);
				Object data = jsonObj.get("data");
				List<HashMap> dataInfor = JSON.parseArray(data.toString(),
						HashMap.class);
				if(null != dataInfor){
					if(dataInfor.isEmpty()){
						return domain;
					}
					if (null != dataInfor.get(0).get("id")) {
						domain.setId(Long.valueOf(dataInfor.get(0).get("id")
								.toString()));
					}
					if (null != dataInfor.get(0).get("name")) {
						domain.setName(dataInfor.get(0).get("name")
								.toString());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return domain;
	}
	
	
	public HashMap<String,Integer> getCommentTotalCount(QueryCommentTotalDomain domain,String token){
		RestTemplate rest = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, token);
		String url = emHost + queryCommentTotal;
	    ResponseEntity<String> resultObj = rest.exchange(url, HttpMethod.PUT, new HttpEntity<QueryCommentTotalDomain>(domain,headers),String.class);
        String resultStr = resultObj.getBody();
        
        com.alibaba.fastjson.JSONObject jsonObj = JSON.parseObject(resultStr);

        HashMap<String,Integer> commentTotalMap = new HashMap<String,Integer>();
			Object data = jsonObj.get("data");
			List<HashMap> dataInfor = JSON.parseArray(data.toString(),
					HashMap.class);
			if (null != dataInfor && dataInfor.size() > 0) {
				for (int i = 0; i < dataInfor.size(); i++) {
					String sourceId = "";
					if (null != dataInfor.get(i).get("sourceId")) {
						 sourceId = dataInfor.get(i)
								.get("sourceId").toString();
					}
					Integer commentTotal = 0;
					if (null != dataInfor.get(i).get("commentTotal")) {
						 commentTotal = Integer.valueOf(dataInfor.get(i)
								.get("commentTotal").toString());
					}
					if(!StringUtils.isEmpty(sourceId)){
					commentTotalMap.put(sourceId, commentTotal);
					}
				}
			}	
		return commentTotalMap;
	}
	
	public void openCallGroup(Long tempGroupId){
		try{
			RestTemplate restTemplate=new RestTemplate();
			restTemplate.exchange(ddHost.concat("/api/phone/v2/counsellor/practice/teacher/openCounsellorGroup?tempGroupId="+tempGroupId),HttpMethod.PUT,null,String.class);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void closeCallGroup(Long tempGroupId){
		try{
			RestTemplate restTemplate=new RestTemplate();
			restTemplate.exchange(ddHost.concat("/api/phone/v2/counsellor/practice/teacher/closeCounsellorGroup?tempGroupId="+tempGroupId),HttpMethod.PUT,null,String.class);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
