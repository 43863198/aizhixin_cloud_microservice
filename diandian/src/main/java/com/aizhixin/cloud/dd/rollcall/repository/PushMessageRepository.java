package com.aizhixin.cloud.dd.rollcall.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.aizhixin.cloud.dd.messege.entity.PushMessage;

public interface PushMessageRepository extends
        JpaRepository<PushMessage, Long> {

    List<PushMessage> findAllByModuleAndFunctionAndUserIdAndHaveRead(
            String module, String function, Long userId, Boolean false1);

    Page<PushMessage> findAllByModuleAndFunctionAndUserIdAndDeleteFlag(
            String module, String function, Long userId, Integer deleteFlag,
            Pageable pageable);

    @Modifying
    @Query("update PushMessage p set p.deleteFlag = ?2 where p.id = ?1 ")
    int deleteMessage(long id, Integer deleteFlag);

    void deleteByUserIdAndModule(Long userId,String module);
}
