
package com.aizhixin.cloud.school.schoolinfo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.aizhixin.cloud.school.schoolinfo.domain.SchoolShuffImageDomain;
import com.aizhixin.cloud.school.schoolinfo.entity.SchoolShuffImage;

/** 
 * @ClassName: SchoolShuffImageRepository 
 * @Description: 学校轮播图管理
 * @author xiagen
 * @date 2017年5月12日 下午12:40:53 
 *  
 */

public interface SchoolShuffImageRepository extends CrudRepository<SchoolShuffImage, Long>{
	@Query("select new com.aizhixin.cloud.school.schoolinfo.domain.SchoolShuffImageDomain(ssf.id,ssf.imageUrl,ssf.imageSort,ssf.href) from com.aizhixin.cloud.school.schoolinfo.entity.SchoolShuffImage ssf where ssf.schoolId=:schoolId")
    public List<SchoolShuffImageDomain> findBySchoolId(@Param("schoolId")Long schoolId);
}
