package com.aizhixin.cloud.dd.dorms.thread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.aizhixin.cloud.dd.common.core.DataValidity;
import com.aizhixin.cloud.dd.dorms.entity.Room;
import com.aizhixin.cloud.dd.dorms.entity.RoomAssgin;
import com.aizhixin.cloud.dd.dorms.jdbcTemplate.RoomAssginJdbc;
import com.aizhixin.cloud.dd.dorms.repository.BedRepository;
import com.aizhixin.cloud.dd.dorms.repository.RoomAssginRepository;
import com.aizhixin.cloud.dd.dorms.repository.RoomRepository;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.service.DDUserService;
import com.aizhixin.cloud.dd.rollcall.service.SmsService;

public class SendSmsRunnable implements Runnable {
	private Logger log = LoggerFactory.getLogger(SendSmsRunnable.class);
	private RoomAssginRepository roomAssginRepository;
	private BedRepository bedRepository;
	private Long roomId;
	private Integer sexType;
	private SmsService smsService;
	private RoomRepository roomRepository;
	private DDUserService ddUserService;
	private RoomAssginJdbc roomAssginJdbc;

	public SendSmsRunnable(DDUserService ddUserService, RoomRepository roomRepository,
			RoomAssginRepository roomAssginRepository, BedRepository bedRepository, Long roomId, Integer sexType,
			SmsService smsService, RoomAssginJdbc roomAssginJdbc) {
		this.roomAssginRepository = roomAssginRepository;
		this.bedRepository = bedRepository;
		this.roomId = roomId;
		this.sexType = sexType;
		this.smsService = smsService;
		this.roomRepository = roomRepository;
		this.ddUserService = ddUserService;
		this.roomAssginJdbc = roomAssginJdbc;
	}

	@Override
	public void run() {

		log.info("获取房间分配专业信息-----------");
		String context = "宿舍分配提醒：您分配给XXXX的SSS宿舍即将满员，请及时分配新的宿舍";
		List<RoomAssgin> rl = roomAssginRepository.findByRoomIdAndDeleteFlag(roomId, DataValidity.VALID.getState());
		if (null != rl && 0 < rl.size()) {
			List<Long> profIds = new ArrayList<>();
			Map<Long, String> mapData = new HashMap<>();
			for (RoomAssgin roomAssgin : rl) {
				profIds.add(roomAssgin.getProfId());
				mapData.put(roomAssgin.getProfId(), roomAssgin.getProfName());
			}
			if (!profIds.isEmpty()) {
				log.info("获取专业分配信息-----------");
				// List<RoomAssgin> ral =
				// roomAssginRepository.findByprofIdInAndDeleteFlagAndSexType(profIds,
				// DataValidity.VALID.getState(), sexType);
				List<Long> roomIds = roomAssginJdbc.findProfInfo(profIds);
				if (null != roomIds && 0 < roomIds.size()) {
					Long bedCount = bedRepository.countByRoomIdInAndDeleteFlag(roomIds, DataValidity.VALID.getState());
					Long liveBedCount = bedRepository.countByRoomIdInAndLiveAndDeleteFlag(roomIds, true,
							DataValidity.VALID.getState());
					if ((null != liveBedCount && 0L < liveBedCount) && (null != bedCount && 0L < bedCount)) {
						log.info("总床位数："+bedCount+"-----住的人数："+liveBedCount);
						Double z = Double.parseDouble(liveBedCount + "") / Double.parseDouble(bedCount + "");
						if (z >= 0.8) {
							String profNames = "";
							int i = 0;
							for (Long profId : mapData.keySet()) {
								if (i != mapData.size() - 1) {
									profNames += mapData.get(profId) + "、";
								} else {
									profNames += mapData.get(profId);
								}
								i++;
							}
							context=context.replaceAll("XXXX", profNames);
							if (sexType == 10) {
								context=context.replaceAll("SSS", "男");
							}
							if (sexType == 20) {
								context=context.replaceAll("SSS", "女");
							}
							Room r = roomRepository.findByIdAndDeleteFlag(roomId, DataValidity.VALID.getState());
							if (null != r) {
								Long userId = r.getCreatedBy();
								List<Long> ids = new ArrayList<>();
								ids.add(userId);
								Map<Long, AccountDTO> map = ddUserService.getUserinfoByIdsV2(ids);
								AccountDTO a = map.get(userId);
								if (null != a && !StringUtils.isEmpty(a.getPhoneNumber())) {
									smsService.sendSms(a.getPhoneNumber(), context);
									log.info("给" + a.getName() + "发送短信号码为：" + a.getPhoneNumber());
								}
							}
						}
					}
				}

			}
		}

	}

}
