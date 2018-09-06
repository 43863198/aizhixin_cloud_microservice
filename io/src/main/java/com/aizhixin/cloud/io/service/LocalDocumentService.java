package com.aizhixin.cloud.io.service;

import com.aizhixin.cloud.io.auth.TokenAuth;
import com.aizhixin.cloud.io.common.core.PublicErrorCode;
import com.aizhixin.cloud.io.common.exception.CommonException;
import com.aizhixin.cloud.io.common.util.DateUtil;
import com.aizhixin.cloud.io.domain.LocalFileDomain;
import com.aizhixin.cloud.io.provider.store.redis.RedisTokenStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.UUID;

/**
 * Created by zhen.pan on 2017/6/13.
 */
@Component
public class LocalDocumentService {
    final static private Logger LOG = LoggerFactory.getLogger(LocalDocumentService.class);
    private final static String DEFAULT_BUCKET = "bucket";
    private RedisTokenStore redisTokenStore;
    private TokenAuth tokenAuth;

    @Value("${sys.base.dir}")
    private String baseDir;
//    @Autowired
//    public LocalDocumentService (RedisConnectionFactory redisConnectionFactory) {
//        redisTokenStore = new RedisTokenStore(redisConnectionFactory);
//        redisTokenStore.setPrefix("azx:io");
//    }

    @Autowired
    public LocalDocumentService (RedisConnectionFactory redisConnectionFactory, TokenAuth tokenAuth) {
        redisTokenStore = new RedisTokenStore(redisConnectionFactory);
        redisTokenStore.setPrefix("azx:io");
        this.tokenAuth = tokenAuth;
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
        System.out.println("---------------------" + fileName);
        String extName = "";
        int p = fileName.lastIndexOf(".");
        if (p > 0) {
            extName = fileName.substring(p + 1);
        }
        String key = UUID.randomUUID().toString().replaceAll("\\-", "").toUpperCase();
        String newfileName = key + "." + extName;
        File newfile = new File(dir, newfileName);
        LocalFileDomain d = new LocalFileDomain ();
        d.setKey(key);
        d.setOriginalFileName(fileName);
        d.setExtFileName(extName);
        if (null != ttl && ttl > 0) {
            d.setTtl(ttl * 1000 + System.currentTimeMillis());
        }
        BufferedOutputStream out = null;
        try {
//            file.transferTo(newfile);
            out = new BufferedOutputStream(new FileOutputStream(newfile));
            FileCopyUtils.copy(file.getInputStream(), out);
            out.flush();
            if (!newfile.exists() || !newfile.isFile() || newfile.length() <= 0) {
                throw new IOException ("After save file[" + newfile.toString() + "], not found in disk or size is 0");
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
                redisTokenStore.storeAccessToken(key, d);
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
        return d;
    }

    public LocalFileDomain getDocumentInfo(String key, String appId, String token) {
        validationAuthToken(appId, token);
        LocalFileDomain d = redisTokenStore.readToken(key);
        if (null != d && null != d.getTtl() && d.getTtl() > 0) {
            if (System.currentTimeMillis() > d.getTtl()) {
                return null;
            }
        }
        return d;
    }

    public ResponseEntity<byte[]> download(String key, String appId, String token) {
        validationAuthToken(appId, token);
        LocalFileDomain d = redisTokenStore.readToken(key);
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
//                HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
//                response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
//                response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(d.getOriginalFileName(), "UTF-8"));
//                Files.copy(Paths.get(f.getAbsolutePath()), response.getOutputStream());
            } catch (Exception e) {
                LOG.warn("File download Exception:", e);
                throw new CommonException(PublicErrorCode.SAVE_EXCEPTION.getIntValue(), "下载文件失败:" + (null != e ? e.getMessage(): "NullPointException"));
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new byte[]{});
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
}
