package com.aizhixin.cloud.dd.imgManager.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.common.domain.PageDomain;
import com.aizhixin.cloud.dd.imgManager.domain.ImgInfoDomain;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.aizhixin.cloud.dd.common.core.PageUtil;
import com.aizhixin.cloud.dd.imgManager.domain.ImgDomain;
import com.aizhixin.cloud.dd.imgManager.domain.OrgDomain;
import com.aizhixin.cloud.dd.imgManager.entity.ImageManager;
import com.aizhixin.cloud.dd.imgManager.entity.ImageOrg;
import com.aizhixin.cloud.dd.imgManager.repository.ImgManagerRepository;
import com.aizhixin.cloud.dd.imgManager.repository.ImgOrgRepository;
import org.springframework.util.StringUtils;
/**
 * @author xiagen
 * @date 2018/2/6 18:06
 */
@Service
public class ImgManagerService {
	@Autowired
	private RedisTemplate<String,String> redisTemplate;
	@Autowired
	private ImgManagerRepository imgManagerRepository;
	@Autowired
	private ImgOrgRepository imgOrgRepository;
	/**
	 * 保存图片管理信息
	 * @param img
	 */
	public ImageManager save(ImgDomain img) {
		ImageManager imr=new ImageManager();
		imr.setImgSrc(img.getImgSrc());
		imr.setModule(img.getModule());
		imr.setRedirectUrl(img.getRedirectUrl());
		imr=imgManagerRepository.save(imr);
		List<ImageOrg> iol=new ArrayList<>();
		for (OrgDomain org : img.getOrgInfoList()) {
			ImageOrg io=new ImageOrg();
			io.setImgManagerId(imr.getId());
			io.setOrgId(org.getOrgId());
			io.setOrgName(org.getOrgName());
			iol.add(io);
		}
		if(!iol.isEmpty()) {
			imgOrgRepository.save(iol);
		}
		return imr;
	}

	public ImageManager update(ImgDomain img) {
		ImageManager im=imgManagerRepository.findOne(img.getId());
		if(null!=im) {
			im.setImgSrc(img.getImgSrc());
			im.setModule(img.getModule());
			im.setRedirectUrl(img.getRedirectUrl());
			im=imgManagerRepository.save(im);
			deleteImgId(im.getId());
			List<ImageOrg> iol=new ArrayList<>();
			for (OrgDomain org : img.getOrgInfoList()) {
				ImageOrg io=new ImageOrg();
				io.setImgManagerId(im.getId());
				io.setOrgId(org.getOrgId());
				io.setOrgName(org.getOrgName());
				iol.add(io);
			}
			if(!iol.isEmpty()) {
				imgOrgRepository.save(iol);
			}
		}
		return im;
	}
	
	
	public ImgDomain findOne(String id) {
		ImgDomain imd=new ImgDomain();
		ImageManager im=imgManagerRepository.findOne(id);
		if(im!=null) {
			BeanUtils.copyProperties(im, imd);
			List<ImageOrg> iml=imgOrgRepository.findByImgManagerId(id);
			List<OrgDomain> odl=new ArrayList<>();
			for (ImageOrg imageOrg : iml) {
				OrgDomain od=new OrgDomain();
				BeanUtils.copyProperties(imageOrg, od);
				odl.add(od);
			}
			imd.setOrgInfoList(odl);
		}
		return imd;
	}
	
	
	public void deleteImgId(String id) {
		imgOrgRepository.deleteByImgManagerId(id);
	}
	
	
	public void deleteImgIdAll(String id) {
		imgManagerRepository.delete(id);
		deleteImgId(id);
	}
	
	public Map<String,Object> listImageDomainAll(Integer pageNumber, Integer pageSize, Map<String,Object> result) {
		Pageable page=PageUtil.createNoErrorPageRequest(pageNumber, pageSize);
		Page<ImageManager>  pim=imgManagerRepository.findAll(page);
		List<ImageManager> piml=pim.getContent();
		List<ImgDomain> idl=new ArrayList<>();
		List<String> imIds=new ArrayList<>();
		for (ImageManager imageManager : piml) {
			ImgDomain idi=new ImgDomain();
			BeanUtils.copyProperties(imageManager, idi);
			idl.add(idi);
			imIds.add(imageManager.getId());
		}
		if(!imIds.isEmpty()) {
			List<ImageOrg> iol=imgOrgRepository.findByImgManagerIdIn(imIds);
			for (ImgDomain imgDomain : idl) {
				List<OrgDomain> odl=new ArrayList<>();
				for (ImageOrg imageOrg : iol) {
					if(imageOrg.getImgManagerId().equals(imgDomain.getId())) {
						OrgDomain od=new OrgDomain();
						BeanUtils.copyProperties(imageOrg, od);
						odl.add(od);
					}
				}
				if(!odl.isEmpty()) {
					imgDomain.setOrgInfoList(odl);
				}
			}
		}
		PageDomain pageDomain=new PageDomain();
		pageDomain.setPageNumber(page.getPageNumber());
		pageDomain.setPageSize(page.getPageSize());
		pageDomain.setTotalElements(pim.getTotalElements());
		pageDomain.setTotalPages(pim.getTotalPages());
		result.put(ApiReturnConstants.RESULT,Boolean.TRUE);
		result.put(ApiReturnConstants.DATA,idl);
		result.put(ApiReturnConstants.PAGE,pageDomain);
		return result;
	}

     /**
	  * @author xiagen
	  * @date 2018/2/6 18:07
	  * @param [orgId]
	  * @return java.util.List<com.aizhixin.cloud.dd.imgManager.domain.ImgInfoDomain>
	  */
	 public List<ImgInfoDomain>  findByImgInfo(Long orgId){
		String jsonData=redisTemplate.opsForValue().get("orgId"+orgId);
		List<ImgInfoDomain> imgInfoDomainList = new ArrayList<>();
		if (StringUtils.isEmpty(jsonData)) {
			List<ImageOrg> iol = imgOrgRepository.findByOrgId(orgId);
			List<String> imIds = new ArrayList<>();
			for (ImageOrg imageOrg : iol) {
				imIds.add(imageOrg.getImgManagerId());
			}
			if (!imIds.isEmpty()) {
				List<ImageManager> imageManagerList = imgManagerRepository.findByIdIn(imIds);
				for (ImageManager imageManager : imageManagerList) {
					ImgInfoDomain imgInfoDomain = new ImgInfoDomain();
					BeanUtils.copyProperties(imageManager, imgInfoDomain);
					imgInfoDomainList.add(imgInfoDomain);
				}
			}
			if (!imgInfoDomainList.isEmpty()){
				String json=JSON.toJSONString(imgInfoDomainList);
				redisTemplate.opsForValue().set("orgId"+orgId,json,300, TimeUnit.SECONDS);
			}
		}else{
			imgInfoDomainList=JSON.parseArray(jsonData,ImgInfoDomain.class);
		}
		return imgInfoDomainList;
	}
}
