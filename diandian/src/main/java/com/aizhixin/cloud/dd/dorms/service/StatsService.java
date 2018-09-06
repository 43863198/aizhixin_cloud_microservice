package com.aizhixin.cloud.dd.dorms.service;

import com.aizhixin.cloud.dd.common.core.PageUtil;
import com.aizhixin.cloud.dd.common.domain.PageData;
import com.aizhixin.cloud.dd.common.domain.PageDomain;
import com.aizhixin.cloud.dd.dorms.domain.ProfStatsDomain;
import com.aizhixin.cloud.dd.dorms.domain.StuStatsDomain;
import com.aizhixin.cloud.dd.dorms.entity.BedStu;
import com.aizhixin.cloud.dd.dorms.jdbcTemplate.RoomAssginJdbc;
import com.aizhixin.cloud.dd.dorms.repository.BedStuRepository;
import com.aizhixin.cloud.dd.orgStructure.repository.ProfRepository;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hsh
 */
@Service
@Transactional
public class StatsService {

    @Autowired
    private BedStuRepository bedStuRepository;

    @Autowired
    private RoomAssginJdbc roomAssginJdbc;

    @Autowired
    private ProfRepository profRepository;

    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteService;

    public void syncBedStu(Long orgId) {
        List<BedStu> list = roomAssginJdbc.findBedStuByOrgIdAndNoName(orgId);
        if (list != null && list.size() > 0) {
            //查询无电话
            list = roomAssginJdbc.findBedStuByOrgIdAndNoPhone(orgId);
        }
        if (list != null && list.size() > 0) {
            Map<String, Object> map = orgManagerRemoteService.getNewStudentList(orgId, null, null, null, null, 1, 10000);
            Map<Long, Object> stuMap = new HashMap<>();
            if (map != null && map.get("data") != null) {
                List stuList = (List) map.get("data");
                if (stuList != null && stuList.size() > 0) {
                    int count = stuList.size();
                    for (int i = 0; i < count; i++) {
                        Map<String, Object> item = (Map<String, Object>) stuList.get(i);
                        if (item != null) {
                            Long stuId = Long.parseLong(item.get("id").toString());
                            stuMap.put(stuId, item);
                        }
                    }
                }
            }
            for (BedStu item : list) {
                if (item != null) {
                    Map<String, Object> userMap = (Map<String, Object>) stuMap.get(item.getStuId());
                    if (userMap != null) {
                        if (userMap.get("name") != null) {
                            item.setStuName(String.valueOf(userMap.get("name")));
                        }
                        if (userMap.get("sex") != null) {
                            item.setGender(String.valueOf(userMap.get("sex")));
                        }
                        if (userMap.get("idNumber") != null) {
                            item.setIdNumber(String.valueOf(userMap.get("idNumber")));
                        }
                        if (userMap.get("phone") != null) {
                            item.setPhone(String.valueOf(userMap.get("phone")));
                        }
                        if (userMap.get("professionalId") != null) {
                            item.setProfId(Long.parseLong(userMap.get("professionalId").toString()));
                        } else {
                            Map<String, Object> info = orgManagerRemoteService.getNewStudentInfo(item.getIdNumber(), item.getStuName());
                            if (info != null) {
                                if(info.get("professionalId") != null){
                                    item.setProfId(Long.parseLong(info.get("professionalId").toString()));
                                }
                                if (info.get("phone") != null) {
                                    item.setPhone(String.valueOf(info.get("phone")));
                                }
                            }
                        }
                        if (userMap.get("professionalName") != null) {
                            item.setProfName(String.valueOf(userMap.get("professionalName")));
                        }
                    }
                }
            }
            bedStuRepository.save(list);
        }
    }

    public PageData<StuStatsDomain> getStuList(Pageable pageable, Long orgId, String name, Long professionalId, String gender) {
        PageData<StuStatsDomain> page = new PageData<StuStatsDomain>();
        List<StuStatsDomain> list = roomAssginJdbc.findBedStuByProfIdAndSexTypeAndName(pageable, orgId, professionalId, gender, name);
        page.setData(list);
        PageDomain d = new PageDomain();
        d.setPageNumber(pageable.getPageNumber());
        d.setPageSize(pageable.getPageSize());
        d.setTotalElements(roomAssginJdbc.countBedStuByProfIdAndSexTypeAndName(orgId, professionalId, gender, name));
        d.setTotalPages(PageUtil.cacalatePagesize(d.getTotalElements(), d.getPageSize()));
        page.setPage(d);
        return page;
    }

    public List<ProfStatsDomain> getProfStats(Long orgId, Long professionalId) {
        List<Map<String, Object>> profList = profList = roomAssginJdbc.findProfInfoByOrgIdAndProfessionalId(orgId, professionalId);
        List<ProfStatsDomain> result = new ArrayList<>();
        DecimalFormat df = new DecimalFormat("0.00");
        if (profList != null && profList.size() > 0) {
            for (Map map : profList) {
                Long profId = Long.parseLong(map.get("profId").toString());
                ProfStatsDomain d = new ProfStatsDomain();
                d.setProfName(map.get("profName").toString());
                d.setProfId(profId);
                d.setBedNum(roomAssginJdbc.countBedByProfId(profId));
                d.setSelectedBedNum(roomAssginJdbc.countStuBedByProfId(profId));
                float ff = (float) d.getSelectedBedNum() / (float) d.getBedNum() * 100;
                if(Float.isNaN(ff)){
                    ff = 0.00f;
                }
                d.setSelectedPct(df.format(ff));
                d.setProfStuNum(getNewStuNumByProfId(orgId, profId));
                result.add(d);
            }
        }
        return result;
    }

    private Long getNewStuNumByProfId(Long orgId, Long profId) {
        Map<String, Object> map = orgManagerRemoteService.getNewStudentList(orgId, null, profId, null, null, 1, 1);
        if (map != null && map.get("page") != null) {
            Map page = (Map) map.get("page");
            if (page != null && page.get("totalElements") != null) {
                String count = page.get("totalElements").toString();
                return Long.parseLong(count);
            }
        }
        return 0L;
    }
}
