package com.aizhixin.cloud.io.service.v2;

import com.aizhixin.cloud.io.LocalFileManager;
import com.aizhixin.cloud.io.auth.TokenAuth;
import com.aizhixin.cloud.io.common.core.PublicErrorCode;
import com.aizhixin.cloud.io.common.exception.CommonException;
import com.aizhixin.cloud.io.common.util.DateUtil;
import com.aizhixin.cloud.io.domain.LocalFileDomain;
import com.aizhixin.cloud.io.entity.LocalFile;
import com.aizhixin.cloud.io.provider.store.redis.RedisTokenStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;

/**
 * Created by zhen.pan on 2017/6/13.
 */
@Component
@Transactional
public class LocalDocumentV2Service {
    final static private Logger LOG = LoggerFactory.getLogger(LocalDocumentV2Service.class);
    private final static String DEFAULT_BUCKET = "bucket";
    private RedisTokenStore redisTokenStore;
    private TokenAuth tokenAuth;
    private LocalFileManager localFileManager;

    @Value("${sys.base.dir}")
    private String baseDir;

    @Autowired
    public LocalDocumentV2Service(RedisConnectionFactory redisConnectionFactory, TokenAuth tokenAuth, LocalFileManager localFileManager) {
        this.redisTokenStore = new RedisTokenStore(redisConnectionFactory);
        this.redisTokenStore.setPrefix("azx:io");
        this.tokenAuth = tokenAuth;
        this.localFileManager = localFileManager;
    }

    private void validationAuthToken(String appId, String token) {
        tokenAuth.authTokean(appId, token);
    }

