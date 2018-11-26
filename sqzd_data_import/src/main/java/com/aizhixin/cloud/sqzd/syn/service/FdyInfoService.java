package com.aizhixin.cloud.sqzd.syn.service;

import com.aizhixin.cloud.sqzd.syn.manager.BaseDataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("fdyInfoService")
public class FdyInfoService extends BaseDataService {
   @Autowired
   @Qualifier("fdyInfoManager")
   private BaseDataManager baseDataManager;

    /**
     * 比较基线版本数据
     */
    public void compactBaseLineData() {
        compactBaseLineData(baseDataManager);
    }

}
