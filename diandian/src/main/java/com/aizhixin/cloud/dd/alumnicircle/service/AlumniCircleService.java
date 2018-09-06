package com.aizhixin.cloud.dd.alumnicircle.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.aizhixin.cloud.dd.alumnicircle.core.SendToModule;
import com.aizhixin.cloud.dd.alumnicircle.domain.AlumniCircleDomain;
import com.aizhixin.cloud.dd.alumnicircle.domain.AlumniCircleDomainOne;
import com.aizhixin.cloud.dd.alumnicircle.domain.AlumniCircleDomainQgSend;
import com.aizhixin.cloud.dd.alumnicircle.domain.AlumniCircleDomainSend;
import com.aizhixin.cloud.dd.alumnicircle.domain.AlumniCircleFileDomain;
import com.aizhixin.cloud.dd.alumnicircle.entity.AlumniCircle;
import com.aizhixin.cloud.dd.alumnicircle.entity.AlumniCircleFile;
import com.aizhixin.cloud.dd.alumnicircle.entity.Attention;
import com.aizhixin.cloud.dd.alumnicircle.entity.DotZan;
import com.aizhixin.cloud.dd.alumnicircle.repository.AlumniCircleFlieRepository;
import com.aizhixin.cloud.dd.alumnicircle.repository.AlumniCircleRepository;
import com.aizhixin.cloud.dd.alumnicircle.repository.AttentionRepository;
import com.aizhixin.cloud.dd.alumnicircle.repository.DotZanRepository;
import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.common.core.DataValidity;
import com.aizhixin.cloud.dd.common.core.PageUtil;
import com.aizhixin.cloud.dd.common.domain.PageDomain;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.repository.AssessRepository;
import com.aizhixin.cloud.dd.rollcall.service.DDUserService;
import com.aizhixin.cloud.dd.rollcall.utils.JsonUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Service
@Transactional
public class AlumniCircleService {
	@Autowired
	private AlumniCircleFlieRepository alumniCircleFlieRepository;
	@Autowired
	private AlumniCircleRepository alumniCircleRepository;
	@Autowired
	private AssessRepository assessRepository;
	@Autowired
	private AttentionRepository attentionRepository;
	@Autowired
	private DotZanRepository dotZanRepository;
	@Autowired
	private OrgManagerRemoteClient orgManagerRemoteService;
	@Autowired
	private DDUserService  ddUserService;

