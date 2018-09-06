
package com.aizhixin.cloud.school.schoolinfo.repository;

import org.springframework.data.repository.CrudRepository;

import com.aizhixin.cloud.school.schoolinfo.entity.SchoolIntrduction;

/** 
 * @ClassName: SchoolIntroductionRepository 
 * @Description: 学校简介信息管理
 * @author xiagen
 * @date 2017年5月12日 下午12:37:41 
 *  
 */

public interface SchoolIntroductionRepository extends CrudRepository<SchoolIntrduction, Long>{
	
   public SchoolIntrduction findBySchoolIdAndDeleteFlag(Long schoolId,Integer deleteFlag);
}
