package com.aizhixin.cloud.orgmanager.training.service;

import com.aizhixin.cloud.orgmanager.common.PageData;
import com.aizhixin.cloud.orgmanager.common.core.DataValidity;
import com.aizhixin.cloud.orgmanager.common.core.ErrorCode;
import com.aizhixin.cloud.orgmanager.common.core.ExcelImportStatus;
import com.aizhixin.cloud.orgmanager.common.exception.CommonException;
import com.aizhixin.cloud.orgmanager.common.provider.store.redis.RedisTokenStore;
import com.aizhixin.cloud.orgmanager.common.rest.RestUtil;
import com.aizhixin.cloud.orgmanager.company.domain.BatchAddUserDomain;
import com.aizhixin.cloud.orgmanager.company.domain.excel.*;
import com.aizhixin.cloud.orgmanager.company.entity.User;
import com.aizhixin.cloud.orgmanager.company.service.ExcelBasedataHelper;
import com.aizhixin.cloud.orgmanager.company.service.UserService;
import com.aizhixin.cloud.orgmanager.training.dto.*;
import com.aizhixin.cloud.orgmanager.training.entity.CorporateMentorsInfo;
import com.aizhixin.cloud.orgmanager.training.entity.TrainingGroup;
import com.aizhixin.cloud.orgmanager.training.repository.MentorsTrainingRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import org.apache.commons.collections.list.AbstractLinkedList;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.util.*;

/**
 * @author Created by jianwei.wu
 * @E-mail wujianwei@aizhixin.com
 */
@Service
@Transactional
public class MentorsTrainingService {
    @Autowired
    MentorsTrainingRepository mentorsTrainingRepository;
    @Value("${zhixin.api.url}")
    private String zhixinApi;
    @Autowired
    private RestUtil restUtil;
    @Autowired
    private TrainingGroupService trainingGroupService;
    @Autowired
    GroupRelationService groupRelationService;
    @Autowired
    private ExcelBasedataHelper excelBasedataHelper;
    @Autowired
    UserService userService;

