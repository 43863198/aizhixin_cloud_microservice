//package com.aizhixin.cloud.dd.alumnicircle.service;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//import javax.transaction.Transactional;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.util.StringUtils;
//
//import com.aizhixin.cloud.dd.alumnicircle.domain.AlumniCircleFileDomain;
//import com.aizhixin.cloud.dd.alumnicircle.entity.AlumniCircleFile;
//import com.aizhixin.cloud.dd.alumnicircle.repository.AlumniCircleFlieRepository;
//import com.aizhixin.cloud.dd.common.core.DataValidity;
//
//@Service
//public class AlumniCircleFileService {
//	@Autowired
//	private AlumniCircleFlieRepository alumniCircleFlieRepository;
//
//	/**
//	 * @Title: save
//	 * @Description: 保存图片与校友圈的关系
//	 * @param alumniCircleFlieDomains
//	 * @return: void
//	 */
//	@Transactional
//	public void save(List<AlumniCircleFileDomain> alumniCircleFlieDomains) {
//		List<AlumniCircleFile> acfl = new ArrayList<>();
//		for (AlumniCircleFileDomain alumniCircleFileDomain : alumniCircleFlieDomains) {
//			if (!StringUtils.isEmpty(alumniCircleFileDomain.getSrcUrl())) {
//				AlumniCircleFile acf = new AlumniCircleFile();
//				acf.setAlumniCircleId(alumniCircleFileDomain.getAlumniCircleId());
//				acf.setCreatedDate(new Date());
//				acf.setDeleteFlag(DataValidity.VALID.getState());
//				acf.setFileSize(alumniCircleFileDomain.getFileSize());
//				acf.setSrcUrl(alumniCircleFileDomain.getSrcUrl());
//				acfl.add(acf);
//			}
//		}
//		if (!acfl.isEmpty()) {
//			alumniCircleFlieRepository.save(acfl);
//		}
//	}
//}
