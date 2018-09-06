
package com.aizhixin.cloud.school.schoolinfo.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.aizhixin.cloud.school.schoolinfo.domain.SchoolHotSpecialtyDomain;
import com.aizhixin.cloud.school.schoolinfo.entity.SchoolHotSpecialty;

/**
 * @ClassName: SchoolHotSpecialtyRepository
 * @Description: 热门专业管理
 * @author xiagen
 * @date 2017年5月12日 下午5:24:33
 * 
 */

public interface SchoolHotSpecialtyRepository extends PagingAndSortingRepository<SchoolHotSpecialty, Long> {
	public SchoolHotSpecialty findByIdAndDeleteFlag(Long id, Integer deleteFlag);
	
	
	public SchoolHotSpecialty findBySpecialtyIdAndDeleteFlag(Long specialtyId,Integer deleteFlag);

	public Page<SchoolHotSpecialty> findBySchoolIdAndDeleteFlag(Pageable page, Long schoolId, Integer deleteFlag);
	
	@Query("select new com.aizhixin.cloud.school.schoolinfo.domain.SchoolHotSpecialtyDomain(shs.id,shs.createdDate,shs.specialtyId,shs.introduction,shs.inUrl,shs.templateShow,shs.collegeId) from com.aizhixin.cloud.school.schoolinfo.entity.SchoolHotSpecialty shs where shs.specialtyId in :ids and shs.schoolId=:schoolId and shs.deleteFlag=:deleteFlag order by shs.id desc")
	public Page<SchoolHotSpecialtyDomain> findBySchoolIdAndDeleteFlagAndSpecialtyId(Pageable page, @Param("schoolId")Long schoolId,@Param("deleteFlag")Integer deleteFlag,@Param("ids")List<Long> ids);
}
