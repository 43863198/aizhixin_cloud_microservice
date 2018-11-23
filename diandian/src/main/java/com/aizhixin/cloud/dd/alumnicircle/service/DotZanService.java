package com.aizhixin.cloud.dd.alumnicircle.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aizhixin.cloud.dd.alumnicircle.domain.DotZanDomain;
import com.aizhixin.cloud.dd.alumnicircle.entity.AlumniCircle;
import com.aizhixin.cloud.dd.alumnicircle.entity.DotZan;
import com.aizhixin.cloud.dd.alumnicircle.repository.DotZanRepository;
import com.aizhixin.cloud.dd.common.core.DataValidity;

@Service
public class DotZanService {

    @Autowired
    private DotZanRepository dotZanRepository;

    @Autowired
    private AlumniCircleService alumniCircleService;

    /**
     * @param alumniCircleId
     * @Title: countDotZan
     * @Description: 统计校友圈的点赞数
     * @return: Long
     */
    public Long countDotZan(Long alumniCircleId) {
        return dotZanRepository.countByAlumniCircleIdAndDeleteFlag(alumniCircleId, DataValidity.VALID.getState());
    }

    /**
     * @param dotZanDomain
     * @Title: save
     * @Description: 点赞保存
     * @return: DotZan
     */
    @Transactional
    public DotZan save(DotZanDomain dotZanDomain, AlumniCircle ac) {
        DotZan dz = new DotZan();
        dz.setAlumniCircleId(dotZanDomain.getAlumniCircleId());
        dz.setUserId(dotZanDomain.getUserId());
        dz.setUserName(dotZanDomain.getUserName());
        dz = dotZanRepository.save(dz);
        Long total = countDotZan(dotZanDomain.getAlumniCircleId());
        ac.setDzTotal(Integer.parseInt(total + ""));
        alumniCircleService.save(ac);
        return dz;
    }

    /**
     * @param dz
     * @return
     * @Title: deleteDotZan
     * @Description: 取消点赞
     * @return: DotZan
     */
    @Transactional
    public DotZan deleteDotZan(DotZan dz, AlumniCircle ac) {
        dz.setDeleteFlag(DataValidity.INVALID.getState());
        dz = dotZanRepository.save(dz);
        Long total = countDotZan(dz.getAlumniCircleId());
        ac.setDzTotal(Integer.parseInt(total + ""));
        alumniCircleService.save(ac);
        return dz;
    }

    /**
     * @param dzd
     * @Title: findDotZan
     * @Description: 查询点赞信息
     * @return: DotZan
     */
    public DotZan findDotZan(DotZanDomain dzd) {
        return dotZanRepository.findByAlumniCircleIdAndUserIdAndDeleteFlag(dzd.getAlumniCircleId(), dzd.getUserId(), DataValidity.VALID.getState());
    }
}
