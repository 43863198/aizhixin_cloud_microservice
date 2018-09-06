package com.aizhixin.cloud.io;

import com.aizhixin.cloud.io.entity.LocalFile;
import com.aizhixin.cloud.io.repositoty.LocalFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional
public class LocalFileManager {
    @Autowired
    private LocalFileRepository localFileRepository;

    public LocalFile save(LocalFile entity) {
        return  localFileRepository.save(entity);
    }

    public List<LocalFile> saveAll(List<LocalFile> entityList) {
        return localFileRepository.save(entityList);
    }

    public List<LocalFile> findByKey(String key) {
        return  localFileRepository.findByKey(key);
    }
}
