
package com.aizhixin.cloud.school.schoolinfo.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.aizhixin.cloud.school.schoolinfo.domain.SchoolExcellentCourseDomain;
import com.aizhixin.cloud.school.schoolinfo.entity.SchoolExcellentCourse;

/** 
 * @ClassName: SchoolExcellentCourseRepository 
 * @Description: 精品课程jpa
 * @author xiagen
 * @date 2017年5月16日 下午5:16:29 
 *  
 */

public interface SchoolExcellentCourseRepository extends PagingAndSortingRepository<SchoolExcellentCourse, Long>{
   public SchoolExcellentCourse findByIdAndDeleteFlag(Long id,Integer deleteFlag);
   
   public SchoolExcellentCourse findByCourseIdAndDeleteFlag(Long courseId,Integer deleteFlag);

   SchoolExcellentCourse findByCourseIdAndSchoolIdAndDeleteFlag(Long courseId,Long schoolId,Integer deleteFlag);
   
   @Query("select new com.aizhixin.cloud.school.schoolinfo.domain.SchoolExcellentCourseDomain(ec.id,ec.courseId,ec.introduction,ec.inUrl,ec.templateShow,ec.createdDate,ec.kfCourseId,ec.kfCourseName) from com.aizhixin.cloud.school.schoolinfo.entity.SchoolExcellentCourse ec where ec.schoolId=:schoolId and ec.deleteFlag=:deleteFlag order by ec.id desc")
   public Page<SchoolExcellentCourseDomain> findBySchoolIdAndDeleteFlag(Pageable page,@Param("schoolId")Long schoolId,@Param("deleteFlag")Integer deleteFlag);
   @Query("select new com.aizhixin.cloud.school.schoolinfo.domain.SchoolExcellentCourseDomain(ec.id,ec.courseId,ec.introduction,ec.inUrl,ec.templateShow,ec.createdDate,ec.kfCourseId,ec.kfCourseName) from com.aizhixin.cloud.school.schoolinfo.entity.SchoolExcellentCourse ec where ec.courseId in :ids and ec.deleteFlag=:deleteFlag order by ec.id desc")
   public Page<SchoolExcellentCourseDomain> findByCourseIdAndDeleteFlagLikeName(Pageable page,@Param("ids")List<Long> ids,@Param("deleteFlag")Integer deleteFlag);
}