    /**
     * 企业导师信息保存
     *
     * @param corporateMentorsInfoDTO
     * @return
     */
    public Map<String, Object>    save(CorporateMentorsInfoDTO corporateMentorsInfoDTO) {
        Map<String, Object> result = new HashMap<>();
        //判断手机号码是否已存在
        if (!StringUtils.isBlank(corporateMentorsInfoDTO.getPhone())) {
            List<CorporateMentorsInfo> corporateMentorsInfo2 = mentorsTrainingRepository.findByPhoneAndDeleteFlag(corporateMentorsInfoDTO.getPhone(), DataValidity.VALID.getState());
            if (corporateMentorsInfo2.size()>0) {
                result.put("success", false);
                result.put("message", "手机号码已存在！");
                return result;
            }
        }
        //判断邮箱是否已存在
        if (!StringUtils.isBlank(corporateMentorsInfoDTO.getMailbox())) {
            List<CorporateMentorsInfo> corporateMentorsInfo1 = mentorsTrainingRepository.findByMailboxAndDeleteFlag(corporateMentorsInfoDTO.getMailbox(), DataValidity.VALID.getState());
            if (corporateMentorsInfo1.size()>0) {
                result.put("success", false);
                result.put("message", "邮箱已存在！");
                return result;
            }
        }
        String time =  String.valueOf(System.currentTimeMillis());
        String uuid = UUID.randomUUID().toString();
        Long accountId = null;
        String accountNo = null;
//        String str = uuid.substring(uuid.length()-3,uuid.length())+time.substring(time.length()-3,time.length());
        String str = corporateMentorsInfoDTO.getPhone();
        if(StringUtils.isEmpty(str)){
        	str = getLoginName(corporateMentorsInfoDTO.getOrgId());
        }
        accountNo = "qygl" + str;
        try {
            String json = restUtil.post(zhixinApi + "/api/account/add" + "?loginName=" + accountNo + "&name=" + corporateMentorsInfoDTO.getName() + "&userType=COM", null);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(json);
            if (null != rootNode) {
                JsonNode idNode = rootNode.path("id");
                if (null != idNode) {
                    accountId = idNode.longValue();
                }else{
                    result.put("success", false);
                    result.put("message", "创建账号失败！");
                    return result;
                }
            }else{
                result.put("success", false);
                result.put("message", "创建账号失败！");
                return result;
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "创建账号失败！");
            return result;
        }
        CorporateMentorsInfo corporateMentorsInfo = new CorporateMentorsInfo();
        if (null != corporateMentorsInfoDTO.getName()) {
            corporateMentorsInfo.setName(corporateMentorsInfoDTO.getName());
        }
        if (null != corporateMentorsInfoDTO.getDepartment()) {
            corporateMentorsInfo.setDepartment(corporateMentorsInfoDTO.getDepartment());
        }
        if (null != corporateMentorsInfoDTO.getEnterpriseName()) {
            corporateMentorsInfo.setEnterpriseName(corporateMentorsInfoDTO.getEnterpriseName());
        }
        if(null!=corporateMentorsInfoDTO.getEnterpriseId()){
            corporateMentorsInfo.setEnterpriseId(corporateMentorsInfoDTO.getEnterpriseId());
        }
        if (null != corporateMentorsInfoDTO.getCompanyAddress()) {
            corporateMentorsInfo.setCompanyAddress(corporateMentorsInfoDTO.getCompanyAddress());
        }
        if (null != corporateMentorsInfoDTO.getJobNumber()) {
            corporateMentorsInfo.setJobNumber(corporateMentorsInfoDTO.getJobNumber());
        }
        if (null != corporateMentorsInfoDTO.getMailbox()) {
            corporateMentorsInfo.setMailbox(corporateMentorsInfoDTO.getMailbox());
        }
        if (null != corporateMentorsInfoDTO.getPhone()) {
            corporateMentorsInfo.setPhone(corporateMentorsInfoDTO.getPhone());
        }
        if (null != corporateMentorsInfoDTO.getPosition()) {
            corporateMentorsInfo.setPosition(corporateMentorsInfoDTO.getPosition());
        }
        if (null != accountId) {
            corporateMentorsInfo.setAccountId(accountId);
        }
        if (null != accountNo) {
            corporateMentorsInfo.setLoginName(accountNo);
        }
        if(null !=  corporateMentorsInfoDTO.getOrgId()){
            corporateMentorsInfo.setOrgId(corporateMentorsInfoDTO.getOrgId());
        }
        if(null != corporateMentorsInfoDTO.getEnterpriseId()){
            corporateMentorsInfo.setEnterpriseId(corporateMentorsInfoDTO.getEnterpriseId());
        }
        if(null != corporateMentorsInfoDTO.getProvince()){
            corporateMentorsInfo.setProvince(corporateMentorsInfoDTO.getProvince());
        }
        if(null != corporateMentorsInfoDTO.getCity()){
            corporateMentorsInfo.setCity(corporateMentorsInfoDTO.getCity());
        }
        try {
            mentorsTrainingRepository.save(corporateMentorsInfo);
            result.put("success", true);
            result.put("message", "企业导师信息保存成功！");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "企业导师信息保存失败！");
            return result;
        }
        return result;
    }

	private String getLoginName(Long orgId) {
		String str = "";
        int countNum = mentorsTrainingRepository.countByOrgId(orgId).intValue()+1;
        String numStr = String.valueOf(countNum);
        if(numStr.length() == 1){
        	str = "000".concat(numStr);
        }
		if(numStr.length() == 2){
			str = "00".concat(numStr);	
		}
		if(numStr.length() == 3){
			str = "0".concat(numStr);	
		}
		if(numStr.length() == 4){
			str = numStr;
		}
		return str;
	}

    /**
     * 根据条件查询企业导师信息
     *
     * @param pageable
     * @param name
     * @return
     */
    @Transactional(readOnly = true)
    public PageData<CorporateMentorsInfo> queryCorporateMentorsPage(Pageable pageable, String name, Long orgId) {
        PageData<CorporateMentorsInfo> data = new PageData<>();
        Page<CorporateMentorsInfo> corporateMentorsInfoPage = null;
        if (null != name) {
            corporateMentorsInfoPage = mentorsTrainingRepository.findCorporateMentorsInfoByPageName(pageable, name,orgId, DataValidity.VALID.getState());
        } else {
            corporateMentorsInfoPage = mentorsTrainingRepository.findCorporateMentorsInfoByPage(pageable, orgId, DataValidity.VALID.getState());
        }
        if (null != corporateMentorsInfoPage) {
            data.setData(corporateMentorsInfoPage.getContent());
            data.getPage().setTotalPages(corporateMentorsInfoPage.getTotalPages());
            data.getPage().setTotalElements(corporateMentorsInfoPage.getTotalElements());
        }
        data.getPage().setPageNumber(pageable.getPageNumber());
        data.getPage().setPageSize(pageable.getPageSize());
        return data;
    }

    /**
     * 根据id查询企业导师相关信息
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public CorporateMentorsInfo queryCorporateMentorsInfoById(Long id) {
        return mentorsTrainingRepository.findOne(id);
    }

    /**
     * 根据id删除企业导师相关信息
     *
     * @param id
     * @return
     */
    public Map<String,Object> deleteCorporateMentorsInfoById(Long id,String accessToken) {
        Map<String,Object> result = new HashMap<>();
        CorporateMentorsInfo corporateMentorsInfo = mentorsTrainingRepository.findOne(id);
        if (null != corporateMentorsInfo) {
            corporateMentorsInfo.setDeleteFlag(DataValidity.INVALID.getState());
            mentorsTrainingRepository.save(corporateMentorsInfo);
//            List<TrainingGroupInfoDTO> trainingGroupInfoDTOList = trainingGroupService.getGroupAllInfoBycorporateMentorsId(corporateMentorsInfo.getId());
//            if (trainingGroupInfoDTOList.size()>0) {
//                TrainingGroupInfoDTO trainingGroupInfoDTO = trainingGroupInfoDTOList.get(0);
//                TrainingGroup trainingGroup = trainingGroupService.getGroupInfoById(trainingGroupInfoDTO.getId());
//                trainingGroup.setDeleteFlag(DataValidity.INVALID.getState());
//                trainingGroupService.save(trainingGroup);
//                //物理删除实训小组下的所有学生的关系
//                groupRelationService.dedeleteByGroupId(trainingGroupInfoDTO.getId());
//            }
            try {
                restUtil.delete(zhixinApi + "/api/web/v1/users/" + corporateMentorsInfo.getAccountId(), accessToken);
            } catch (Exception e) {
                result.put("success", false);
                result.put("message", "企业导师的账号删除失败！");
                return result;
            }
            result.put("success", true);
            result.put("message","企业导师信息删除成功！");
        }
        return result;
    }

    /**
     * 更新企业导师相关信息
     *
     * @param corporateMentorsInfoDTO
     * @return
     */
    public Map<String, Object> updateCorporateMentorsInfo(CorporateMentorsInfoDTO corporateMentorsInfoDTO) {
        Map<String, Object> result = new HashMap<>();
        //判断手机号码是否已存在
        if (!StringUtils.isBlank(corporateMentorsInfoDTO.getPhone())) {
            List<CorporateMentorsInfo> corporateMentorsInfo2 = mentorsTrainingRepository.findByPhoneAndDeleteFlag(corporateMentorsInfoDTO.getPhone(), DataValidity.VALID.getState());
            if (corporateMentorsInfo2.size()>0) {
                if(corporateMentorsInfo2.size()==1){
                    if(corporateMentorsInfoDTO.getId().equals(corporateMentorsInfo2.get(0).getId())){
                    }else {
                        result.put("success", false);
                        result.put("message", "手机号码已存在！");
                        return result;
                    }
                }else {
                    result.put("success", false);
                    result.put("message", "手机号码已存在！");
                    return result;
                }

            }
        }
        //判断邮箱是否已存在
        if (!StringUtils.isBlank(corporateMentorsInfoDTO.getMailbox())) {
            List<CorporateMentorsInfo> corporateMentorsInfo1 = mentorsTrainingRepository.findByMailboxAndDeleteFlag(corporateMentorsInfoDTO.getMailbox(), DataValidity.VALID.getState());
            if (corporateMentorsInfo1.size()>0) {
                if(corporateMentorsInfo1.size()==1){
                    if(corporateMentorsInfoDTO.getId().equals(corporateMentorsInfo1.get(0).getId())){
                    }else{
                        result.put("success", false);
                        result.put("message", "邮箱已存在！");
                        return result;
                    }
                }else {
                    result.put("success", false);
                    result.put("message", "邮箱已存在！");
                    return result;
                }
            }
        }
        try {
            CorporateMentorsInfo corporateMentorsInfo = mentorsTrainingRepository.findOne(corporateMentorsInfoDTO.getId());
            if (null != corporateMentorsInfoDTO.getName()) {
                corporateMentorsInfo.setName(corporateMentorsInfoDTO.getName());
            }
            if (null != corporateMentorsInfoDTO.getDepartment()) {
                corporateMentorsInfo.setDepartment(corporateMentorsInfoDTO.getDepartment());
            }
            if (null != corporateMentorsInfoDTO.getEnterpriseName()) {
                corporateMentorsInfo.setEnterpriseName(corporateMentorsInfoDTO.getEnterpriseName());
            }
            if (null != corporateMentorsInfoDTO.getCompanyAddress()) {
                corporateMentorsInfo.setCompanyAddress(corporateMentorsInfoDTO.getCompanyAddress());
            }
            if (null != corporateMentorsInfoDTO.getJobNumber()) {
                corporateMentorsInfo.setJobNumber(corporateMentorsInfoDTO.getJobNumber());
            }
            if (null != corporateMentorsInfoDTO.getMailbox()) {
                corporateMentorsInfo.setMailbox(corporateMentorsInfoDTO.getMailbox());
            }
            if (null != corporateMentorsInfoDTO.getPhone()) {
                corporateMentorsInfo.setPhone(corporateMentorsInfoDTO.getPhone());
            }
            if (null != corporateMentorsInfoDTO.getPosition()) {
                corporateMentorsInfo.setPosition(corporateMentorsInfoDTO.getPosition());
            }
            if(null !=  corporateMentorsInfoDTO.getOrgId()){
                corporateMentorsInfo.setOrgId(corporateMentorsInfoDTO.getOrgId());
            }
            if(null != corporateMentorsInfoDTO.getEnterpriseId()){
                corporateMentorsInfo.setEnterpriseId(corporateMentorsInfoDTO.getEnterpriseId());
            }
            if(null != corporateMentorsInfoDTO.getProvince()){
                corporateMentorsInfo.setProvince(corporateMentorsInfoDTO.getProvince());
            }
            if(null != corporateMentorsInfoDTO.getCity()){
                corporateMentorsInfo.setCity(corporateMentorsInfoDTO.getCity());
            }
            corporateMentorsInfo.setLastModifiedDate(new Date());
            mentorsTrainingRepository.save(corporateMentorsInfo);
            result.put("success", true);
            result.put("message", "企业导师信息更新成功！");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "企业导师信息更新失败！");
            return result;
        }
        return result;
    }

    /**
     * 根据企业导师的id 查询企业导师相关的信息
     *
     * @param id
     * @return
     */
