package com.aizhixin.cloud.orgmanager.training.service;

import com.aizhixin.cloud.orgmanager.common.PageData;
import com.aizhixin.cloud.orgmanager.common.core.DataValidity;
import com.aizhixin.cloud.orgmanager.common.domain.CounRollcallGroupPracticeDTO;
import com.aizhixin.cloud.orgmanager.common.domain.MessageDTOV2;
import com.aizhixin.cloud.orgmanager.company.service.UserService;
import com.aizhixin.cloud.orgmanager.training.dto.CorporateMentorsInfoByEnterpriseDTO;
import com.aizhixin.cloud.orgmanager.training.dto.CorporateMentorsInfoPageByEnterpriseDTO;
import com.aizhixin.cloud.orgmanager.training.dto.StudentDTO;
import com.aizhixin.cloud.orgmanager.training.dto.TrainingGropDTO;
import com.aizhixin.cloud.orgmanager.training.dto.TrainingGropSetDTO;
import com.aizhixin.cloud.orgmanager.training.dto.TrainingGroupInfoDTO;
import com.aizhixin.cloud.orgmanager.training.entity.Enterprise;
import com.aizhixin.cloud.orgmanager.training.entity.TrainingGroup;
import com.aizhixin.cloud.orgmanager.training.entity.TrainingGroupSet;
import com.aizhixin.cloud.orgmanager.training.repository.TrainingGroupRepository;
import com.aizhixin.cloud.orgmanager.training.service.TrainingManageService.UpdateCallGroupThread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Created by jianwei.wu
 * @E-mail wujianwei@aizhixin.com
 */
@Service
@Transactional
public class TrainingGroupService {
	
	private static Logger log = LoggerFactory
			.getLogger(TrainingGroupService.class);
	
    @Autowired
    private TrainingGroupRepository trainingGroupRepository;
    @Autowired
    private GroupRelationService groupRelationService;
    @Autowired
    private TrainingGroupSetService trainingGroupSetService;
    @Autowired
    @Lazy
    private RedisDataService redisDataService;
    @Autowired
    private UserService userService;
    @Autowired
    private EnterpriseService enterpriseService;

    /**
     * 保存实训小组的信息
     * @param trainingGroup
     * @return
     */
    public TrainingGroup save(TrainingGroup trainingGroup){
        return trainingGroupRepository.save(trainingGroup);
    }


    /**
     * 根据id 获取实训小组的信息
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public TrainingGroup getGroupInfoById(Long id){
        return trainingGroupRepository.findOne(id);
    }
    /**
     * 根据企业导师id查询实训小组的信息
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public List<TrainingGroupInfoDTO> getGroupAllInfoBycorporateMentorsId(Long id){
        return trainingGroupRepository.findTrainingGroupInfoByCid(id, new Date(), DataValidity.VALID.getState());
    }

    /**
     * 组织id查询实训小组的信息
     * @param
     * @return
     */
    @Transactional(readOnly = true)
    public  List<TrainingGroupInfoDTO> findAllByOrgId(Long orgId){
        return trainingGroupRepository.findAllByOrgIdAndDeleteFlag(orgId, DataValidity.VALID.getState());
    }

    /**
     * 根据学校老师id查询学校老师当前所在的实训小组id
     * @param teacherId
     * @return
     */
    @Transactional(readOnly = true)
    public List<Long> findTrainingGroupIdByTeacherId(Long teacherId){
        return trainingGroupRepository.findTrainingGroupIdByTeacherId(DataValidity.VALID.getState(), teacherId, new Date());
    }
    
    /**
     * 根据学校老师id查询学校老师当前所在的实训参与计划id和名称
     * @param teacherId
     * @return
     */
    @Transactional(readOnly = true)
    public List<TrainingGropDTO> findTrainingGroupByTeacherId(Long teacherId){
        return trainingGroupRepository.findTrainingGroupByTeacherId(DataValidity.VALID.getState(), teacherId, new Date());
    }
    
    
    @Transactional(readOnly = true)
    public Page<TrainingGroupInfoDTO> findGroupInforByTeacherId(Pageable pageable,Long teacherId){
        return trainingGroupRepository.findGroupInforByTeacherId(pageable,DataValidity.VALID.getState(), teacherId, new Date());
    }
    
    @Transactional(readOnly = true)
    public Page<TrainingGroupInfoDTO> findEndGroupInforByTeacherId(Pageable pageable,Long teacherId){
        return trainingGroupRepository.findEndGroupInforByTeacherId(pageable,DataValidity.VALID.getState(), teacherId, new Date());
    }
    
