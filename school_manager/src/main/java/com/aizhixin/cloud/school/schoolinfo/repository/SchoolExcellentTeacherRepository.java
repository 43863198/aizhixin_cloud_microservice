
package com.aizhixin.cloud.school.schoolinfo.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.aizhixin.cloud.school.schoolinfo.domain.SchoolExcellentTeacherDomain;
import com.aizhixin.cloud.school.schoolinfo.entity.SchoolExcellentTeacher;

/** 
 * @ClassName: SchoolExcellentTeacherRepository 
 * @Description: 优秀教师管理jpa
 * @author xiagen
 * @date 2017年5月15日 上午11:37:49 
 *  
 */

public interface SchoolExcellentTeacherRepository extends PagingAndSortingRepository<SchoolExcellentTeacher, Long>{
    public SchoolExcellentTeacher findByIdAndDeleteFlag(Long id,Integer deleteFlag);
    
    public SchoolExcellentTeacher findByTeacherIdAndDeleteFlag(Long teacherId,Integer deleteFlag);
    
    @Query("select new com.aizhixin.cloud.school.schoolinfo.domain.SchoolExcellentTeacherDomain(se.id,se.teacherId,se.introduction,se.inUrl,se.templateShow) from com.aizhixin.cloud.school.schoolinfo.entity.SchoolExcellentTeacher se where se.schoolId=:schoolId and se.deleteFlag=:deleteFlag order by se.id desc")
    public Page<SchoolExcellentTeacherDomain> findBySchoolIdAndDeleteFlag(Pageable page,@Param("schoolId")Long schoolId,@Param("deleteFlag")Integer deleteFlag);
    
    @Query("select new com.aizhixin.cloud.school.schoolinfo.domain.SchoolExcellentTeacherDomain(se.id,se.teacherId,se.introduction,se.inUrl,se.templateShow) from com.aizhixin.cloud.school.schoolinfo.entity.SchoolExcellentTeacher se where se.teacherId in :ids and se.schoolId=:schoolId and se.deleteFlag=:deleteFlag order by se.id desc")
    public Page<SchoolExcellentTeacherDomain> findByTeacherIdAndDeleteFlag(Pageable page,@Param("schoolId")Long schoolId,@Param("deleteFlag")Integer deleteFlag,@Param("ids")List<Long> ids);
}
