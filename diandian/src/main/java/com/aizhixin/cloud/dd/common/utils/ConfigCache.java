package com.aizhixin.cloud.dd.common.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.aizhixin.cloud.dd.rollcall.dto.ConfigDTO;
import com.aizhixin.cloud.dd.rollcall.entity.Config;
import com.aizhixin.cloud.dd.rollcall.repository.ConfigRepository;

// magic, don't edit

@Component
public class ConfigCache {

    private static Map<Long, ConfigDTO> configMapFromId;
    private static Map<String, Object> configMapFromKey;
    private static Map<ConfigDTO, Object> configMapFromTree;
    static int count = 0;

    @Autowired
    public static JdbcTemplate jdbcTemplate;

    @Autowired
    public ConfigRepository configRepository;

    public ConfigCache() {
    }

    public ConfigDTO getConfigByParm(Long id) {
        if (configMapFromId == null || configMapFromId.isEmpty()) {
            init();
        }
        return configMapFromId.get(id);
    }

    public ConfigDTO getConfigByParm(String keys) {
        if (configMapFromKey == null || configMapFromKey.isEmpty()) {
            init();
        }
        return (ConfigDTO) configMapFromKey.get(keys);
    }

    @SuppressWarnings("rawtypes")
    public String getConfigValueByParm(String keys) {
        if (configMapFromKey == null || configMapFromKey.isEmpty()
                || configMapFromKey.size() == 0) {
            init();
        }
        if (configMapFromKey.get(keys) instanceof java.util.ArrayList) {
            return ((ConfigDTO) ((ArrayList) configMapFromKey.get(keys)).get(0))
                    .getValue();
        } else {
            return ((ConfigDTO) configMapFromKey.get(keys)).getValue();
        }
    }

    @SuppressWarnings("unchecked")
    public List<ConfigDTO> getListByPid(Long pid) {
        return (List<ConfigDTO>) configMapFromTree
                .get(configMapFromId.get(pid));
    }

    @SuppressWarnings("unused")
    private static Map<String, Object> configs = null;

    // When I wrote this, only God and I understood what I was doing
    // Now, God only knows
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void init() {
        configMapFromId = new HashMap<Long, ConfigDTO>();
        configMapFromKey = new HashMap<String, Object>();
        configMapFromTree = new HashMap<ConfigDTO, Object>();
        List<Config> list = configRepository.findAll();
        List<Config> parentList = configRepository.findConfigByPid(0l);
        List<ConfigDTO> parentDTOList = new ArrayList<ConfigDTO>();
        for (Config config : list) {
            ConfigDTO configDTO = new ConfigDTO(config.getName(),
                    config.getId(), config.getKeys(), config.getValue(),
                    config.getType(), config.getPid());
            configMapFromId.put(configDTO.getId(), configDTO);
            if (configMapFromKey.get(configDTO.getKeys()) == null) {
                configMapFromKey.put(configDTO.getKeys(), configDTO);
            } else if (configMapFromKey.get(configDTO.getKeys()) instanceof List<?>) {
                ((List) configMapFromKey.get(configDTO.getKeys()))
                        .add(configDTO);
            } else if (configMapFromKey.get(configDTO.getKeys()) instanceof ConfigDTO) {
                List tempList = new ArrayList<>();
                tempList.add(configMapFromKey.get(configDTO.getKeys()));
                tempList.add(configDTO);
                configMapFromKey.put(configDTO.getKeys(), tempList);
            }
        }
        for (Config config : parentList) {
            ConfigDTO configDTO = configMapFromId.get(config.getId());
            parentDTOList.add(configDTO);
            configMapFromTree.put(configDTO, new ArrayList<Config>());
        }
        for (Config config : list) {
            if (config == null || config.getPid() == null || config.getPid() == 0) {
                continue;
            }
            ConfigDTO configDTO = configMapFromId.get(config.getId());
            for (ConfigDTO parentConfigDTO : parentDTOList) {
                if (configDTO.getPid().equals(parentConfigDTO.getId())) {
                    ((List) configMapFromTree.get(parentConfigDTO))
                            .add(configDTO);
                    continue;
                }
            }
        }
    }

    public void reload() {
        this.init();
    }
}