    /**
     * 实训小组列表信息
     * @return
     */
    @Transactional(readOnly = true)
    public Page<TrainingGroupInfoDTO> getAllGroupInfo(Pageable pageable, Long orgId){
        return trainingGroupRepository.queryGroupList(pageable, DataValidity.VALID.getState(), orgId,new Date());
    }
    /**
     * 根据名字查询实训小组列表信息
     * @return
     */
    @Transactional(readOnly = true)
    public Page<TrainingGroupInfoDTO> getAllGroupInfoByName(Pageable pageable, String name, Long orgId){
        return trainingGroupRepository.queryGroupListByName(pageable, DataValidity.VALID.getState(), name, orgId, new Date());
    }
    /**
     * 实训小组已结束列表信息
     * @return
     */
    @Transactional(readOnly = true)
    public Page<TrainingGroupInfoDTO> getAllGroupInfoEnd(Pageable pageable, Long orgId){
        return trainingGroupRepository.queryGroupListEnd(pageable, DataValidity.VALID.getState(), orgId, new Date());
    }
    /**
     * 根据名字查询实训小组已结束列表信息
     * @return
     */
    @Transactional(readOnly = true)
    public Page<TrainingGroupInfoDTO> getAllGroupInfoByNameEnd(Pageable pageable, String name, Long orgId){
        return trainingGroupRepository.queryGroupListByNameEnd(pageable, DataValidity.VALID.getState(), name, orgId,new Date());
    }
    
    /**
     * 实训小组全部列表信息
     * @return
     */
    @Transactional(readOnly = true)
    public Page<TrainingGroupInfoDTO> getAllGroupInfoAll(Pageable pageable, Long orgId){
        return trainingGroupRepository.queryGroupListAll(pageable, DataValidity.VALID.getState(), orgId);
    }
    /**
     * 根据名字查询实训小组全部列表信息
     * @return
     */
    @Transactional(readOnly = true)
    public Page<TrainingGroupInfoDTO> getAllGroupInfoByNameAll(Pageable pageable, String name, Long orgId){
        return trainingGroupRepository.queryGroupListByNameAll(pageable, DataValidity.VALID.getState(), name, orgId);
    }
    
    /**
     * 删除实训小组
     * @param id
     * @return
     */
    @Transactional
    public Map<String,Object> deleteGroup(Long id){
        Map<String,Object> result = new HashMap<>();
        try {
        	TrainingGroup group = trainingGroupRepository.findOne(id);
        	if(null != group.getTeacher()){
        		redisDataService.delCounselorGroupInfor(group.getTeacher().getId());
        	}
            trainingGroupRepository.delete(id);
            //物理删除实训小组下的所有学生的关系
            groupRelationService.dedeleteByGroupId(id);
            
            List<StudentDTO> stuList = groupRelationService.findStudentsByGroupId(id);
            List<Long> stuIds = new ArrayList<Long>();
            for(StudentDTO stu : stuList){
            	stuIds.add(stu.getId());
            }
            
            if(null != group.getTeacher()){
            	delCallGroup(group.getId());
            }
            
            try{
	            MessageDTOV2 messageDTO = new MessageDTOV2();
	            messageDTO.setAudience(stuIds);
	            messageDTO.setType(20);
	            messageDTO.setIsDel(true);
	            userService.pushMsg(messageDTO);
	        }catch (Exception e) {
				log.error("下发实训计划删除消息失败！", e);
			}
            
            result.put("success", true);
            result.put("message", "实训计划删除成功");
            return result;
        }catch (Exception e){
            result.put("success",false);
            result.put("message","实训计划删除失败");
            log.error("实训计划创建失败！", e);
            return result;
        }
    }

    public void delCallGroup(Long groupId) {

		DelCallGroupThread thread = new DelCallGroupThread(groupId);
		thread.start();
	}

	class DelCallGroupThread extends Thread {
		private Long groupId;
		public DelCallGroupThread(Long groupId) {
			this.groupId = groupId;
		}

		public void run() {
			userService.delCounsellorGroup(groupId);
		}
	}
    
    public CorporateMentorsInfoByEnterpriseDTO queryGroupInfoById(Long groupId){
    	CorporateMentorsInfoByEnterpriseDTO data = new CorporateMentorsInfoByEnterpriseDTO();
    	TrainingGroup group = getGroupInfoById(groupId);
    	if(null != group){
    		
    		 data.setTrainingGroupName(group.getGropName());
    		 data.setStarDate(group.getStartDate());
             data.setEndDate(group.getEndDate());
    		
    		//企业导师信息
    		if(null != group.getCorporateMentorsInfo()){
		    	data.setLoginName(group.getCorporateMentorsInfo().getLoginName());
		    	data.setName(group.getCorporateMentorsInfo().getName());
		    	data.setEnterpriseName(group.getCorporateMentorsInfo().getEnterpriseName());
		    	data.setDepartment(group.getCorporateMentorsInfo().getDepartment());
		    	data.setPosition(group.getCorporateMentorsInfo().getPosition());
		    	data.setPhone(group.getCorporateMentorsInfo().getPhone());
		    	data.setMailbox(group.getCorporateMentorsInfo().getMailbox());
    		}
	    	
	    	//辅导员信息
    		if(null != group.getTeacher()){
		    	data.setTeacherJobNumer(group.getTeacher().getJobNumber());
		    	data.setTeacherName(group.getTeacher().getName());
		    	data.setCollegeName(group.getTeacher().getCollege().getName());
		    	data.setTeacherSex(group.getTeacher().getSex());
		    	data.setTeacherPhone(group.getTeacher().getPhone());
    		}
	    	
	    	//学生信息
    		 List<StudentDTO> studentDTOs = groupRelationService.findStudentsByGroupId(groupId);
             if (null != studentDTOs && studentDTOs.size() > 0) {
                 data.setStudentDTOList(studentDTOs);
             }
             
             //设置信息
             TrainingGroupSet set = trainingGroupSetService.findByGroupId(groupId);
             if(null != set){
            	 TrainingGropSetDTO setDTO = new TrainingGropSetDTO();
            	 BeanUtils.copyProperties(set, setDTO);
            	 data.setSetDTO(setDTO);
             }
    	}
    	return data;
    }
    
