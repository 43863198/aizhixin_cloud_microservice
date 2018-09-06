package com.aizhixin.cloud.dd.homepage.service;

import java.util.*;

import com.aizhixin.cloud.dd.common.core.ApiReturn;
import com.aizhixin.cloud.dd.common.core.DataValidity;
import com.aizhixin.cloud.dd.common.provider.store.redis.RedisTokenStore;
import com.aizhixin.cloud.dd.homepage.domain.HomeDomain;
import com.aizhixin.cloud.dd.homepage.domain.HomePageDomain;
import com.aizhixin.cloud.dd.homepage.dto.HomePageMenuDTO;
import com.aizhixin.cloud.dd.homepage.entity.HomePage;
import com.aizhixin.cloud.dd.homepage.repository.HomePageRepository;
import com.aizhixin.cloud.dd.orgStructure.entity.OrgInfo;
import com.aizhixin.cloud.dd.orgStructure.repository.OrgInfoRepository;
import com.aizhixin.cloud.dd.remote.SchoolManagerClient;
import com.aizhixin.cloud.dd.rollcall.service.UserService;
import com.aizhixin.cloud.dd.rollcall.utils.HomePageUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class HomePageService {

    private final Logger log = LoggerFactory.getLogger(HomePageService.class);

    @Autowired
    private HomePageRepository homePageRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private SchoolManagerClient schoolManagerClient;
    @Autowired
    private RedisTokenStore redisTokenStore;
    @Autowired
    private OrgInfoRepository orgInfoRepository;

    @Cacheable(value = "CACHE.HOMEPAGE", key = "#key")
    public List<HomePageMenuDTO> list(String role, String subRole, String type, String key, String version, Long userId, Long orgId) {
        List<HomePage> list = null;
        list = homePageRepository.findAllByRoleAndTypeAndVersionAndDeleteFlag(role.equals(HomePageUtil.STUDENT) ? HomePageUtil.STUDENT : HomePageUtil.TEACHER, type, version,
                DataValidity.VALID.getState(), new Sort(new Order(Direction.ASC, "order")));
        // int show = 1;// 1: 只显示课程; 2:不显示导员点名，显示显示课表 ;3:显示导员点名，不显示课表
        if (list != null) {

            List<HomePageMenuDTO> hpDtoList = new ArrayList();
            for (HomePage homePage : list) {

                HomePageMenuDTO hpDto = new HomePageMenuDTO();
                BeanUtils.copyProperties(homePage, hpDto);

                if (!checkOnOff(homePage, orgId)) {
                    hpDto.setOnOff(HomePageUtil.ONOFF_OFF);
                } else {
                    hpDto.setOnOff(HomePageUtil.ONOFF_ON);
                }

                if (HomePageUtil.ONOFF_OFF.equals(hpDto.getOnOff()) && (HomePageUtil.TYPE_BANNER.equals(hpDto.getType()) || HomePageUtil.TEACHER.equals(role))) {
                    continue;
                }

                hpDtoList.add(hpDto);
            }
            return hpDtoList;
        }
        return null;
    }

    public boolean checkOnOff(HomePage homePage, Long orgId) {
        if (null == homePage) {
            return false;
        }
        String onOff = homePage.getOnOff();
        switch (onOff) {
            case HomePageUtil.ONOFF_ON:
                return true;
            case HomePageUtil.ONOFF_OFF:
                return false;
            case HomePageUtil.ONOFF_MANY:
                return checkOrg(homePage, orgId);
        }
        return true;
    }

    public boolean checkOrg(HomePage homePage, Long orgId) {
        if (null == orgId) {
            return true;
        }
        Set<String> set = new HashSet();
        String[] orgSpilts = homePage.getOrgs().split(",");
        Collections.addAll(set, orgSpilts);
        if (set.contains(String.valueOf(orgId))) {
            return true;
        }
        return false;
    }

    //    @Cacheable(value = "CACHE.HOMEPAGE", key = "#role +'_'+ #version +'_'+ #orgId")
    public List<HomeDomain> getAd(String role, String version, Long orgId) {
        List<HomeDomain> result = redisTokenStore.getAd(role + version + orgId);
        if (result == null) {
            result = initAd(role, version, orgId);
        }
        return result;
    }

    public List<HomeDomain> initAd(String role, String version, Long orgId) {
        List<HomePage> homepageList = homePageRepository.findAllByRoleAndTypeAndVersionAndDeleteFlag(role, HomePageUtil.TYPE_AD, version, DataValidity.VALID.getState(), new Sort(new Order(Direction.ASC, "order")));
        if (homepageList == null || homepageList.size() < 1) {
            homepageList = new ArrayList<>();
            HomePage h = new HomePage();
            h.setIconUrl("");
            h.setTargetUrl("");
            h.setTitle("");
            homepageList.add(h);
        }
        List<HomeDomain> result = typeHomeDomain(homepageList);
        if (orgId != null && orgId.longValue() > 0) {
            //获取org logo
            String str = schoolManagerClient.findLogo(orgId);
            if (!StringUtils.isEmpty(str)) {
                JSONObject jsonObject = JSONObject.fromObject(str);
                if (jsonObject != null) {
                    JSONArray imgArray = jsonObject.getJSONArray("data");
                    if (imgArray != null && imgArray.length() >= 2) {
                        JSONObject imgobj = imgArray.getJSONObject(1);
                        if (imgobj != null) {
                            String logoUrl = imgobj.getString("logoUrl");
                            if (result != null && result.size() > 0) {
                                result.get(0).setLogoUrl(logoUrl);
                            }
                        }
                    }
                    if (imgArray != null && imgArray.length() >= 3) {
                        JSONObject imgobj = imgArray.getJSONObject(2);
                        if (imgobj != null) {
                            String logoUrl = imgobj.getString("logoUrl");
                            if (result != null && result.size() > 0) {
                                result.get(0).setLogoUrl2(logoUrl);
                            }
                        }
                    }
                }
            }
        }
        redisTokenStore.setAd(role + version + orgId, result);
        return result;
    }

    @Async
    public void initAllRoleAd(String version, Long orgId) {
        initAd(HomePageUtil.STUDENT, StringUtils.isBlank(version) ? "V2" : version, orgId);
        initAd(HomePageUtil.TEACHER, StringUtils.isBlank(version) ? "V2" : version, orgId);
    }

    public void initAllOrgAd() {
        List<OrgInfo> orgInfos = orgInfoRepository.findAll();
        if (orgInfos != null && orgInfos.size() > 0) {
            for (OrgInfo orgInfo : orgInfos) {
                initAllRoleAd("V2", orgInfo.getOrgId());
            }
        }
    }

    private List<HomeDomain> typeHomeDomain(List<HomePage> list) {
        List<HomeDomain> result = new ArrayList<>();
        if (list != null && list.size() > 0) {
            for (HomePage item : list) {
                if (item != null) {
                    HomeDomain d = new HomeDomain();
                    BeanUtils.copyProperties(item, d);
                    d.setLogoUrl("");
                    d.setLogoUrl2("");
                    result.add(d);
                }
            }
        }
        return result;
    }

    /**
     * 查询
     *
     * @return
     */
    public HomePage queryById(Long id) {
        return homePageRepository.findOne(id);
    }

    /**
     * 查询
     *
     * @param role
     * @param type
     * @param version
     * @return
     */
    public List<HomePage> query(String role, String type, String version, Long orgId) {
        if (orgId != null && orgId > 0) {
            List<HomePage> homepage = homePageRepository.findAllByRoleAndTypeAndVersionAndDeleteFlagAndOrgs(role, type, version, DataValidity.VALID.getState(), orgId.toString(), new Sort(new Order(Direction.ASC, "order")));
            return homepage;
        } else {
            List<HomePage> homepage = homePageRepository.findAllByRoleAndTypeAndVersionAndDeleteFlag(role, type, version, DataValidity.VALID.getState(), new Sort(new Order(Direction.ASC, "order")));
            return homepage;
        }
    }

    /**
     * 新增
     *
     * @param homePageDomain
     * @return
     */
    @CacheEvict(value = "CACHE.HOMEPAGE", allEntries = true)
    public Map<String, Object> add(HomePageDomain homePageDomain) {
        Map map = check(homePageDomain);

        if (null != map) {
            return map;
        }
        HomePage homePage = new HomePage();
        copyBean(homePageDomain, homePage);
        homePageRepository.save(homePage);
        if (homePage.getType().equals(HomePageUtil.TYPE_AD)) {
            initAllOrgAd();
        }
        return ApiReturn.message(Boolean.TRUE, null, null);
    }

    /**
     * 修改
     *
     * @param homePageDomain
     * @return
     */
    @CacheEvict(value = "CACHE.HOMEPAGE", allEntries = true)
    public Map<String, Object> update(HomePageDomain homePageDomain) {
        Map map = check(homePageDomain);
        if (null != map) {
            return map;
        }
        HomePage homePage = homePageRepository.findOne(homePageDomain.getId());
        if (null == homePage) {
            return ApiReturn.message(Boolean.FALSE, "未找到对应数据!", null);
        }
        copyBean(homePageDomain, homePage);
        homePageRepository.save(homePage);
        if (homePage.getType().equals(HomePageUtil.TYPE_AD)) {
            initAllOrgAd();
        }
        return ApiReturn.message(Boolean.TRUE, null, null);
    }

    /**
     * 属性拷贝
     *
     * @param homePageDomain
     * @param homePage
     */
    public void copyBean(HomePageDomain homePageDomain, HomePage homePage) {
        homePage.setDomainName("dd_mobile");
        homePage.setIconUrl(homePageDomain.getIconUrl());
        homePage.setIsNeedLogin(Boolean.TRUE);
        homePage.setIsRefresh(Boolean.FALSE);
        homePage.setIsStatusBar(Boolean.TRUE);
        homePage.setOrder(homePageDomain.getOrder() == null ? 1 : homePageDomain.getOrder());
        homePage.setRole(homePageDomain.getRole());
        homePage.setTargetTitle(homePageDomain.getTargetTitle());
        homePage.setTargetType(homePageDomain.getTargetType() == null ? "app" : homePageDomain.getTargetType());
        homePage.setTargetUrl(homePageDomain.getTargetUrl());
        homePage.setTitle(homePageDomain.getTitle());
        homePage.setType(homePageDomain.getType());
        homePage.setVersion("V2");
        homePage.setDeleteFlag(DataValidity.VALID.getState());
        homePage.setOnOff(homePageDomain.getOnOff() == null ? HomePageUtil.ONOFF_ON : homePageDomain.getOnOff());
        homePage.setOrgs(homePageDomain.getOrgs());
        homePage.setIsQuestionnaire(homePageDomain.getIsQuestionnaire());
    }

    /**
     * 校验
     *
     * @param homePageDomain
     * @return
     */
    public Map<String, Object> check(HomePageDomain homePageDomain) {
        String ban = homePageDomain.getType();
        if (!HomePageUtil.TYPE_BANNER.equals(ban) && !HomePageUtil.TYPE_MENU.equals(ban) && !HomePageUtil.TYPE_AD.equals(ban) && !HomePageUtil.TYPE_ALERT.equals(ban)) {
            return ApiReturn.message(Boolean.FALSE, "类型异常!", null);
        }
        String role = homePageDomain.getRole();
        if (!HomePageUtil.TEACHER.equals(role) && !HomePageUtil.STUDENT.equals(role)) {
            return ApiReturn.message(Boolean.FALSE, "角色异常!", null);
        }
        return null;
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @CacheEvict(value = "CACHE.HOMEPAGE", allEntries = true)
    public Map<String, Object> delete(Long id) {
        HomePage homePage = homePageRepository.findOne(id);
        homePage.setDeleteFlag(DataValidity.INVALID.getState());
        homePageRepository.save(homePage);
        if(homePage.getType().equals(HomePageUtil.TYPE_AD)){
            initAllOrgAd();
        }
        return ApiReturn.message(Boolean.TRUE, null, null);
    }

    /**
     * 交换顺序
     *
     * @param orders
     * @return
     */
    @CacheEvict(value = "CACHE.HOMEPAGE", allEntries = true)
    public Map<String, Object> sort(List<Long> orders) {
        if (null == orders || orders.isEmpty()) {
            return ApiReturn.message(Boolean.FALSE, "对象为空", null);
        }
        List<HomePage> homePages = new ArrayList<>();
        for (int i = 0, len = orders.size(); i < len; i++) {
            HomePage homePage = homePageRepository.findOne(orders.get(i));
            homePage.setOrder(i + 1);
            homePages.add(homePage);
        }
        homePageRepository.save(homePages);
        return ApiReturn.message(Boolean.TRUE, null, null);
    }
}