    private File createBaseDir(String bucket) {
        File dir = null;
        StringBuilder sb = new StringBuilder(baseDir);
        if (!baseDir.endsWith(File.separator)) {
            sb.append(File.separator);
        }
        sb.append("default").append(File.separator);
        if (StringUtils.isEmpty(bucket)) {
            sb.append(DEFAULT_BUCKET);
        } else {
            sb.append(bucket);
        }
        sb.append(File.separator).append(DateUtil.getCurrentDate());
        dir = new File(sb.toString());
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public LocalFileDomain uploadDocument(MultipartFile file, String filename, String bucket, Long ttl, String appId, String token) {
        validationAuthToken(appId, token);
        File dir = createBaseDir(bucket);

        String fileName = file.getOriginalFilename();
        if (!StringUtils.isEmpty(filename)) {
            try {
                fileName = URLDecoder.decode(filename, "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String extName = "";
        int p = fileName.lastIndexOf(".");
        if (p > 0) {
            extName = fileName.substring(p + 1);
        }
        String key = UUID.randomUUID().toString().replaceAll("\\-", "").toUpperCase();
        String newfileName = key + "." + extName;
        File newfile = new File(dir, newfileName);
        LocalFile d = new LocalFile();
        d.setKey(key);
        d.setOriginalFileName(fileName);
        d.setExtFileName(extName);
        if (null != ttl && ttl > 0) {
            d.setTtl(ttl * 1000 + System.currentTimeMillis());
        }
        BufferedOutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(newfile));
            FileCopyUtils.copy(file.getInputStream(), out);
            out.flush();
            if (!newfile.exists() || !newfile.isFile() || newfile.length() <= 0) {
                throw new IOException ("After save file[" + newfile.toString() + "], not found in disk or size is 0");
            }
        } catch (Exception e) {
            LOG.warn("File save Exception:", e);
            throw new CommonException(PublicErrorCode.SAVE_EXCEPTION.getIntValue(), "保存文件失败:" + (null != e ? e.getMessage(): "NullPointException"));
        }
        finally {
            if (null != out) {
                try {
                    out.close();
                } catch (IOException e) {
                    LOG.warn("File close Exception:", e);
                    throw new CommonException(PublicErrorCode.SAVE_EXCEPTION.getIntValue(), "保存文件失败:" + (null != e ? e.getMessage(): "NullPointException"));
                }
            }
        }
        newfileName = newfile.toString();
        if (newfileName.length() > baseDir.length()) {
            int start = 0;
            if (!baseDir.endsWith(File.separator)) {
                start = baseDir.length() + File.separator.length();
            } else {
                start = baseDir.length();
            }
            newfileName = newfileName.substring(start);
            d.setFilePath(newfileName);
        }
        d = localFileManager.save(d);
        LOG.info("Upload file to local success:{}, file name:{}", newfile.toString(), filename);
        LocalFileDomain fd = new LocalFileDomain();
        fd.setTtl(d.getTtl());
        fd.setOriginalFileName(d.getOriginalFileName());
        fd.setFilePath(d.getFilePath());
        fd.setExtFileName(d.getExtFileName());
        fd.setKey(d.getKey());
        return fd;
    }



    @Transactional(readOnly = true)
    public LocalFileDomain getDocumentInfo(String key, String appId, String token) {
        validationAuthToken(appId, token);
        LocalFileDomain d = redisTokenStore.readToken(key);
//        if (null != d && null != d.getTtl() && d.getTtl() > 0) {
//            if (System.currentTimeMillis() > d.getTtl()) {
//                return null;
//            }
//        }
        if (null == d) {
            d = findByKey(key);
        }
        return d;
    }

    private LocalFileDomain findByKey(String key) {
        LocalFileDomain t = null;
        List<LocalFile> list = localFileManager.findByKey(key);
        if (null != list && list.size() > 0) {
            LocalFile src = list.get(0);
            t =  new LocalFileDomain();
            t.setKey(src.getKey());
            t.setExtFileName(src.getExtFileName());
            t.setFilePath(src.getFilePath());
            t.setOriginalFileName(src.getOriginalFileName());
            t.setTtl(src.getTtl());
            redisTokenStore.storeAccessToken(t.getKey(), t);
        }
        return t;
    }


    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> download(String key, String appId, String token) {
        validationAuthToken(appId, token);
        LocalFileDomain d = redisTokenStore.readToken(key);
        if (null == d) {
            d = findByKey(key);
        }
        if (null != d) {
            if (null != d.getTtl() && d.getTtl() > 0) {
                if (System.currentTimeMillis() > d.getTtl()) {
                    return ResponseEntity.ok().body(new byte[]{});
                }
            }
            FileInputStream fs = null;
            FileChannel channel = null;
            try {
                File f = new File(baseDir, d.getFilePath());
                fs = new FileInputStream(f);
                channel = fs.getChannel();
                ByteBuffer byteBuffer = ByteBuffer.allocate((int) channel.size());
                while ((channel.read(byteBuffer)) > 0) {
                }
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).header("Content-Disposition", "attachment; filename=" + URLEncoder.encode(d.getOriginalFileName(), "UTF-8")).body(byteBuffer.array());
            } catch (Exception e) {
                LOG.warn("File download Exception:", e);
                throw new CommonException(PublicErrorCode.SAVE_EXCEPTION.getIntValue(), "下载文件失败:" + (null != e ? e.getMessage(): "NullPointException"));
            } finally {
                if (null != channel) {
                    try {
                        channel.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                if (null != fs)
                    try {
                        fs.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
        }
        return ResponseEntity.ok().body(new byte[]{});
    }

    private void copyData( List<LocalFile>  localFileList, Set<String> bkeys) {
        List<LocalFileDomain> list = redisTokenStore.readKeys(bkeys);
        if (list.size() > 0) {
            for (LocalFileDomain d : list) {
                LocalFile f = new LocalFile();
                f.setKey(d.getKey());
                f.setExtFileName(d.getExtFileName());
                f.setOriginalFileName(d.getOriginalFileName());
                f.setTtl(d.getTtl());
                f.setFilePath(d.getFilePath());
                localFileList.add(f);
            }
        }
    }

    public int loadFromRedisToDB() {
        Set<String> keys = redisTokenStore.readAllKeys();
        LOG.info("Read all data size :{}", keys.size());
        List<LocalFile>  localFileList = new ArrayList<>();
        //以下分批加载数据，防止redis超时
        if (keys.size() > 100) {
            Set<String> bkeys = new HashSet<>();
            int p = 1;
            for (String k : keys) {
                bkeys.add(k);
                if (0 == p%100) {
                    copyData(localFileList, bkeys);
                    bkeys.clear();
                }
                p++;
            }
            if (bkeys.size() > 0) {
                copyData(localFileList, bkeys);
            }
        } else {
            if (!keys.isEmpty()) {
                copyData(localFileList, keys);
            }
        }
        if (!localFileList.isEmpty()) {
            localFileManager.saveAll(localFileList);
        }
        return  localFileList.size();
    }

    public String addExistFile(LocalFileDomain localFileDomain) {
        if (null == localFileDomain) {
            throw new CommonException(PublicErrorCode.PARAM_EXCEPTION.getIntValue(), "缺少必须的参数");
        }
        if (StringUtils.isEmpty(localFileDomain.getKey())) {
            throw new CommonException(PublicErrorCode.PARAM_EXCEPTION.getIntValue(), "KEY是必须的");
        }
        if (StringUtils.isEmpty(localFileDomain.getFilePath())) {
            throw new CommonException(PublicErrorCode.PARAM_EXCEPTION.getIntValue(), "文件的相对路径是必须的");
        }

        if (StringUtils.isEmpty(localFileDomain.getOriginalFileName())) {
            throw new CommonException(PublicErrorCode.PARAM_EXCEPTION.getIntValue(), "文件名是必须的");
        }
        StringBuilder dir = new StringBuilder(baseDir);
        if (!baseDir.endsWith(File.separator)) {
            dir.append(File.separator);
        }
        dir.append(localFileDomain.getFilePath());
        File file = new File(dir.toString());
        if (!file.exists()) {
            if (StringUtils.isEmpty(localFileDomain.getOriginalFileName())) {
                throw new CommonException(PublicErrorCode.PARAM_EXCEPTION.getIntValue(), "文件:(" + file.toString() + ")不存在");
            }
        }
        List<LocalFile> list = localFileManager.findByKey(localFileDomain.getKey());
        if (null != list && list.size() > 0) {
            throw new CommonException(PublicErrorCode.PARAM_EXCEPTION.getIntValue(), "该Key值已经存在");
        }
        LocalFile localFile = new LocalFile();
        localFile.setFilePath(localFileDomain.getFilePath());
        localFile.setOriginalFileName(localFileDomain.getOriginalFileName());
        localFile.setKey(localFileDomain.getKey());
        int p = localFileDomain.getFilePath().lastIndexOf(".");
        if (p > 0) {
            localFile.setExtFileName(localFileDomain.getFilePath().substring(p + 1));
        }
        localFileManager.save(localFile);
        return localFile.getKey();
    }
}
