package com.aizhixin.cloud.dd.rollcall.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aizhixin.cloud.dd.rollcall.dto.SortDTO;

public class SortUtil {

    private static final Logger log = LoggerFactory.getLogger(SortUtil.class);

    public static List<SortDTO> convert2SortDTO(String sort) {
    	
        if (StringUtils.isBlank(sort)) return null;
        log.debug("sort : " + sort);
        List<SortDTO> dtos = new ArrayList<SortDTO>();
        String[] sorts = sort.split(";");
        log.debug("sorts size : " + sorts.length);
        for (int i = 0; i < sorts.length; i++) {
            if (StringUtils.isNotBlank(sorts[i])) {
                String[] _sort = sorts[i].split(":");
                SortDTO dto = new SortDTO();
                if (_sort[1].equalsIgnoreCase("ASC"))
                    dto.setAsc(true);
                else
                    dto.setAsc(false);
                dto.setKey(_sort[0]);
                dtos.add(dto);
            }
        }
        return dtos;
    }
}