    public List<TrainingGroupInfoDTO> queryGroupListByOrgId(Long orgId,Date eDateTime){
    	return trainingGroupRepository.queryGroupListByOrgId(orgId, eDateTime);
    }
    
    public List<TrainingGroupInfoDTO> queryGroupListAll(Date eDateTime){
    	return trainingGroupRepository.queryGroupListAll(eDateTime);
    }
    
    public void initMsg(Long orgId){
    	List<TrainingGroupInfoDTO> list = null;
    	if(null == orgId){
    		list = queryGroupListAll(new Date());
    	}else{
    		list = queryGroupListByOrgId(orgId,new Date());
    	}
    	for(TrainingGroupInfoDTO dto : list){
    		 List<StudentDTO> stuList = groupRelationService.findStudentsByGroupId(dto.getId());
             List<Long> stuIds = new ArrayList<Long>();
             for(StudentDTO stu : stuList){
             	stuIds.add(stu.getId());
             }
             MessageDTOV2 messageDTO = new MessageDTOV2();
             messageDTO.setAudience(stuIds);
             messageDTO.setData(dto);
             messageDTO.setType(20);
             userService.pushMsg(messageDTO);
    	}
    }

    
    
    public TrainingGroupInfoDTO findGroupInforById(Long id){
    	return trainingGroupRepository.findGroupInforById(DataValidity.VALID.getState(), id);
    }
    
    public CorporateMentorsInfoByEnterpriseDTO getGroupById(Long groupId){
    	CorporateMentorsInfoByEnterpriseDTO data = new CorporateMentorsInfoByEnterpriseDTO();
    	TrainingGroup group = getGroupInfoById(groupId);
    	if(null != group){
    		
    		 data.setTrainingGroupName(group.getGropName());
    		 data.setStarDate(group.getStartDate());
             data.setEndDate(group.getEndDate());
    		
    		//企业导师信息
    		if(null != group.getCorporateMentorsInfo()){
		    	data.setLoginName(group.getCorporateMentorsInfo().getLoginName());
		    	data.setName(group.getCorporateMentorsInfo().getName());
		    	data.setDepartment(group.getCorporateMentorsInfo().getDepartment());
		    	data.setPosition(group.getCorporateMentorsInfo().getPosition());
		    	data.setPhone(group.getCorporateMentorsInfo().getPhone());
		    	data.setMailbox(group.getCorporateMentorsInfo().getMailbox());
		    	Long enterpriseId = group.getCorporateMentorsInfo().getEnterpriseId();
		    	Enterprise enterprise = enterpriseService.findById(enterpriseId);
		    	if(null != enterprise){
		    		data.setEnterpriseName(enterprise.getName());
		    		data.setCompanyAddress(enterprise.getAddress());
		    	}
    		}
	    	
    	}
    	return data;
    }
    
    public  List<TrainingGropDTO> getGroupListByTeacherId(Long teacherId){
    	return trainingGroupRepository.findTrainingGroupAllByTeacherId(DataValidity.VALID.getState(), teacherId);
    }
    
    public  List<TrainingGroupInfoDTO> queryGroupListByName(Long orgId,String name,Set<Long> ids){
    	return trainingGroupRepository.queryGroupListByName(DataValidity.VALID.getState(), name, new Date(), ids,orgId);
    }
    
    public  List<TrainingGroupInfoDTO> queryGroupList(Long orgId,Set<Long> ids){
    	return trainingGroupRepository.queryGroupList(DataValidity.VALID.getState(), new Date(), ids,orgId);
    }
    
    public  List<TrainingGroupInfoDTO> queryGroupListByName(Long orgId,String name){
    	return trainingGroupRepository.queryGroupListByName(DataValidity.VALID.getState(), name, new Date(),orgId);
    }
    
    public  List<TrainingGroupInfoDTO> queryGroupList(Long orgId){
    	return trainingGroupRepository.queryGroupList(DataValidity.VALID.getState(), new Date(),orgId);
    }
}
