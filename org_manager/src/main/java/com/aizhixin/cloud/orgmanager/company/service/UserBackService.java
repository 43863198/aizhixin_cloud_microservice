/**
 *
 */
package com.aizhixin.cloud.orgmanager.company.service;

import com.aizhixin.cloud.orgmanager.classschedule.service.TeachingClassStudentsService;
import com.aizhixin.cloud.orgmanager.common.PageData;
import com.aizhixin.cloud.orgmanager.common.core.DataValidity;
import com.aizhixin.cloud.orgmanager.common.core.ErrorCode;
import com.aizhixin.cloud.orgmanager.common.core.PageUtil;
import com.aizhixin.cloud.orgmanager.common.domain.SuccessDomain;
import com.aizhixin.cloud.orgmanager.common.exception.CommonException;
import com.aizhixin.cloud.orgmanager.common.service.DataSynService;
import com.aizhixin.cloud.orgmanager.company.domain.StudentBackDomain;
import com.aizhixin.cloud.orgmanager.company.entity.User;
import com.aizhixin.cloud.orgmanager.company.entity.UserBack;
import com.aizhixin.cloud.orgmanager.company.repository.UserBackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.*;

/**
 * 用户相关操作业务逻辑处理
 *
 * @author zhen.pan
 */
@Component
@Transactional
public class UserBackService {
    @Autowired
    private EntityManager em;
    @Autowired
    private UserBackRepository userBackRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private TeachingClassStudentsService teachingClassStudentsService;
    @Autowired
    private BaseDataCacheService baseDataCacheService;
    @Autowired
    private DataSynService dataSynService;

    /**
     * 保存实体
     *
     * @param user
     * @return
     */
    public UserBack save(UserBack user) {
        return userBackRepository.save(user);
    }

    /**
     * 保存实体列表
     *
     * @param userList
     * @return
     */
    public List<UserBack> save(List<UserBack> userList) {
        return userBackRepository.save(userList);
    }

    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public UserBack findById(Long id) {
        return userBackRepository.findOne(id);
    }

