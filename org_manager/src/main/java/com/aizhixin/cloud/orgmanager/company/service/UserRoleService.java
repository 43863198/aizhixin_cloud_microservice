/**
 *
 */
package com.aizhixin.cloud.orgmanager.company.service;

import com.aizhixin.cloud.orgmanager.common.PageData;
import com.aizhixin.cloud.orgmanager.common.core.DataValidity;
import com.aizhixin.cloud.orgmanager.common.core.PageUtil;
import com.aizhixin.cloud.orgmanager.company.core.UserRoleEnum;
import com.aizhixin.cloud.orgmanager.company.domain.UserDomain;
import com.aizhixin.cloud.orgmanager.company.dto.TeacherUserRoleDTO;
import com.aizhixin.cloud.orgmanager.company.entity.User;
import com.aizhixin.cloud.orgmanager.company.entity.UserRole;
import com.aizhixin.cloud.orgmanager.company.repository.UserRoleRepository;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 用户相关操作业务逻辑处理
 *
 * @author zhen.pan
 */
@Component
@Transactional
public class UserRoleService {
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    UserService userService;
    @Autowired
    private EntityManager em;
    @Autowired
    private BaseDataCacheService baseDataCacheService;

    /**
     * 保存实体
     *
     * @param userRole
     * @return
     */
    public UserRole save(UserRole userRole) {
        return userRoleRepository.save(userRole);
    }

    /**
     * 根据用户信息查询用户角色实体
     *
     * @param userId
     * @return
     */
    @Transactional(readOnly = true)
    public List<String> findByUser(Long userId) {
        return userRoleRepository.findByUser(userId, DataValidity.VALID.getState());
    }

    /**
     * 按照用户删除角色
     *
     * @param user
     */
    public void deleteByUser(User user) {
        userRoleRepository.deleteByUser(user);
    }

    /**
     * 按照用户ID删除用户角色
     *
     * @param userId
     */
    public void deleteByUser(Long userId) {
        userRoleRepository.deleteByUserId(userId);
    }

    public List<UserRole> save(List<UserRole> userRoles) {
        return userRoleRepository.save(userRoles);
    }
    // *************************************************************以下部分处理页面调用逻辑**********************************************************************//

    /**
     * 角色分配
     *
     * @param managerId
     * @param userId
     * @param roleNaem
     * @return
     */
    public Map<String, Object> distributionRole(Long managerId, Long userId, String roleNaem) {
        Map<String, Object> result = new HashMap<>();
        User user = userService.findById(userId);
        Set<String> nameSet = new HashSet<>();
        nameSet.add("ROLE_ORG_ADMIN");
        nameSet.add("ROLE_ORG_MANAGER");
        nameSet.add("ROLE_COLLEGE_ADMIN");
        nameSet.add("ROLE_ORG_EDUCATIONALMANAGER");
        nameSet.add("ROLE_ORG_DATAVIEW");
        nameSet.add("ROLE_COLLEG_EDUCATIONALMANAGER");
        nameSet.add("ROLE_COLLEG_DATAVIEW");
        nameSet.add("ROLE_FINANCE_ADMIN");
        nameSet.add("ROLE_DORM_ADMIN");
        nameSet.add("ROLE_ENROL_ADMIN");
        if (null != user) {
            List<String> userRole = this.findByUser(userId);
            boolean isExist = false;
            if (userRole.size() > 0) {
                for (String role : userRole) {
                    if (nameSet.contains(role)) {
                        isExist = true;
                        break;
                    }
                }
                if (isExist) {
                    result.put("success", false);
                    result.put("message", "该老师已存在权限角色，请勿重复分配!");
                } else {
                    if (null != roleNaem && !StringUtils.isBlank(roleNaem)) {
                        UserRole ur = new UserRole();
                        ur.setUser(user);
                        ur.setRoleGroup("B");
                        ur.setRoleName(roleNaem);
                        ur.setCreatedDate(new Date());
                        ur.setLastModifiedDate(new Date());
                        ur.setLastModifiedBy(managerId);
                        userRoleRepository.save(ur);

                        // 更新Redis的用户信息
                        UserDomain d = baseDataCacheService.readUser(userId);
                        if (null != d) {
                            d.addRole(roleNaem);
                            baseDataCacheService.cacheUser(d);
                        }
                        result.put("success", true);
                        result.put("message", "角色分配成功!");
                    }
                }
            }
        }
        return result;
    }

