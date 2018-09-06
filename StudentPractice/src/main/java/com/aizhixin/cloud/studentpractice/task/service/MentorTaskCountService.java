package com.aizhixin.cloud.studentpractice.task.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aizhixin.cloud.studentpractice.common.domain.IdNameDomain;
import com.aizhixin.cloud.studentpractice.common.service.QueryService;
import com.aizhixin.cloud.studentpractice.common.util.PageJdbcUtil;
import com.aizhixin.cloud.studentpractice.task.core.TaskStatusCode;
import com.aizhixin.cloud.studentpractice.task.domain.TaskCountDomain;
import com.aizhixin.cloud.studentpractice.task.entity.File;
import com.aizhixin.cloud.studentpractice.task.entity.MentorTaskCount;
import com.aizhixin.cloud.studentpractice.task.entity.TaskFile;
import com.aizhixin.cloud.studentpractice.task.repository.FileRepository;
import com.aizhixin.cloud.studentpractice.task.repository.MentorTaskCountRepository;

@Transactional
@Service
public class MentorTaskCountService {
	
	@Autowired
	private PageJdbcUtil pageJdbcUtil;
	@Autowired
	private MentorTaskCountRepository mentorTaskCountRepository;
	@Autowired
	@Lazy
	private QueryService queryService;
	
	public void saveList(List<MentorTaskCount> list){
		mentorTaskCountRepository.save(list);
	}
	
	public List<MentorTaskCount> findAllByMentorTaskIds(Set<String> mentorTaskIds){
		return mentorTaskCountRepository.findAllByMentorTaskIds(mentorTaskIds);
	}
	
	public HashMap<String,MentorTaskCount> findByMentorTaskIds(Set<String> mentorTaskIds){
		
		List<MentorTaskCount> countList = findAllByMentorTaskIds(mentorTaskIds);
		HashMap<String,MentorTaskCount> map = new HashMap<String,MentorTaskCount>();
		if(null != countList && !countList.isEmpty()){
			for(MentorTaskCount countDomain:countList){
				map.put(countDomain.getMentorTaskId(), countDomain);
			}
		}
		return map;
	}
	
	RowMapper<TaskCountDomain> countMentorRm = new RowMapper<TaskCountDomain>() {

		@Override
		public TaskCountDomain mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			// TODO Auto-generated method stub
			TaskCountDomain domain = new TaskCountDomain();
			domain.setTaskCountNum(rs.getLong("count_num"));
			domain.setTaskStatus(rs.getString("STUDENT_TASK_STATUS"));
			domain.setMentorTaskId(rs.getString("MENTOR_TASK_ID"));
			return domain;
		}
	};

	/**
	 * 统计导师的待审核和已完成任务数
	 * 
	 * @param userId
	 * @return
	 */
	public List<TaskCountDomain> countTaskNum() {
		
		List<IdNameDomain> groupList = queryService.getAllGroupValid();
		String groupIds = "";
		if(null != groupList && !groupList.isEmpty()){
			for(IdNameDomain domain : groupList){
				if(StringUtils.isBlank(groupIds)){
					groupIds = domain.getId().toString();
				}else{
					groupIds += ","+domain.getId();
				}
			}
			
			String sql = "SELECT count(1) as count_num,st.MENTOR_TASK_ID,st.STUDENT_TASK_STATUS FROM `sp_student_task` st  where DELETE_FLAG = 0 and st.STUDENT_TASK_STATUS in ('checkPending','finish') and group_id in("+groupIds+") GROUP BY st.MENTOR_TASK_ID,st.STUDENT_TASK_STATUS ORDER BY st.MENTOR_TASK_ID;";
			List<TaskCountDomain> list = pageJdbcUtil.getInfo(sql, countMentorRm);
			return list;
		}else{
			return null;
		}
	}
	
	
	@SuppressWarnings("null")
	public void saveCountTask(){
		List<TaskCountDomain> countList = countTaskNum();
		if(null != countList && !countList.isEmpty()){
			
			HashMap<String,MentorTaskCount> saveMap = new HashMap<String,MentorTaskCount>();
			for(TaskCountDomain domain : countList){
				MentorTaskCount mtaskCount = null;
				if(null == saveMap.get(domain.getMentorTaskId())){
					mtaskCount = new MentorTaskCount();
					mtaskCount.setId(domain.getMentorTaskId());
					mtaskCount.setMentorTaskId(domain.getMentorTaskId());
				}else{
					mtaskCount = saveMap.get(domain.getMentorTaskId());
				}
				
				if (TaskStatusCode.TASK_STATUS_CHECK_PENDING.equals(domain.getTaskStatus())) {
					mtaskCount.setCheckPendingNum(domain.getTaskCountNum());
				}else if (TaskStatusCode.TASK_STATUS_FINISH.equals(domain.getTaskStatus())) {
					mtaskCount.setFinishNum(domain.getTaskCountNum());
				}
				
				saveMap.put(domain.getMentorTaskId(), mtaskCount);
			}
			
			if(!saveMap.isEmpty()){
				List<MentorTaskCount> saveList = new ArrayList<MentorTaskCount>();
				Collection<MentorTaskCount> valuesList =saveMap.values();
				Iterator<MentorTaskCount> it = valuesList.iterator();  
		        while(it.hasNext()) {  
		        	saveList.add(it.next());  
		        }  
				this.saveList(saveList);
			}
		}
	}
}
