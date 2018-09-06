package com.aizhixin.cloud.dd.rollcall.serviceV3;

import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.dto.SignInDTO;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author: Created by jianwei.wu
 * @E-mail: wujianwei@aizhixin.com
 * @Date: 2017-09-27
 */
@Component
public class RollCallServiceV3 {
    static ConcurrentLinkedQueue concurrentLinkedDeque = new ConcurrentLinkedQueue();
    /**
     * 学生签到(信息先保存在队列中，异步处理)
     *
     * @param account
     * @param signInDTO
     * @return
     */
    public void excuteSignIn(AccountDTO account, SignInDTO signInDTO) {
        Map<String,Object> map = new HashMap<>();
        map.put("account",account);
        map.put("signInDTO",signInDTO);
        concurrentLinkedDeque.add(map);
    }




}
