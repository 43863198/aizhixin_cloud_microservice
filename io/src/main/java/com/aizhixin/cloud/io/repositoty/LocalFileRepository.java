package com.aizhixin.cloud.io.repositoty;


import com.aizhixin.cloud.io.entity.LocalFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface LocalFileRepository extends JpaRepository<LocalFile, String> {
    List<LocalFile> findByKey(String key);
}