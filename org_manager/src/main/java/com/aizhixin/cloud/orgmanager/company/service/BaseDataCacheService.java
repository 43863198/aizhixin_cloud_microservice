package com.aizhixin.cloud.orgmanager.company.service;


import com.aizhixin.cloud.orgmanager.common.core.DataValidity;
import com.aizhixin.cloud.orgmanager.common.util.RoleConfig;
import com.aizhixin.cloud.orgmanager.company.domain.*;
import com.aizhixin.cloud.orgmanager.company.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class BaseDataCacheService {
    public final static String ORG_KEY = "org_api:first:org";
    public final static String COLLEGE_KEY = "org_api:first:college";
    public final static String PROFESSIONAL_KEY = "org_api:first:professional";
    public final static String CLASSES_KEY = "org_api:first:classes";
    public final static String USER_KEY = "org_api:first:user";
    public final static String ALL_ORG_KEY = "org_api:first:allorgs";

    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private CollegeService collegeService;
    @Autowired
    private ProfessionalService professionalService;
    @Autowired
    private ClassesService classesService;
    @Autowired
    private UserService userService;
    @Autowired
    private RoleConfig roleConfig;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ClassesTeacherService classesTeacherService;
    @Autowired
    private UserRoleService userRoleService;


    /************************************************学校的相关操作********************************************************/
    @Transactional(readOnly = true)
    public void cacheAllOrg(Set<Long> orgids) {
        List<Organization> orgs = null;
        if (null == orgids || orgids.size() <= 0) {
            orgs = organizationService.findAll();
        } else {
            orgs = organizationService.findByIdIn(orgids);
        }
        if (null == orgs) {
            return;
        }

        Map<String, OrgDomain> orgDomainMap = new HashMap<>();
        Map<String, CollegeDomain> collegeDomainMap = new HashMap<>();
        Map<String, ProfessionnalDomain> professionnalDomainMap = new HashMap<>();
        Map<String, ClassesDomain> classesDomainMap = new HashMap<>();

        for (Organization o : orgs) {
            orgDomainMap.put(o.getId().toString(), new OrgDomain(o));

            List<College> collegeList = collegeService.findAllByOrgIdAndDeleteFlag(o.getId(), DataValidity.VALID.getState());
            for (College c : collegeList) {
                collegeDomainMap.put(c.getId().toString(), new CollegeDomain(c));

                List<Professional> pflist = professionalService.findByOrgId(o.getId());
                for (Professional p : pflist) {
                    professionnalDomainMap.put(p.getId().toString(), new ProfessionnalDomain(p));

                    List<Classes> clist = classesService.findByOrgId(o.getId());
                    for (Classes cs : clist) {
                        classesDomainMap.put(cs.getId().toString(), new ClassesDomain(cs));
                    }
                }
            }
        }

        if (!orgDomainMap.isEmpty()) {
            redisTemplate.opsForHash().putAll(ORG_KEY, orgDomainMap);
            redisTemplate.expire(ORG_KEY, 1, TimeUnit.DAYS);
        }
        if (!collegeDomainMap.isEmpty()) {
            redisTemplate.opsForHash().putAll(COLLEGE_KEY, collegeDomainMap);
            redisTemplate.expire(COLLEGE_KEY, 1, TimeUnit.DAYS);
        }
        if (!professionnalDomainMap.isEmpty()) {
            redisTemplate.opsForHash().putAll(PROFESSIONAL_KEY, professionnalDomainMap);
            redisTemplate.expire(PROFESSIONAL_KEY, 1, TimeUnit.DAYS);
        }
        if (!classesDomainMap.isEmpty()) {
            redisTemplate.opsForHash().putAll(CLASSES_KEY, classesDomainMap);
            redisTemplate.expire(CLASSES_KEY, 1, TimeUnit.DAYS);
        }
    }

    public void clearAllOrg() {
        redisTemplate.delete(ORG_KEY);
        redisTemplate.delete(COLLEGE_KEY);
        redisTemplate.delete(PROFESSIONAL_KEY);
        redisTemplate.delete(CLASSES_KEY);
        clearAllUser();
    }

    public void cacheOrg(OrgDomain o) {
        redisTemplate.opsForHash().put(ORG_KEY, o.getId().toString(), o);
    }

    public OrgDomain readOrg(Long id) {
        return (OrgDomain) redisTemplate.opsForHash().get(ORG_KEY, id.toString());
    }


    /************************************************学院的相关操作********************************************************/
    public void clearAllCollege() {
        redisTemplate.delete(COLLEGE_KEY);
    }

    public void cacheCollege(CollegeDomain c) {
        redisTemplate.opsForHash().put(COLLEGE_KEY, c.getId().toString(), c);
        redisTemplate.expire(COLLEGE_KEY, 1, TimeUnit.DAYS);
    }

    public void cacheCollege(List<CollegeDomain> cs) {
        Map<String, CollegeDomain> collegeDomainMap = new HashMap<>();
        for (CollegeDomain c : cs) {
            collegeDomainMap.put(c.getId().toString(), c);
        }
        redisTemplate.opsForHash().putAll(COLLEGE_KEY, collegeDomainMap);
        redisTemplate.expire(COLLEGE_KEY, 1, TimeUnit.DAYS);
    }

    public CollegeDomain readCollege(Long id) {
        return (CollegeDomain) redisTemplate.opsForHash().get(COLLEGE_KEY, id.toString());
    }

    /************************************************专业的相关操作********************************************************/
    public void clearAllProfessional() {
        redisTemplate.delete(PROFESSIONAL_KEY);
    }

    public void cacheProfessional(ProfessionnalDomain c) {
        redisTemplate.opsForHash().put(PROFESSIONAL_KEY, c.getId().toString(), c);
        redisTemplate.expire(PROFESSIONAL_KEY, 1, TimeUnit.DAYS);
    }

    public void cacheProfessional(List<ProfessionnalDomain> cs) {
        Map<String, ProfessionnalDomain> professionnalMap = new HashMap<>();
        for (ProfessionnalDomain c : cs) {
            professionnalMap.put(c.getId().toString(), c);
        }
        redisTemplate.opsForHash().putAll(PROFESSIONAL_KEY, professionnalMap);
        redisTemplate.expire(PROFESSIONAL_KEY, 1, TimeUnit.DAYS);
    }

    public ProfessionnalDomain readProfessional(Long id) {
        return (ProfessionnalDomain) redisTemplate.opsForHash().get(PROFESSIONAL_KEY, id.toString());
    }

    /************************************************班级的相关操作********************************************************/
    public void clearAllClasses() {
        redisTemplate.delete(CLASSES_KEY);
    }

    public void cacheClasses(ClassesDomain c) {
        redisTemplate.opsForHash().put(CLASSES_KEY, c.getId().toString(), c);
    }

    public void cacheClasses(List<ClassesDomain> cs) {
        Map<String, ClassesDomain> classesMap = new HashMap<>();
        for (ClassesDomain c : cs) {
            classesMap.put(c.getId().toString(), c);
        }
        redisTemplate.opsForHash().putAll(CLASSES_KEY, classesMap);
        redisTemplate.expire(CLASSES_KEY, 1, TimeUnit.DAYS);
    }

    public ClassesDomain readClasses(Long id) {
        return (ClassesDomain) redisTemplate.opsForHash().get(CLASSES_KEY, id.toString());
    }


    /************************************************用户的相关操作********************************************************/
    public void clearAllUser() {
        redisTemplate.delete(USER_KEY);
        for (int i = 0; i < 20; i++) {
            redisTemplate.delete(USER_KEY + i);
        }
    }

    public boolean cacheUser(UserDomain c) {
        redisTemplate.opsForHash().put(USER_KEY + (c.getId() % 20), c.getId().toString(), c);
        redisTemplate.expire(USER_KEY + (c.getId() % 20), 1, TimeUnit.DAYS);
        return true;
    }

    public void cacheUser(List<UserDomain> cs) {
        Map<Long, Map<String, UserDomain>> userMap = new HashMap<>();
        for (UserDomain c : cs) {
            Long m = c.getId() % 20;
            Map<String, UserDomain> um = userMap.get(m);
            if (null == um) {
                um = new HashMap<>();
                userMap.put(m, um);
            }
            um.put(c.getId().toString(), c);
        }
        for (Map.Entry<Long, Map<String, UserDomain>> e : userMap.entrySet()) {
            if (!e.getValue().isEmpty()) {
                redisTemplate.opsForHash().putAll(USER_KEY + e.getKey(), e.getValue());
                redisTemplate.expire(USER_KEY + e.getKey(), 1, TimeUnit.DAYS);
            }
        }
    }

    public UserDomain readUser(Long id) {
        if (id == null) {
            return null;
        }
        return (UserDomain) redisTemplate.opsForHash().get(USER_KEY + (id % 20), id.toString());
    }

    public void deleteUser(Long id) {
        redisTemplate.opsForHash().delete(USER_KEY + (id % 20), id.toString());
    }

    public void cacheAllOrgIds(Set<Long> orgids) {
        if (null != orgids && orgids.size() > 0) {
            StringBuilder sb = new StringBuilder();
            int i = 0;
            for (Long id : orgids) {
                if (i > 0) {
                    sb.append(",");
                }
                sb.append(id);
                i++;
            }
            redisTemplate.opsForHash().put(ALL_ORG_KEY, ALL_ORG_KEY, sb.toString());
        }
    }

    public Set<Long> readAllOrgIds() {
        Set<Long> orgIds = new HashSet<>();
        String str = (String) redisTemplate.opsForHash().get(ALL_ORG_KEY, ALL_ORG_KEY);
        if (!StringUtils.isEmpty(str)) {
            String[] ids = StringUtils.split(str, ",");
            for (String id : ids) {
                orgIds.add(new Long(id));
            }
        }
        return orgIds;
    }

    @Transactional(readOnly = true)
    public void cacheAllOrgUser(Set<Long> orgids) {
        Map<Long, Map<String, UserDomain>> userMap = new HashMap<>();
        for (Long orgId : orgids) {
            List<User> users = userService.findAllTeacherByOrgId(orgId);
            for (User u : users) {
                UserDomain d = userService.initBatchCommitUserReturnData(u, null, null);
                d.setRoleGroup(roleConfig.getRoleGroup2B());
                List<String> roles = userRoleService.findByUser(u.getId());
                if (null != roles && !roles.isEmpty()) {
                    for (String role : roles) {
                        d.addRole(role);
                    }
                }
                long cts = classesTeacherService.countByTeacher(u);//班主任判断
                if (cts > 0) {
                    d.addRole(RoleConfig.CLASSES_MASTER);
                }
                Long m = u.getId() % 20;
                Map<String, UserDomain> um = userMap.get(m);
                if (null == um) {
                    um = new HashMap<>();
                    userMap.put(m, um);
                }
                um.put(u.getId().toString(), d);
            }
            users = userService.findAllStudentByOrgId(orgId);
            for (User u : users) {
                Long m = u.getId() % 20;
                Map<String, UserDomain> um = userMap.get(m);
                if (null == um) {
                    um = new HashMap<>();
                    userMap.put(m, um);
                }
                um.put(u.getId().toString(), userService.initBatchCommitUserReturnData(u, roleConfig.getRoleGroup2B(), roleConfig.getRoleStudent2B()));
            }
        }

        for (Map.Entry<Long, Map<String, UserDomain>> e : userMap.entrySet()) {
            if (!e.getValue().isEmpty()) {
                redisTemplate.opsForHash().putAll(USER_KEY + e.getKey(), e.getValue());
                redisTemplate.expire(USER_KEY + e.getKey(), 1, TimeUnit.DAYS);
            }
        }
    }

    public void initAllBaseData(Set<Long> orgs) {
        if (null != orgs && orgs.size() > 0) {
            cacheAllOrgIds(orgs);
            cacheAllOrg(orgs);
            cacheAllOrgUser(orgs);
        } else {
            Set<Long> org2 = readAllOrgIds();
            if (null != org2 && org2.size() > 0) {
                cacheAllOrg(orgs);
                cacheAllOrgUser(org2);
            }
        }
    }
}
