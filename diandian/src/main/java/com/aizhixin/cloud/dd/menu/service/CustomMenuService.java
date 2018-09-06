package com.aizhixin.cloud.dd.menu.service;

import com.aizhixin.cloud.dd.menu.entity.CustomMenu;
import com.aizhixin.cloud.dd.menu.repository.CustomMenuRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CustomMenuService {

    @Autowired
    private CustomMenuRespository menuRespository;

    public void save(CustomMenu menu) {
        List<CustomMenu> list = menuRespository.findByUserId(menu.getUserId());
        menuRespository.delete(list);
        menuRespository.save(menu);
    }

    public CustomMenu getByUserId(Long userId) {
        List<CustomMenu> list = menuRespository.findByUserId(userId);
        if(list != null && list.size() > 0){
            return list.get(0);
        }
        return null;
    }
}