    // *************************************************************以下部分处理页面调用逻辑**********************************************************************//
    /**
     * 将学生假删除，添加到备份表
     * @param ids       学生ID列表
     * @param cause     移动的缘由
     * @param userId    操作人
     */
    public SuccessDomain doBackUser(List<Long> ids, String cause, Long userId) {
        if (null == userId) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "操作人是必须的");
        }
        if (null == ids || ids.isEmpty()) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "用户ID是必须的");
        }
        List<User> userList = new ArrayList<>();
        List<UserBack> userBackList = new ArrayList<>();
        List<Long> msgList = new ArrayList<>();
        for (Long id : ids) {
            User user = userService.findById(id);
            if (null == user) {
                throw new CommonException(ErrorCode.ID_IS_REQUIRED, "根据ID(" + id + ")没有查找到对应的用户信息");
            }
            //对象数据拷贝
            UserBack userBack = new UserBack();
            userBack.setUser(user);
            userBack.setCause(cause);
            userBack.setCreatedBy(userId);
            userBack.setLastModifiedBy(userId);
            userBackList.add(userBack);

            //假删除用户信息
            user.setDeleteFlag(DataValidity.INVALID.getState());
            userList.add(user);

            //禁用账号，暂时可以不禁用

            //从当前学期的教学班里边删除相应的用户信息
            teachingClassStudentsService.deleteByStudent(user);

            baseDataCacheService.deleteUser(id);
            msgList.add(id);
        }
        if (!userList.isEmpty()) {
            userService.save(userList);
            dataSynService.sendStudentDeleteMsg(msgList);
        }
        SuccessDomain successDomain = new SuccessDomain();
        if (!userBackList.isEmpty()) {
            save(userBackList);
            successDomain.setId(userBackList.get(0).getId());
        }
        return successDomain;
    }


    public SuccessDomain doResume(List<Long> ids, String cause, Long userId) {
        if (null == userId) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "操作人是必须的");
        }
        if (null == ids || ids.isEmpty()) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "备份ID是必须的");
        }
        List<User> userList = new ArrayList<>();
        List<UserBack> userBackList = new ArrayList<>();
        for (Long id : ids) {
            UserBack userBack = findById(id);
            if (null == userBack) {
                throw new CommonException(ErrorCode.ID_IS_REQUIRED, "根据ID(" + id + ")没有查找到对应的用户备份信息");
            }
            userBack.setLastModifiedBy(userId);
            userBack.setResumeCause(cause);
            userBack.setLastModifiedDate(new Date());
            userBack.setDeleteFlag(DataValidity.INVALID.getState());
            userBackList.add(userBack);

            User user = userBack.getUser();
            //假删除用户信息的恢复
            user.setDeleteFlag(DataValidity.VALID.getState());
            userList.add(user);
        }
        if (!userList.isEmpty()) {
            userService.save(userList);
        }
        SuccessDomain successDomain = new SuccessDomain();
        if (!userBackList.isEmpty()) {
            save(userBackList);
            successDomain.setId(userBackList.get(0).getId());
        }
        return successDomain;
    }


    @Transactional(readOnly = true)
    public PageData<StudentBackDomain> queryList(Pageable pageable, Long orgId, String name) {
        PageData<StudentBackDomain> pageData = new PageData<>();
        pageData.getPage().setPageSize(pageable.getPageSize());
        pageData.getPage().setPageNumber(pageable.getPageNumber() + 1);
        if (null == orgId || orgId <= 0) {
            return pageData;
        }
        Map<String, Object> condition = new HashMap<>();
        condition.put("deleteFlag", DataValidity.VALID.getState());
        StringBuilder chql
                = new StringBuilder("select count(c.id) from com.aizhixin.cloud.orgmanager.company.entity.UserBack c join c.user join c.user.classes join c.user.professional join c.user.college where  c.deleteFlag = :deleteFlag");
        StringBuilder hql = new StringBuilder(
                "select new com.aizhixin.cloud.orgmanager.company.domain.StudentBackDomain (c.id, c.cause, c.user.id, c.user.name, c.user.phone, c.user.jobNumber, c.user.sex, c.user.classes.id, c.user.classes.name, c.user.professional.id, c.user.professional.name, c.user.college.id, c.user.college.name) from com.aizhixin.cloud.orgmanager.company.entity.UserBack c where c.deleteFlag = :deleteFlag");

        if (!StringUtils.isEmpty(name)) {
            hql.append(" and (c.user.name like :name or c.user.jobNumber like :name)");
            chql.append(" and (c.user.name like :name or c.user.jobNumber like :name)");
            condition.put("name", "%" + name + "%");
        }

        hql.append(" and c.user.orgId = :orgId");
        chql.append(" and c.user.orgId = :orgId");
        condition.put("orgId", orgId);
        hql.append(" order by c.id DESC");

        Query q = em.createQuery(chql.toString());
        for (Map.Entry<String, Object> e : condition.entrySet()) {
            q.setParameter(e.getKey(), e.getValue());
        }
        Long count = (Long)q.getSingleResult();
        pageData.getPage().setTotalElements(count);
        if (count <= 0) {
            pageData.getPage().setTotalPages(1);
            return pageData;
        }
        TypedQuery<StudentBackDomain> tq = em.createQuery(hql.toString(), StudentBackDomain.class);
        for (Map.Entry<String, Object> e : condition.entrySet()) {
            tq.setParameter(e.getKey(), e.getValue());
        }
        tq.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        tq.setMaxResults(pageable.getPageSize());
        pageData.setData(tq.getResultList());
        pageData.getPage().setTotalPages(PageUtil.cacalatePagesize(count, pageData.getPage().getPageSize()));

        return pageData;
    }
}