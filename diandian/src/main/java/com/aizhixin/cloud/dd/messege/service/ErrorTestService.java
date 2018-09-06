package com.aizhixin.cloud.dd.messege.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aizhixin.cloud.dd.messege.jdbcTemplate.MsgJdbc;
import com.aizhixin.cloud.dd.rollcall.JdbcTemplate.RevertJdbc;
import com.aizhixin.cloud.dd.rollcall.dto.RevertDTO;
import com.aizhixin.cloud.dd.rollcall.repository.PushMessageRepository;

@Service
@Transactional
public class ErrorTestService {
	@Autowired
	private MsgJdbc msjdbc;
	@Autowired
	private RevertJdbc revertJdbc;
	@Autowired
	private PushMessageRepository pushMessageRepository;
	
	public void chuli() {
		List<Long> userIds=msjdbc.findRevertMsg();
		if(null!=userIds&&0<userIds.size()) {
			for (Long userId : userIds) {
				RevertDTO rd=revertJdbc.findFirstByRevert(userId);
				if(null==rd) {
					//TODO 消息 删除 评论回复消息
					pushMessageRepository.deleteByUserIdAndModule(userId, "revert");
				}
			}
		}
			
	}

}
