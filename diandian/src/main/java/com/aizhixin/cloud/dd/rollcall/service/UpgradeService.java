/**
 *
 */
package com.aizhixin.cloud.dd.rollcall.service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.aizhixin.cloud.dd.common.core.DataValidity;
import com.aizhixin.cloud.dd.constant.UpgradeConstants;
import com.aizhixin.cloud.dd.rollcall.entity.Upgrade;
import com.aizhixin.cloud.dd.rollcall.repository.UpgradeRepository;
import com.aizhixin.cloud.dd.rollcall.utils.Ftp;
import com.aizhixin.cloud.dd.rollcall.utils.FtpUtil;

/**
 * 升级管理
 *
 * @author zhen.pan
 */
@Service
@Transactional
public class UpgradeService {

    private final Logger log = LoggerFactory.getLogger(UpgradeService.class);

    @Autowired
    private UpgradeRepository upgradeRepository;

    @Autowired
    private Environment env;

    private static FTPClient ftp;

    public Object upgradeApk(String version, String versionDescrip,
                             String type, String role, MultipartFile mulFile, String isRequired, String isRemind) {

        Map result = new HashMap();
        Upgrade upgrade = new Upgrade();
        try {
            // 获取FTP地址，将apk上传到ftp服务器，返回 下载地址
            String ipAddr = env.getProperty("ftp.ipAddr", String.class, "172.16.23.30");
            int port = Integer.parseInt(env.getProperty("ftp.port", String.class, "21"));
            String userName = env.getProperty("ftp.userName", String.class, "admin");
            String password = env.getProperty("ftp.password", String.class, "admin");
            String path = "";
            if (UpgradeConstants.UPGRADE_ANDROID.equals(type)) {
                path = env.getProperty("ftp.androidPath", String.class, "Android");
            } else {
                path = env.getProperty("ftp.iosPath", String.class, "ios");
            }
            Integer buildNum = upgradeRepository.findMaxBuildNum();
            if (buildNum == null) {
                buildNum = new Integer(0);
            }
            buildNum = buildNum + 1;
            FtpUtil.connectFtp(new Ftp(ipAddr, port, userName, password, path));
            String name = buildNum + "_" + mulFile.getOriginalFilename();
            File file = new File(name);
            FileUtils.copyInputStreamToFile(mulFile.getInputStream(), file);
            FtpUtil.upload(file);
            FtpUtil.closeFtp();
            file.delete();
            String downloadPath = env.getProperty("ftp.downloadPath", String.class, "");
            if (StringUtils.isBlank(downloadPath)) {
                downloadPath = "ftp://" + ipAddr + ":" + port + "/" + path + "/" + name;
            } else {
                downloadPath += name;
            }
            upgrade.setBuildNumber(buildNum);
            upgrade.setVersion(version);
            upgrade.setVersionDescrip(versionDescrip);
            upgrade.setType(type);
            upgrade.setRole(role);
            upgrade.setDownloadUrl(downloadPath);
            upgrade.setDeleteFlag(DataValidity.VALID.getState());
            upgrade.setIsRequired("yes".equals(isRequired) ? true : false);
            upgrade.setIsRemind("yes".equals(isRemind) ? true : false);
            upgradeRepository.save(upgrade);
            result.put("trueMSG", true);
        } catch (Exception e) {
            e.printStackTrace();
            log.warn("app升级包上传失败", e);
            result.put("trueMSG", false);
            result.put("message", e);

        }
        return result;
    }

    @Cacheable(value = "CACHE.VERSIONINFO", key = "#role +'_'+ #type")
    public Map<String, Object> getLatestVersionInfo(String type, String role) {
        Integer buildNum = upgradeRepository.findMaxBuildNumAnd(type, role);
        if (null == buildNum) {
            return null;
        }
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            Upgrade upgrade = upgradeRepository
                    .findOneByTypeAndRoleAndBuildNumberAndDeleteFlag(type,
                            role, buildNum, DataValidity.VALID.getState());
            if (upgrade != null) {
                result.put("isRequired", upgrade.getIsRequired());
                result.put("version", upgrade.getVersion());
                result.put("url", upgrade.getDownloadUrl());
                result.put("versionDescrip", upgrade.getVersionDescrip());
                result.put("isRemind", upgrade.getIsRemind());
            } else {
                result.put("version", "there is no upgrade !");
            }
            result.put("trueMSG", true);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("trueMSG", false);
        }
        return result;
    }

}