	public AlumniCircle save(AlumniCircleDomain alumniCircleDomain, AccountDTO adt) {
		AlumniCircle ac = new AlumniCircle();
		BeanUtils.copyProperties(alumniCircleDomain, ac);
		ac.setFromUserId(adt.getId());
		ac.setFromUserName(adt.getName());
		ac.setCollegeId(adt.getCollegeId());
		ac.setCollegeName(adt.getCollegeName());
		ac.setOrgId(adt.getOrganId());
		String org=orgManagerRemoteService.getOrganById(adt.getOrganId());
		if(!StringUtils.isEmpty(org)){
			try {
			Map<String, Object> d=JsonUtil.Json2Object(org);
			if(null!=d&&null!=d.get("name")){
				ac.setOrgName(d.get("name").toString());
			}
			} catch (JsonParseException e) {

				e.printStackTrace();
			} catch (JsonMappingException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
		ac.setId(null);
		if (!alumniCircleDomain.getAlumniCircleFileDomains().isEmpty()) {
			List<AlumniCircleFile> acfl = new ArrayList<>();
			for (AlumniCircleFileDomain alumniCircleFileDomain : alumniCircleDomain.getAlumniCircleFileDomains()) {
				AlumniCircleFile acf = new AlumniCircleFile();
				acf.setAlumniCircle(ac);
				acf.setCreatedDate(new Date());
				acf.setDeleteFlag(DataValidity.VALID.getState());
				acf.setFileSize(alumniCircleFileDomain.getFileSize());
				acf.setSrcUrl(alumniCircleFileDomain.getSrcUrl());
				acfl.add(acf);
			}
			alumniCircleFlieRepository.save(acfl);
		}else{
			ac = alumniCircleRepository.save(ac);
		}
		return ac;
	}
	public AlumniCircle findByAlumniCircle(Long id) {
		return alumniCircleRepository.findByIdAndDeleteFlag(id, DataValidity.VALID.getState());
	}
	@Transactional
	public AlumniCircle save(AlumniCircle ac) {
		return alumniCircleRepository.save(ac);
	}
	
	/**
	 * 
	 * @Title: findByAlumniCircleInfo 
	 * @Description: 查询本校校友圈
	 * @param pageNumber
	 * @param pageSize
	 * @param orgId
	 * @param userId
	 * @param result
	 * @return: Map<String,Object>
	 */
	public Map<String, Object> findByAlumniCircleInfo( Integer pageNumber,Integer pageSize,Long orgId, Long userId,Map<String, Object> result) {
		Pageable page=PageUtil.createNoErrorPageRequest(pageNumber, pageSize);
		Page<AlumniCircle> pac = alumniCircleRepository.findByOrgIdAndSendToModuleAndDeleteFlagOrderByDzTotalDesc(page, orgId,SendToModule.BX,
				DataValidity.VALID.getState());
		PageDomain pd=new PageDomain();
		List<AlumniCircleDomainSend> acdl = new ArrayList<>();
		if (null != pac) {
			pd.setPageNumber(pac.getNumber());
			pd.setPageSize(pac.getSize());
			pd.setTotalElements(pac.getTotalElements());
			pd.setTotalPages(pac.getTotalPages());
			Map<String, Attention> map = new HashMap<>();
			List<Attention> at = attentionRepository.findByAttentionUserIdAndDeleteFlag(userId,
					DataValidity.VALID.getState());
			Map<String, DotZan> mapDotZan = new HashMap<>();
			List<DotZan> dzl = dotZanRepository.findByUserIdAndDeleteFlag(userId, DataValidity.VALID.getState());
			if (null != dzl && 0 < dzl.size()) {
				for (DotZan dotZan : dzl) {
					mapDotZan.put(dotZan.getUserId() + "-" + dotZan.getAlumniCircleId(), dotZan);
				}
			}
			if (null != at && 0 < at.size()) {
				for (Attention attention : at) {
					map.put(attention.getAttentionUserId() + "-" + attention.getFollowedUserId(), attention);
				}
			}
			List<AlumniCircle> acl = pac.getContent();
			List<Long> ids = new ArrayList<>();
			for (AlumniCircle alumniCircle : acl) {
				AlumniCircleDomainSend acd = new AlumniCircleDomainSend();
				BeanUtils.copyProperties(alumniCircle, acd);
				List<AlumniCircleFile>  acfl=alumniCircle.getAlumniCircleFile();
				if(null!=acfl&&0<acfl.size()){
					 List<AlumniCircleFileDomain> alumniCircleFileDomains=new ArrayList<>();
					 for (AlumniCircleFile alumniCircleFile : acfl) {
						 AlumniCircleFileDomain acdf=new AlumniCircleFileDomain();
							BeanUtils.copyProperties(alumniCircleFile, acdf);
							alumniCircleFileDomains.add(acdf);
					}
					 acd.setAlumniCircleFileDomains(alumniCircleFileDomains);
				}
				if (null != map.get(userId + "-" + acd.getFromUserId())) {
					acd.setAttention(Boolean.TRUE);
				} else {
					acd.setAttention(Boolean.FALSE);
				}
				if (null != mapDotZan.get(userId + "-" + acd.getId())) {
					acd.setDotZan(Boolean.TRUE);
				} else {
					acd.setDotZan(Boolean.FALSE);
				}
					ids.add(alumniCircle.getFromUserId());
				if (!StringUtils.isEmpty(alumniCircle.getAvatars())) {
					List<String> avatrs = new ArrayList<>();
					String[] av = alumniCircle.getAvatars().split(";");
					for (String avatr : av) {
						avatrs.add(avatr);
					}
					acd.setAvatars(avatrs);
				}
				acdl.add(acd);
			}
			if (!ids.isEmpty()) {
				Map<Long, AccountDTO> data = ddUserService.getUserinfoByIdsV2(ids);
				if(null!=data){
					for (AlumniCircleDomainSend alumniCircleDomainSend : acdl) {
						AccountDTO ad=data.get(alumniCircleDomainSend.getFromUserId());
						if(null!=ad){
							alumniCircleDomainSend.setFromUserAvatar(ad.getAvatar());
							if (alumniCircleDomainSend.isNickName()) {
								alumniCircleDomainSend.setFromUserName(ad.getName());
							}
						}
					}
				}
			}
		}else{
			pd.setPageNumber(pageNumber);
			pd.setPageSize(pageSize);
			pd.setTotalElements(0L);
			pd.setTotalPages(0);
		}
		result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
		result.put(ApiReturnConstants.DATA, acdl);
		result.put(ApiReturnConstants.PAGE, pd);
		return result;
	}
	
	public AlumniCircleDomainOne findByAlumniCircleInfoOne(Long alumniCircleId,Long userId) {
		AlumniCircleDomainOne acds=new AlumniCircleDomainOne();
		AlumniCircle ac=alumniCircleRepository.findByIdAndDeleteFlag(alumniCircleId, DataValidity.VALID.getState());
		if(null!=ac){
			BeanUtils.copyProperties(ac, acds);
			List<AlumniCircleFile> acfl=ac.getAlumniCircleFile();
			if(null!=acfl&&0<acfl.size()){
				List<AlumniCircleFileDomain> acfdl=new ArrayList<>();
				for (AlumniCircleFile alumniCircleFile : acfl) {
					AlumniCircleFileDomain acfd=new AlumniCircleFileDomain();
					BeanUtils.copyProperties(alumniCircleFile, acfd);
					acfdl.add(acfd);
				}
				acds.setAlumniCircleFileDomains(acfdl);
			}
			List<Long> ids=new ArrayList<>();
			ids.add(ac.getFromUserId());
			Map<Long, AccountDTO> map=ddUserService.getUserinfoByIdsV2(ids);
			if(null!=map){
				AccountDTO ad=map.get(ac.getFromUserId());
				if(null!=ad){
					acds.setFromUserAvatar(ad.getAvatar());
					if(acds.isNickName()){
						acds.setFromUserName(ad.getName());
					}
				}
			}
			Attention a=attentionRepository.findByAttentionUserIdAndFollowedUserIdAndDeleteFlag(userId, ac.getFromUserId(), DataValidity.VALID.getState());
		    if(null!=a){
		    	acds.setAttention(Boolean.TRUE);
		    }else{
		    	acds.setAttention(Boolean.FALSE);
		    }
		}
		return acds;
		
	}
	
	/**
	 * 
	 * @Title: findAlumniCircleQG 
	  全国校友圈查询
	 * @param userId
	 * @param pageNumber
	 * @param pageSize
	 * @param result
	 * @return: Map<String,Object>
	 */
	
	public Map<String, Object> findAlumniCircleQG(Long userId,Integer pageNumber,Integer pageSize,Map<String, Object> result) {
		Pageable page=PageUtil.createNoErrorPageRequest(pageNumber, pageSize);
		Page<AlumniCircle> pac=alumniCircleRepository.findBySendToModuleAndDeleteFlagOrderByDzTotalDesc(page, SendToModule.QG, DataValidity.VALID.getState());
		List<AlumniCircleDomainQgSend> adqsl=new ArrayList<>();
		PageDomain pd=new PageDomain();
		pd.setPageNumber(pac.getNumber());
		pd.setPageSize(pac.getSize());
		pd.setTotalElements(pac.getTotalElements());
		pd.setTotalPages(pac.getTotalPages());
		if(null!=pac){
			List<AlumniCircle> acl=pac.getContent();
			Map<String, DotZan> mapDotZan = new HashMap<>();
			List<DotZan> dzl = dotZanRepository.findByUserIdAndDeleteFlag(userId, DataValidity.VALID.getState());
			if (null != dzl && 0 < dzl.size()) {
				for (DotZan dotZan : dzl) {
					mapDotZan.put(dotZan.getUserId() + "-" + dotZan.getAlumniCircleId(), dotZan);
				}
			}
			List<Long> ids=new ArrayList<>();
			for (AlumniCircle alumniCircle : acl) {
				AlumniCircleDomainQgSend adqs=new AlumniCircleDomainQgSend();
				BeanUtils.copyProperties(alumniCircle, adqs);
				List<AlumniCircleFile> acfl=alumniCircle.getAlumniCircleFile();
				List<AlumniCircleFileDomain> acfdl=new ArrayList<>();
				ids.add(alumniCircle.getFromUserId());
				if(null!=acfl&&0<acfl.size()){
					for (AlumniCircleFile alumniCircleFile : acfl) {
						AlumniCircleFileDomain acfd=new AlumniCircleFileDomain();
						BeanUtils.copyProperties(alumniCircleFile, acfd);
						acfdl.add(acfd);
					}
				}
				if (null != mapDotZan.get(userId + "-" + adqs.getId())) {
					adqs.setDotZan(Boolean.TRUE);
				} else {
					adqs.setDotZan(Boolean.FALSE);
				}
				adqs.setAlumniCircleFileDomains(acfdl);
				adqsl.add(adqs);
			}
			if(!ids.isEmpty()){
				Map<Long, AccountDTO>  data=ddUserService.getUserinfoByIdsV2(ids);
				if(null!=data){
					for (AlumniCircleDomainQgSend alumniCircleDomainQgSend : adqsl) {
						AccountDTO ad=data.get(alumniCircleDomainQgSend.getFromUserId());
						if(null!=ad){
							alumniCircleDomainQgSend.setFromUserAvatar(ad.getAvatar());
							if(alumniCircleDomainQgSend.isNickName()){
								alumniCircleDomainQgSend.setFromUserName(ad.getName());
							}
						}
					}
				}
			}
		}
		result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
		result.put(ApiReturnConstants.DATA, adqsl);
		result.put(ApiReturnConstants.PAGE, pd);
		return result;
	}
	
	public Map<String, Object> findAttentionAlumniCircle(Long userId,Integer pageNumber,Integer pageSize,Map<String, Object> result) {
		Pageable page=PageUtil.createNoErrorPageRequest(pageNumber, pageSize);
		List<Attention> at=attentionRepository.findByAttentionUserIdAndDeleteFlag(userId, DataValidity.VALID.getState());
		List<AlumniCircleDomainSend> acds=new ArrayList<>();
		PageDomain pd=new PageDomain();
		pd.setPageNumber(pageNumber);
		pd.setPageSize(pageSize);
		pd.setTotalElements(0L);
		pd.setTotalPages(0);
		if(null!=at&&0<at.size()){
			List<Long> ids=new ArrayList<>();
			for (Attention attention : at) {
				ids.add(attention.getFollowedUserId());
			}
			if(!ids.isEmpty()){
				Page<AlumniCircle>  pac=	alumniCircleRepository.findByFromUserIdInAndDeleteFlagOrderByCreatedDateDesc(page,ids,DataValidity.VALID.getState());
				pd.setPageNumber(pac.getNumber());
				pd.setPageSize(pac.getSize());
				pd.setTotalElements(pac.getTotalElements());
				pd.setTotalPages(pac.getTotalPages());
				List<AlumniCircle> acl=pac.getContent();
			    	if(null!=acl&&0<acl.size()){
			    		Map<Long, AccountDTO> mu=ddUserService.getUserinfoByIdsV2(ids);
						Map<String, DotZan> mapDotZan = new HashMap<>();
						List<DotZan> dzl = dotZanRepository.findByUserIdAndDeleteFlag(userId, DataValidity.VALID.getState());
						if (null != dzl && 0 < dzl.size()) {
							for (DotZan dotZan : dzl) {
								mapDotZan.put(dotZan.getUserId() + "-" + dotZan.getAlumniCircleId(), dotZan);
							}
						}
			    		for (AlumniCircle alumniCircle : acl) {
							AlumniCircleDomainSend acd = new AlumniCircleDomainSend();
							BeanUtils.copyProperties(alumniCircle, acd);
							List<AlumniCircleFile>  acfl=alumniCircle.getAlumniCircleFile();
							if(null!=acfl&&0<acfl.size()){
								 List<AlumniCircleFileDomain> alumniCircleFileDomains=new ArrayList<>();
								 for (AlumniCircleFile alumniCircleFile : acfl) {
									 AlumniCircleFileDomain acdf=new AlumniCircleFileDomain();
										BeanUtils.copyProperties(alumniCircleFile, acdf);
										alumniCircleFileDomains.add(acdf);
								}
								 acd.setAlumniCircleFileDomains(alumniCircleFileDomains);
							}
								acd.setAttention(Boolean.TRUE);
							if (null != mapDotZan.get(userId + "-" + acd.getId())) {
								acd.setDotZan(Boolean.TRUE);
							} else {
								acd.setDotZan(Boolean.FALSE);
							}
							if (!StringUtils.isEmpty(alumniCircle.getAvatars())) {
								List<String> avatrs = new ArrayList<>();
								String[] av = alumniCircle.getAvatars().split(";");
								for (String avatr : av) {
									avatrs.add(avatr);
								}
								acd.setAvatars(avatrs);
							}
							if(null!=mu){
								AccountDTO ad=mu.get(acd.getFromUserId());
								if(null!=ad){
									acd.setFromUserAvatar(ad.getAvatar());
									if(acd.isNickName()){
										acd.setFromUserName(ad.getName());
									}
								}
							}
							acds.add(acd);
						}
			    	}
			}
			
		}
	    result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
	    result.put(ApiReturnConstants.DATA, acds);
	    result.put(ApiReturnConstants.PAGE, pd);
		return result;
	}
	
}
