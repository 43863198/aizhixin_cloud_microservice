/**
 * 文件资源存储API调用
 */
package com.aizhixin.cloud.dd.rollcall.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.ByteArrayPartSource;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.PartSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aizhixin.cloud.dd.common.utils.ConfigCache;
import com.aizhixin.cloud.dd.rollcall.dto.IODTO;

@Service
@Transactional
public class IOUtil {
    private final Logger log = LoggerFactory.getLogger(IOUtil.class);
    private HttpClient client = new HttpClient();
    @Autowired
    private ConfigCache configCache;

    public IODTO upload(String fileName, byte[] avatarData) {
        if (avatarData == null)
            return null;

        String apiURL = configCache.getConfigValueByParm("common.service.host")
                + configCache.getConfigValueByParm("common.service.resUpload");
        final PostMethod post = new PostMethod(apiURL);
        try {
            UUID uuid = UUID.randomUUID();
            String[] nameParts = fileName.split("\\.");
            String fileExtension = nameParts.length > 1 ? "." + nameParts[1]
                    : "";
            PartSource byteSource = new ByteArrayPartSource(uuid.toString()
                    + fileExtension, avatarData);
            FilePart filePart = new FilePart("file", byteSource);
            Part[] parts = {filePart};
            MultipartRequestEntity mre = new MultipartRequestEntity(parts, post.getParams());
            post.setRequestEntity(mre);
            client.executeMethod(post);
            final String response = post.getResponseBodyAsString();
            switch (post.getStatusCode()) {
                case 200:
                case 201:
                    log.info("update image file success.");
                    JSONObject jsonObj = JSONObject.fromString(response);
                    Map<String, Class> keys = new HashMap<String, Class>();
                    IODTO ioDTO = new IODTO();
                    ioDTO.setFileName(jsonObj.getString("fileName"));
                    ioDTO.setFileUrl(jsonObj.getString("fileUrl"));
                    ioDTO.setFileMD5code(jsonObj.getString("fileMD5code"));
                    return ioDTO;
                default:
                    log.warn("Invalid response code (" + post.getStatusCode() + ") from common api service!");
                    break;
            }
        } catch (Exception e) {
            log.warn("Exception", e);
        } finally {
            post.releaseConnection();
        }
        return null;
    }
}