package com.aizhixin.cloud.dd.menu.repository;

import com.aizhixin.cloud.dd.menu.entity.CustomMenu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomMenuRespository extends JpaRepository<CustomMenu, Long> {
    public List<CustomMenu> findByUserId(Long userId);
}

