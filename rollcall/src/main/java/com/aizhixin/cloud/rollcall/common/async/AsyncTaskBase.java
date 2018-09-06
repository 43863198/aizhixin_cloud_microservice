package com.aizhixin.cloud.rollcall.common.async;


import com.aizhixin.cloud.rollcall.domain.ScheduleRedisDomain;
import com.aizhixin.cloud.rollcall.service.ClassInTaskPreprocessService;
import com.aizhixin.cloud.rollcall.service.ClassOutTaskPreprocessService;
import com.aizhixin.cloud.rollcall.service.RollCallMedianProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AsyncTaskBase {
    //    private final static Logger LOG = LoggerFactory.getLogger(AsyncTaskBase.class);
    @Autowired
    private ClassInTaskPreprocessService classInTaskPreprocessService;
    @Autowired
    private ClassOutTaskPreprocessService classOutTaskPreprocessService;

    @Lazy
    @Autowired
    private RollCallMedianProcessService rollCallMedianProcessService;

    /**
     * 启动当前课堂进入当前课堂内模式
     *
     * @param preSchedules 需要在15分钟内开启课堂内模式的课程列表
     * @param time         当前时间
     * @param orgId        学校ID
     */
    @Async
    public void startScheduleClasses(List <ScheduleRedisDomain> preSchedules, long time, Long orgId) {
        classInTaskPreprocessService.timeToSetupClasses(preSchedules, time, orgId);
    }

    /**
     * 启动当前课堂进入课后模式
     *
     * @param preSchedules 需要在15分钟内退出课堂内模式的课程列表
     * @param time         当前时间
     * @param orgId        学校ID
     */
    @Async
    public void stopScheduleClasses(List <ScheduleRedisDomain> preSchedules, long time, Long orgId) {
        classOutTaskPreprocessService.timeToOutClasses(preSchedules, time, orgId);
    }

    /**
     * 中值计算
     *
     * @param orgId
     */
    @Async
    public void rollCallMedian(Long orgId) {
        rollCallMedianProcessService.rollCallMedianOrg(orgId);
    }
}