//    @Transactional(readOnly = true)
//    public CorporateMentorsInfoByEnterpriseDTO queryById(Long id) {
//        CorporateMentorsInfoByEnterpriseDTO data = new CorporateMentorsInfoByEnterpriseDTO();
//        if (null != id) {
//            CorporateMentorsInfo corporateMentorsInfo = mentorsTrainingRepository.findOne(id);
//            List<TrainingGroupInfoDTO> trainingGroupInfoDTOList = trainingGroupService.getGroupAllInfoBycorporateMentorsId(id);
//            if (null != corporateMentorsInfo) {
//                data.setId(corporateMentorsInfo.getId());
//                data.setLoginName(corporateMentorsInfo.getLoginName());
//                data.setDepartment(corporateMentorsInfo.getDepartment());
//                data.setPosition(corporateMentorsInfo.getPosition());
//                data.setEnterpriseName(corporateMentorsInfo.getEnterpriseName());
//                data.setCompanyAddress(corporateMentorsInfo.getCompanyAddress());
//                data.setAccountId(corporateMentorsInfo.getAccountId());
//                data.setJobNumber(corporateMentorsInfo.getJobNumber());
//                data.setMailbox(corporateMentorsInfo.getMailbox());
//                data.setPhone(corporateMentorsInfo.getPhone());
//                data.setPosition(corporateMentorsInfo.getPosition());
//                data.setProvince(corporateMentorsInfo.getProvince());
//                data.setCity(corporateMentorsInfo.getCity());
//            }
//            if (trainingGroupInfoDTOList.size()>0) {
//                TrainingGroupInfoDTO trainingGroupInfoDTO = trainingGroupInfoDTOList.get(0);
//                data.setTeacherId(trainingGroupInfoDTO.getTeacherId());
//                data.setTeacherName(trainingGroupInfoDTO.getTeacherName());
//                data.setTeacherJobNumer(trainingGroupInfoDTO.getJobNumber());
//                data.setCollegeName(trainingGroupInfoDTO.getCollegeName());
//                data.setTrainingGroupId(trainingGroupInfoDTO.getId());
//                data.setTrainingGroupName(trainingGroupInfoDTO.getGropName());
//                data.setStarDate(trainingGroupInfoDTO.getStartDate());
//                data.setEndDate(trainingGroupInfoDTO.getEndDate());
//                List<StudentDTO> studentDTOs = groupRelationService.findStudentsByGroupId(trainingGroupInfoDTO.getId());
//                if (null != studentDTOs && studentDTOs.size() > 0) {
//                    data.setStudentDTOList(studentDTOs);
//                }
//            }
//
//        }
//        return data;
//    }

    /**
     * 根据企业导师的accoutId 查询企业导师相关的信息
     *
     * @param accountId
     * @return
     */
    @Transactional(readOnly = true)
    public List<CorporateMentorsInfo>  queryByAccountId(Long accountId){
        return mentorsTrainingRepository.findByAccountIdAndDeleteFlag(accountId,DataValidity.VALID.getState());
    }
    /**
     * 查询企业导师相关的信息
     *
     * @param accoutId
     * @param name
     * @return
     */
    @Transactional(readOnly = true)
    public CorporateMentorsInfoPageByEnterpriseDTO queryPageByAccountIdOrName(Pageable pageable, Long accoutId, String name) {
        CorporateMentorsInfoPageByEnterpriseDTO data = new CorporateMentorsInfoPageByEnterpriseDTO();
        if (null != accoutId) {
            List<CorporateMentorsInfo> corporateMentorsInfoList = mentorsTrainingRepository.findByAccountIdAndDeleteFlag(accoutId, DataValidity.VALID.getState());
            if (corporateMentorsInfoList.size()>0) {
                CorporateMentorsInfo corporateMentorsInfo = corporateMentorsInfoList.get(0);
                List<TrainingGroupInfoDTO> trainingGroupInfoDTOList = trainingGroupService.getGroupAllInfoBycorporateMentorsId(corporateMentorsInfo.getId());
                data.setId(corporateMentorsInfo.getId());
                data.setOrgId(corporateMentorsInfo.getOrgId());
                data.setLoginName(corporateMentorsInfo.getLoginName());
                data.setDepartment(corporateMentorsInfo.getDepartment());
                data.setPosition(corporateMentorsInfo.getPosition());
                data.setAccountId(corporateMentorsInfo.getAccountId());
                data.setJobNumber(corporateMentorsInfo.getJobNumber());
                data.setEnterpriseName(corporateMentorsInfo.getEnterpriseName());
                data.setCompanyAddress(corporateMentorsInfo.getCompanyAddress());
                data.setMailbox(corporateMentorsInfo.getMailbox());
                data.setPhone(corporateMentorsInfo.getPhone());
                data.setProvince(corporateMentorsInfo.getProvince());
                data.setCity(corporateMentorsInfo.getCity());
                if (trainingGroupInfoDTOList.size()>0) {
                    TrainingGroupInfoDTO trainingGroupInfoDTO = trainingGroupInfoDTOList.get(0);
                    data.setTeacherId(trainingGroupInfoDTO.getTeacherId());
                    data.setTeacherName(trainingGroupInfoDTO.getTeacherName());
                    data.setTeacherJobNumer(trainingGroupInfoDTO.getJobNumber());
                    data.setCollegeName(trainingGroupInfoDTO.getCollegeName());
                    data.setTrainingGroupId(trainingGroupInfoDTO.getId());
                    data.setTrainingGroupName(trainingGroupInfoDTO.getGropName());
                    data.setStarDate(trainingGroupInfoDTO.getStartDate());
                    data.setEndDate(trainingGroupInfoDTO.getEndDate());
                    Page<StudentDTO> studentDTOs = groupRelationService.findStudentsByGroupIdOrName(pageable, trainingGroupInfoDTO.getId(), name);
                    if (null != studentDTOs && studentDTOs.getContent().size() > 0) {
                        PageData<StudentDTO> pageData = new PageData<>();
                        pageData.setData(studentDTOs.getContent());
                        pageData.getPage().setTotalPages(studentDTOs.getTotalPages());
                        pageData.getPage().setPageNumber(pageable.getPageNumber());
                        pageData.getPage().setPageSize(pageable.getPageSize());
                        pageData.getPage().setTotalElements(studentDTOs.getTotalElements());
                        data.setStudentDTOList(pageData);
                    }
                }
            }
        }
        return data;
    }
    
    public PageData<StudentDTO> findByGroupIdAndName(Pageable pageable, Long groupId, String name){
    	 Page<StudentDTO> studentDTOs = groupRelationService.findStudentsByGroupIdOrName(pageable, groupId, name);
    	 PageData<StudentDTO> pageData = new PageData<>();
    	 if (null != studentDTOs && studentDTOs.getContent().size() > 0) {
    		 String ids = "";
    		 for(StudentDTO stu : studentDTOs.getContent()){
    			 if(StringUtils.isEmpty(ids)){
    				 ids = ""+stu.getId();
    			 }else{
    				 ids += ","+stu.getId();
    			 }
    		 }
    		 HashMap<Long, AccountDTO> avatarMap = getavatarUsersInfo(ids);
    		 for(StudentDTO stu : studentDTOs.getContent()){
    			 AccountDTO account = avatarMap.get(stu.getId());
    			 if(null != account){
    				 stu.setAvatar(account.getAvatar());
    			 }
    		 }
    		 
             pageData.setData(studentDTOs.getContent());
             pageData.getPage().setTotalPages(studentDTOs.getTotalPages());
             pageData.getPage().setPageNumber(pageable.getPageNumber());
             pageData.getPage().setPageSize(pageable.getPageSize());
             pageData.getPage().setTotalElements(studentDTOs.getTotalElements());
         }
         return pageData;
    }
    
    public HashMap<Long, AccountDTO> getavatarUsersInfo(String ids) {

  		RestTemplate rest = new RestTemplate();
  		HashMap<Long, AccountDTO> map = new HashMap<Long, AccountDTO>();
  		try {
  			String str = rest.getForObject(zhixinApi + "/api/account/userAvatarlist"
  					+ "?ids=" + ids, String.class);
  			JSONObject jsonObject = new JSONObject(str);
  			Iterator<?> iterator = jsonObject.keys();
  			while (iterator.hasNext()) {
  				String key = (String) iterator.next();
  				String value = jsonObject.get(key).toString();
  				value = value.replaceAll("null", "\"\"");
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

    public List<TrainingGroupInfoDTO> queryGroupListByAccoutId(Long accountId){
        List<TrainingGroupInfoDTO> trainingGroupList = null;
        if (null != accountId) {
            List<CorporateMentorsInfo> corporateMentorsInfoList = mentorsTrainingRepository.findByAccountIdAndDeleteFlag(accountId, DataValidity.VALID.getState());
            if (corporateMentorsInfoList.size() > 0) {
                CorporateMentorsInfo corporateMentorsInfo = corporateMentorsInfoList.get(0);
                trainingGroupList = trainingGroupService.getGroupAllInfoBycorporateMentorsId(corporateMentorsInfo.getId());
            }
        }
        return trainingGroupList;
    }


    public List<LineCodeNameBaseDomain> importCorporateMentorData(Long orgId,MultipartFile file){
        if (null == orgId || orgId <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "orgId是必须的");
        }
        List<CorporateMentorExcelDomain> excelDatas = excelBasedataHelper.readCorporateMentorFromInputStream(file);
        if (null == excelDatas || excelDatas.size() <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "没有读取到任何数据");
        }
        Set<String>  mailboxSet = new HashSet<>();
        Set<String> phoneSet = new HashSet<>();
        List<CorporateMentorsInfo> dataForAdd = new ArrayList<>();//新增部分
        String msg = null;
        boolean hasError = false;
        List<LineCodeNameBaseDomain> errorInfos = new ArrayList<>();
        for (CorporateMentorExcelDomain d : excelDatas) {
            LineCodeNameBaseDomain error = new LineCodeNameBaseDomain();
            if (!StringUtils.isEmpty(d.getMailbox())) {
                if (mailboxSet.contains(d.getMailbox())) {
                    error.setLine(d.getLine());
                    error.setName(error.getName() + " 邮箱");
                    error.setMsg(error.getMsg() + " " + d.getMailbox() + "邮箱在此Excel中存在重复");
                    hasError = true;
                }
                mailboxSet.add(d.getMailbox());
            }
            if (!StringUtils.isEmpty(d.getPhone())) {
                if (mailboxSet.contains(d.getPhone())) {
                    error.setLine(d.getLine());
                    error.setName(error.getName() + " 手机号码");
                    error.setMsg(error.getMsg() + " " + d.getPhone() + "手机号码在此Excel中存在重复");
                    hasError = true;
                }
                phoneSet.add(d.getPhone());
            }
            if(null != error.getLine()) {
                errorInfos.add(error);
            }
        }
        if(hasError){
            return errorInfos;
        }else {
            List<CorporateMentorsInfo> phoneList = mentorsTrainingRepository.findByPhoneListAndDeleteFlag(new ArrayList(phoneSet), DataValidity.VALID.getState());
            List<CorporateMentorsInfo> mailboxList = mentorsTrainingRepository.findByMailboxListAndDeleteFlag(new ArrayList(mailboxSet), DataValidity.VALID.getState());
            Set<String> existPhoneSet = new HashSet<>();
            Set<String> existMailboxSet = new HashSet<>();
            if(phoneList.size()>0){
                for(CorporateMentorsInfo corporateMentorsInfo : phoneList){
                    existPhoneSet.add(corporateMentorsInfo.getPhone());
                }
            }
            if(mailboxList.size()>0){
                for(CorporateMentorsInfo corporateMentorsInfo : mailboxList){
                    existMailboxSet.add(corporateMentorsInfo.getMailbox());
                }
            }

            List<BatchAddUserDomain> batchAddUserDomainList = new ArrayList<>();
            Set<String> loginSet = new HashSet<>();
            for (CorporateMentorExcelDomain d : excelDatas) {
                LineCodeNameBaseDomain error = new LineCodeNameBaseDomain();
                if((phoneList.size() > 0 && existPhoneSet.contains(d.getPhone()))) {
                    error.setLine(d.getLine());
                    error.setName(error.getName() + " 手机号码");
                    error.setMsg(error.getMsg() + " " + d.getPhone() + " 手机号已被注册！");
                    hasError = true;
                }
                if(mailboxList.size()>0 && existMailboxSet.contains(d.getMailbox())){
                    error.setLine(d.getLine());
                    error.setName(error.getName() + " 邮箱");
                    error.setMsg(error.getMsg() + " " + d.getMailbox() + " 邮箱已被注册！");
                    hasError = true;
                }
                if(null != error.getLine()) {
                    errorInfos.add(error);
                }
                BatchAddUserDomain batchAddUserDomain = new BatchAddUserDomain();
                String time =  String.valueOf(System.currentTimeMillis());
                String uuid = UUID.randomUUID().toString();
                String str = uuid.substring(0,3)+time.substring(time.length()-3,time.length());
                String accountNo = "qygl" + str;
                batchAddUserDomain.setLogin(accountNo);
                batchAddUserDomain.setRoleGroup("COM");
                batchAddUserDomain.setUserName(d.getName());
                batchAddUserDomainList.add(batchAddUserDomain);
                loginSet.add(accountNo);
               /**********************************************************************/
                CorporateMentorsInfo corporateMentorsInfo = new CorporateMentorsInfo();
                corporateMentorsInfo.setName(d.getName());
                corporateMentorsInfo.setJobNumber(d.getJobNumber());
                corporateMentorsInfo.setEnterpriseName(d.getEnterpriseName());
                corporateMentorsInfo.setCompanyAddress(d.getCompanyAddress());
                corporateMentorsInfo.setDepartment(d.getDepartment());
                corporateMentorsInfo.setPosition(d.getPosition());
                corporateMentorsInfo.setMailbox(d.getMailbox());
                corporateMentorsInfo.setPhone(d.getPhone());
                corporateMentorsInfo.setOrgId(orgId);
                corporateMentorsInfo.setLoginName(accountNo);
                dataForAdd.add(corporateMentorsInfo);
            }
            List<String> accountList = userService.batchQueryAccount(loginSet);
            if(accountList.size()>0){
                throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "创建账号重复错误！");
            }
            if(hasError) {
               return errorInfos;
            }else {
                List<BatchAddUserResultDomain> reAccountList = userService.batchAddAccount(batchAddUserDomainList);
                if (reAccountList.size() > 0) {
                    for (int i = 0; i < dataForAdd.size(); i++) {
                        for (int j = 0; j < reAccountList.size(); j++) {
                            if (dataForAdd.get(i).getLoginName().equals(reAccountList.get(j).getLogin())) {
                                dataForAdd.get(i).setAccountId(reAccountList.get(j).getId());
                                break;
                            }
                        }
                    }
                    mentorsTrainingRepository.save(dataForAdd);
                }
            }
        }
        return errorInfos;
    }



}
