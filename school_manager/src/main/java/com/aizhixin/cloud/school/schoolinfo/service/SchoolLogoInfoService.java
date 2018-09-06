
package com.aizhixin.cloud.school.schoolinfo.service;

import java.util.Date;
import java.util.List;

import com.aizhixin.cloud.school.schoolinfo.remote.DDClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aizhixin.cloud.school.common.core.DataValidity;
import com.aizhixin.cloud.school.schoolinfo.domain.SchoolLogoInfoDomain;
import com.aizhixin.cloud.school.schoolinfo.entity.SchoolLogoInfo;
import com.aizhixin.cloud.school.schoolinfo.repository.SchoolLogoInfoRepository;

/**
 * @author xiagen
 * @ClassName: SchoolLogoInfoService
 * @Description:学校logo信息管理
 * @date 2017年5月11日 下午6:37:28
 */
@Service
public class SchoolLogoInfoService {

    @Autowired
    private SchoolLogoInfoRepository schoolLogoInfoRepository;
    @Autowired
    private DDClient ddClient;

    /**
     * @param schoolLogoInfoDomain
     * @return SchoolLogoInfo
     * @throws
     * @Title: saveSchoolLogoInfo
     * @Description: 添加学校logo信息
     */
    public SchoolLogoInfo saveSchoolLogoInfo(SchoolLogoInfoDomain schoolLogoInfoDomain) {
        SchoolLogoInfo schoolLogoInfo = new SchoolLogoInfo();
        schoolLogoInfo.setCreatedBy(schoolLogoInfoDomain.getUserId());
        schoolLogoInfo.setDescription(schoolLogoInfoDomain.getDescription());
        schoolLogoInfo.setLastModifiedBy(schoolLogoInfoDomain.getUserId());
        schoolLogoInfo.setLogoUrl(schoolLogoInfoDomain.getLogoUrl());
        schoolLogoInfo.setSchoolId(schoolLogoInfoDomain.getOrgId());
        schoolLogoInfo.setLogoSize(schoolLogoInfoDomain.getLogoSize());
        schoolLogoInfo.setLogoSort(schoolLogoInfoDomain.getLogoSort());
        return schoolLogoInfoRepository.save(schoolLogoInfo);

    }

    public SchoolLogoInfo findById(Long id) {
        return schoolLogoInfoRepository.findByIdAndDeleteFlag(id, DataValidity.VALID.getState());
    }

    public SchoolLogoInfo updateSchoolLogoInfo(SchoolLogoInfoDomain schoolLogoInfoDomain, SchoolLogoInfo schoolLogoInfo) {
        schoolLogoInfo.setDescription(schoolLogoInfoDomain.getDescription());
        schoolLogoInfo.setLastModifiedBy(schoolLogoInfoDomain.getUserId());
        schoolLogoInfo.setLogoUrl(schoolLogoInfoDomain.getLogoUrl());
        schoolLogoInfo.setLastModifiedDate(new Date());
        schoolLogoInfo.setLogoSize(schoolLogoInfoDomain.getLogoSize());
        schoolLogoInfo.setLogoSort(schoolLogoInfoDomain.getLogoSort());
        return schoolLogoInfoRepository.save(schoolLogoInfo);

    }

    public List<SchoolLogoInfoDomain> findSchoolLogoInfo(Long schoolId) {
        return schoolLogoInfoRepository.findBySchoolIdAndDeleteFlag(schoolId, DataValidity.VALID.getState());

    }

    public void updateGetAdCache(Long orgId) {
        try {
            ddClient.initAd(orgId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
