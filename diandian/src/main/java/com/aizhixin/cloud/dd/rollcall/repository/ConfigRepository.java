package com.aizhixin.cloud.dd.rollcall.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aizhixin.cloud.dd.rollcall.entity.Config;

public interface ConfigRepository extends JpaRepository<Config, Long> {

	List<Config> findConfigByPid(Long parentid);

	Config findOneByPidEqualsAndKeysEquals(long l, String codeParentSms);

	Config findConfigByKeys(String keys);

}
