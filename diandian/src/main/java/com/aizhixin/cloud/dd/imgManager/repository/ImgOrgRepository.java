package com.aizhixin.cloud.dd.imgManager.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.aizhixin.cloud.dd.imgManager.entity.ImageOrg;

public interface ImgOrgRepository extends MongoRepository<ImageOrg, String>{
    public void deleteByImgManagerId(String imgManagerId);
    public List<ImageOrg> findByImgManagerId(String imgManagerId);
    public List<ImageOrg> findByImgManagerIdIn(List<String> imgManagerIds);
    public List<ImageOrg> findByOrgId(Long orgId);
}
