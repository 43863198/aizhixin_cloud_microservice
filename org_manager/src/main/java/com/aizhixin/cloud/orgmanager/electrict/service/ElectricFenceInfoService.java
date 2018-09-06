package com.aizhixin.cloud.orgmanager.electrict.service;

import com.aizhixin.cloud.orgmanager.common.core.DataValidity;
import com.aizhixin.cloud.orgmanager.electrict.entity.ElectricFenceInfo;
import com.aizhixin.cloud.orgmanager.electrict.repository.ElectricFenceInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Created by jianwei.wu
 * @E-mail wujianwei@aizhixin.com
 */
@Service
@Transactional
public class ElectricFenceInfoService {
    @Autowired
    private ElectricFenceInfoRepository electricFenceInfoRepository;

    /**
     * 根据学校id查询围栏信息
     * @param organId
     * @return
     */
    public ElectricFenceInfo findOneByOrganId(Long organId) {
        return electricFenceInfoRepository.findOneByOrganIdAndDeleteFlag(organId, DataValidity.VALID.getState());
    }

    /**
     * 根据学校id和开启状态查询围栏信息
     * @param organId
     * @param setupOrClose
     * @return
     */
    public Long findIdByOranIdAndSetupOrClose(Long organId,Integer setupOrClose) {
        return electricFenceInfoRepository.findIdByOranIdAndSetupOrClose(organId, DataValidity.VALID.getState(), setupOrClose);
    }

    /**
     * 更新电子围栏
     * @param organId
     * @param lltudes
     * @param monitorDate
     * @param nomonitorDate
     * @param semesterId
     */
    public void updateElectricFenceInfo(Long organId, String lltudes,String monitorDate,String nomonitorDate, Long semesterId) {
         electricFenceInfoRepository.updateElectricFenceInfo(organId,lltudes,monitorDate,nomonitorDate,semesterId);
    }

    /**
     * 创建电子围栏
     * @param electricFenceInfo
     */
    public void save(ElectricFenceInfo electricFenceInfo) {
        electricFenceInfoRepository.save(electricFenceInfo);
    }

    /**
     * 电子围栏的关闭和开启
     * @param organdId
     * @param flag
     * @return
     */
    public Map<String,Object> fenceSwitch(Long organdId, String flag){
        Map<String,Object> result = new HashMap<>();
        ElectricFenceInfo electricFenceInfo = electricFenceInfoRepository.findOneByOrganIdAndDeleteFlag(organdId, DataValidity.VALID.getState());
        if(null != electricFenceInfo){
            if(flag.equals("0")){
                electricFenceInfo.setSetupOrClose(20);
                electricFenceInfoRepository.save(electricFenceInfo);
                result.put("success",true);
                result.put("message","围栏关闭成功！");
            }else{
                electricFenceInfo.setSetupOrClose(10);
                electricFenceInfoRepository.save(electricFenceInfo);
                result.put("success",true);
                result.put("message","围栏开启成功！");
            }
        }else {
            result.put("success",false);
            result.put("message","未设置，请先设置！");
        }
        return result;
    }

}