    /**
     * 按照条件分页查询已分配的管理员
     *
     * @param managerId
     * @param teacherName
     * @param roleName
     * @param pageable
     * @return
     */
    public PageData<TeacherUserRoleDTO> distributionlList(Long managerId, Long collegeId, String teacherName, String roleName, Pageable pageable) {
        PageData<TeacherUserRoleDTO> p = new PageData<>();
        User user = userService.findById(managerId);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd ");
        Long count = 0L;
        Set<String> nameSet = new HashSet<>();
        nameSet.add("ROLE_ORG_MANAGER");
        nameSet.add("ROLE_COLLEGE_ADMIN");
        nameSet.add("ROLE_ORG_EDUCATIONALMANAGER");
        nameSet.add("ROLE_ORG_DATAVIEW");
        nameSet.add("ROLE_COLLEG_EDUCATIONALMANAGER");
        nameSet.add("ROLE_COLLEG_DATAVIEW");
        nameSet.add("ROLE_FINANCE_ADMIN");
        nameSet.add("ROLE_DORM_ADMIN");
        nameSet.add("ROLE_ENROL_ADMIN");
        Map<String, Object> condition = new HashMap<>();
        StringBuffer sql = new StringBuffer(
            "SELECT u.ID as id, u.NAME as name, u.PHONE as phone, u.JOB_NUMBER as jobNUmber, u.SEX as sex, c.NAME as cName, r.ROLE_NAME as rName, r.LAST_MODIFIED_DATE as mDate, r.LAST_MODIFIED_BY as manager"
                + " FROM t_user_role r LEFT JOIN t_user u ON r.USER_ID = u.ID LEFT JOIN t_college c ON u.COLLEGE_ID = c.ID WHERE 1 = 1");
        if (null != user) {
            sql.append(" and u.ORG_ID = :orgId");
            condition.put("orgId", user.getOrgId());
            if (null != collegeId) {
                sql.append(" and c.ID = :collegeId");
                condition.put("collegeId", collegeId);
            }
            if (null != teacherName && !StringUtils.isBlank(teacherName)) {
                sql.append(" and u.NAME like :teacherName");
                condition.put("teacherName", "%" + teacherName + "%");
            }
            if (null != roleName && !StringUtils.isBlank(roleName)) {
                sql.append(" and r.ROLE_NAME like :roleName");
                condition.put("roleName", "%" + roleName + "%");
            } else {
                sql.append(" and r.ROLE_NAME IN (:nameSet)");
                condition.put("nameSet", nameSet);
            }
            sql.append(" ORDER BY r.LAST_MODIFIED_DATE DESC");

            Query sq = em.createNativeQuery(sql.toString());
            for (Map.Entry<String, Object> e : condition.entrySet()) {
                sq.setParameter(e.getKey(), e.getValue());
            }
            count = new Long(sq.getResultList().size());
            sq.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
            sq.setMaxResults(pageable.getPageSize());
            List<Object> result = sq.getResultList();
            if (result.size() > 0) {
                List<TeacherUserRoleDTO> teacherUserRoleDTOList = new ArrayList<>();
                for (Object d : result) {
                    Object[] obj = (Object[])d;
                    TeacherUserRoleDTO teacherUserRoleDTO = new TeacherUserRoleDTO();
                    teacherUserRoleDTO.setId(Long.valueOf(String.valueOf(obj[0])));
                    if (null != obj[1]) {
                        teacherUserRoleDTO.setName(String.valueOf(obj[1]));
                    }
                    if (null != obj[2]) {
                        teacherUserRoleDTO.setPhone(String.valueOf(obj[2]));
                    }
                    if (null != obj[3]) {
                        teacherUserRoleDTO.setJobNumber(String.valueOf(obj[3]));
                    }
                    if (null != obj[4]) {
                        teacherUserRoleDTO.setSex(String.valueOf(obj[4]));
                    }
                    if (null != obj[5]) {
                        teacherUserRoleDTO.setCollegeName(String.valueOf(obj[5]));
                    }
                    if (null != obj[6]) {
                        teacherUserRoleDTO.setRole(String.valueOf(obj[6]));
                        teacherUserRoleDTO.setRoleName(UserRoleEnum.valueOf(String.valueOf(obj[6])).getValue());
                    }

                    try {
                        teacherUserRoleDTO.setLastModifiedDate(format.parse(String.valueOf(obj[7])));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (null != obj[8] && null != Long.valueOf(String.valueOf(obj[8]))) {
                        User manager = userService.findById(Long.valueOf(String.valueOf(obj[8])));
                        if (null != manager) {
                            teacherUserRoleDTO.setManager(manager.getName());
                        }
                    }
                    teacherUserRoleDTOList.add(teacherUserRoleDTO);
                }
                p.setData(teacherUserRoleDTOList);
            }
        }
        p.getPage().setTotalElements(count);
        p.getPage().setPageNumber(pageable.getPageNumber());
        p.getPage().setPageSize(pageable.getPageSize());
        p.getPage().setTotalPages(PageUtil.cacalatePagesize(count, p.getPage().getPageSize()));
        return p;
    }

    /**
     * 取消用户的权限
     *
     * @param userId
     * @param roleName
     * @return
     */
    public Map<String, Object> deleteRole(Long userId, String roleName) {
        Map<String, Object> result = new HashMap<>();
        if (null != userId && null != roleName && !StringUtils.isBlank(roleName)) {
            userRoleRepository.deleteByUserIdAndRoleName(userId, roleName);

            // 更新Redis的用户信息
            UserDomain d = baseDataCacheService.readUser(userId);
            if (null != d) {
                d.deleteRole(roleName);
                baseDataCacheService.cacheUser(d);
            }

            result.put("sussess", true);
            result.put("message", "取消权限成功！");
        } else {
            result.put("sussess", false);
            result.put("message", "取消权限失败！");
        }
        return result;
    }

}