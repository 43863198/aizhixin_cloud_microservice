
package com.aizhixin.cloud.school.schoolinfo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.aizhixin.cloud.school.schoolinfo.domain.SchoolLogoInfoDomain;
import com.aizhixin.cloud.school.schoolinfo.entity.SchoolLogoInfo;

/** 
 * @ClassName: SchoolLogoInfoRepository 
 * @Description: 学校logo信息管理操作
 * @author xiagen
 * @date 2017年5月11日 下午6:34:48 
 *  
 */

public interface SchoolLogoInfoRepository extends CrudRepository<SchoolLogoInfo, Long>{
	/**
	 * 
	 * @Title: findBySchoolIdAndDeleteFlag 
	 * @Description: 根据学校id查询学校logo信息
	 * @param  schoolId
	 * @param  deleteFlag
	 * @return List<SchoolLogoInfo> 
	 * @throws
	 */
	@Query("select new com.aizhixin.cloud.school.schoolinfo.domain.SchoolLogoInfoDomain(sli.id,sli.schoolId,sli.logoUrl,sli.description,sli.logoSize,sli.logoSort) from com.aizhixin.cloud.school.schoolinfo.entity.SchoolLogoInfo sli where sli.schoolId=:schoolId and sli.deleteFlag=:deleteFlag")
    public List<SchoolLogoInfoDomain> findBySchoolIdAndDeleteFlag(@Param("schoolId")Long schoolId,@Param("deleteFlag")Integer deleteFlag);

    public SchoolLogoInfo findByIdAndDeleteFlag(Long id,Integer deleteFlag);
}
