package com.aizhixin.cloud.dd.imgManager.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.aizhixin.cloud.dd.imgManager.entity.ImageManager;

import java.util.List;

public interface ImgManagerRepository extends MongoRepository<ImageManager, String>{
    public Page<ImageManager> findAll(Pageable page);
    public List<ImageManager> findByIdIn(List<String> ids);
}
