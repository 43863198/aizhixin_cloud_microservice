package com.aizhixin.cloud.ew.live.service;

import com.aizhixin.cloud.ew.live.domain.LiveCommentDomain;
import com.aizhixin.cloud.ew.live.domain.LiveContentDomain;
import com.aizhixin.cloud.ew.live.domain.LiveSubscriptionDomain;
import com.aizhixin.cloud.ew.live.entity.LiveComment;
import com.aizhixin.cloud.ew.live.entity.LiveContent;
import com.aizhixin.cloud.ew.live.entity.LiveSubscription;
import com.aizhixin.cloud.ew.live.jdbc.LiveQueryMessageJdbcTemplate;
import com.aizhixin.cloud.ew.live.jdbc.LiveQueryMessagePaginationSQL;
import com.aizhixin.cloud.ew.live.jdbc.LiveQueryNameAndStatusJdbcTemplate;
import com.aizhixin.cloud.ew.live.jdbc.LiveQueryNameAndStatusPaginationSQL;
import com.aizhixin.cloud.ew.live.jdbc.LiveQueryOkJdbcTemplate;
import com.aizhixin.cloud.ew.live.jdbc.LiveQueryOkPaginationSQL;
import com.aizhixin.cloud.ew.live.jdbc.LiveQueryRecentlyNoLiveJdbcTemplate;
import com.aizhixin.cloud.ew.live.repository.LiveCommentRepository;
import com.aizhixin.cloud.ew.live.repository.LiveRepository;
import com.aizhixin.cloud.ew.live.repository.LiveSubscriptionRepository;
import com.aizhixin.cloud.ew.common.dto.AccountDTO;
import com.aizhixin.cloud.ew.common.PageInfo;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Transactional
public class LiveService {

    @Autowired
    LiveRepository liveRepository;
    @Autowired
    LiveSubscriptionRepository liveSubscriptionRepository;

    @Autowired
    LiveCommentRepository liveCommentRepository;

    @Autowired
    LiveQueryRecentlyNoLiveJdbcTemplate liveQueryRecentlyNoLiveJdbcTemplate;

    @Autowired
    LiveQueryOkJdbcTemplate liveQueryOkJdbcTemplate;

    @Autowired
    LiveQueryMessageJdbcTemplate liveQueryMessageJdbcTemplate;

    @Autowired
    private LiveQueryNameAndStatusJdbcTemplate liveQueryNameAndStatusJdbcTemplate;




    public Map<String, Object> saveLiveContent(LiveContentDomain liveContentDomain, AccountDTO account,String status) throws ParseException {
  /*      SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Date videoTime = sdf.parse(video);*/
        Map<String, Object> result = new HashMap<>();
        String str = liveContentDomain.getData();
        JSONObject json = JSONObject.fromString(str);
        String video = (String) json.get("duration");

        LiveContent live = new LiveContent();
        live.setTitle(liveContentDomain.getTitle());
        live.setName(liveContentDomain.getName());
        live.setCoverPic(liveContentDomain.getCoverPic());
        live.setChildPic(liveContentDomain.getChildPic());
        live.setData(liveContentDomain.getData());
        live.setStatus(status);
        live.setLiveStatus(liveContentDomain.getLiveStatus());
        live.setPublishTime(liveContentDomain.getPublishTime());
        live.setUserId(liveContentDomain.getUserId());
        live.setTypeId(liveContentDomain.getTypeId());
        live.setOnlineNumber(0l);
        live.setVideoTime(video);
        LiveContent l = liveRepository.save(live);
        if (l != null) {
            result.put("result", "success");
        } else {
            result.put("result", "error");
        }
        return result;
    }

    public Map<String, Object> updateLiveContent(LiveContentDomain liveContentDomain, AccountDTO account, String status) {
        Map<String, Object> result = new HashMap<>();
        LiveContent live = new LiveContent();
        live.setTitle(liveContentDomain.getTitle());
        live.setName(liveContentDomain.getName());
        live.setCoverPic(liveContentDomain.getCoverPic());
        live.setChildPic(liveContentDomain.getChildPic());
        live.setData(liveContentDomain.getData());
        live.setStatus(status);
        live.setLiveStatus(liveContentDomain.getLiveStatus());
        live.setPublishTime(liveContentDomain.getPublishTime());
        live.setUserId(liveContentDomain.getUserId());
        live.setTypeId(liveContentDomain.getTypeId());
        live.setId(liveContentDomain.getId());
        LiveContent onelive = liveRepository.findOne(liveContentDomain.getId());
        if (onelive != null) {
            int i = liveRepository.updateLiveContent(liveContentDomain.getTitle(),liveContentDomain.getName(),liveContentDomain.getCoverPic(),
                    liveContentDomain.getChildPic(), liveContentDomain.getData(),liveContentDomain.getStatus(),liveContentDomain.getLiveStatus(),liveContentDomain.getPublishTime(), liveContentDomain.getUserId(),liveContentDomain.getTypeId(), liveContentDomain.getId());
            if (i > 0) {
                result.put("result", "update success");
            } else {
                result.put("result", "update error");
            }
        } else {
            result.put("result", "no find");
        }
        return result;

    }

    public Map<String, Object> openLive(Long liveId, String date) {
        Map<String, Object> result = new HashMap<>();
        LiveContent onelive = liveRepository.findOne(liveId);
        if("".equals(date)){
            date=new Date().toString();
        }
        Timestamp time = Timestamp.valueOf(date);
        if(onelive!=null){
            int i = liveRepository.updateLiveStatus(liveId,time);
            if(i>0){
                result.put("result", "open success");
            }
        }else{
            result.put("result", "no find");
        }
        return result;
    }







    public Map<String, Object> delLiveContent(Long liveId) {
        Map<String, Object> result = new HashMap<>();
        if (null == liveId || liveId <= 0) {
        	result.put("error", "id不能为空");
           return result;
        }
        liveRepository.delete(liveId);
        result.put("result", "success");
        return result;
    }




    public Page<LiveContent> queryNoLiveList(Integer pageNumber, Integer pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        if(pageNumber==null || pageNumber <= 0){
            pageNumber=0;
        }else {
            pageNumber-=1;
        }
        if(pageSize==null|| pageSize <= 0){
            pageSize=20;
        }
        Pageable pageable = new PageRequest(pageNumber, pageSize, sort);
        Page<LiveContent> list = liveRepository.findAllNoLiveContent(pageable);
        return list;
    }

    @SuppressWarnings({ "unchecked", "rawtypes", "static-access" })
	public PageInfo<LiveContentDomain> queryNameAndStatus(Integer pageNumber, Integer pageSize, String title, Integer typeId, Integer status) throws Exception {
        PageInfo pageInfo = liveQueryNameAndStatusJdbcTemplate.getPageInfo(pageSize, pageNumber, liveQueryNameAndStatusJdbcTemplate.liveContentMapper, null, new LiveQueryNameAndStatusPaginationSQL(title,typeId,status));
        return  pageInfo;
    }


    @SuppressWarnings({ "unchecked", "rawtypes", "static-access" })
	public PageInfo<LiveContentDomain> queryLiveList(Integer pageNumber, Integer pageSize,Integer status,Long userId) throws Exception {
        PageInfo pageInfo = liveQueryNameAndStatusJdbcTemplate.getPageInfo(pageSize, pageNumber, liveQueryOkJdbcTemplate.liveContentMapper, null, new LiveQueryOkPaginationSQL(status,userId));
        return pageInfo;
    }

    public int saveOnlineNumber(Long liveId) {
        return liveRepository.saveOnlineNumber(liveId);
    }


    public LiveContent queryLiveId(Long liveId) {
        return liveRepository.findOne(liveId);
    }

    public List<LiveContentDomain> recentlyNoLive() throws Exception {
        List<LiveContentDomain> liveContentDomains = liveQueryRecentlyNoLiveJdbcTemplate.queryList();
        return  liveContentDomains;
    }

    public Map<String, Object> saveLiveSubscription(LiveSubscriptionDomain liveSubscriptionDomain, AccountDTO account) {
        Map<String, Object> result = new HashMap<>();
        LiveSubscription liveSubscription=new LiveSubscription();
        liveSubscription.setStatus("1");
        liveSubscription.setTypeId(liveSubscriptionDomain.getTypeId());
        liveSubscription.setUserId(account.getId());
        liveSubscription.setVideoId(liveSubscriptionDomain.getVideoId());
        liveSubscription.setSubscriptionTime(liveSubscriptionDomain.getSubscriptionTime());
        LiveContent one = liveRepository.findOne(liveSubscriptionDomain.getVideoId());
        liveSubscription.setPublishTime(one.getPublishTime());
        LiveSubscription byUserIdAndVideoId = liveSubscriptionRepository.findByUserIdAndVideoId(account.getId(), liveSubscriptionDomain.getVideoId());
        if(byUserIdAndVideoId==null){
            LiveSubscription save = liveSubscriptionRepository.save(liveSubscription);
            if(save!=null){
                result.put("result", "success");
            }
        }else{
            int i = liveSubscriptionRepository.updateSubscriptionOne(account.getId(), liveSubscriptionDomain.getVideoId());
            if(i>0){
                result.put("result", "success");
            }
        }
        return result;
    }



    @SuppressWarnings("unused")
	public Map<String, Object> cancelLiveSubscription(LiveSubscriptionDomain liveSubscriptionDomain, Long id) {
        Map<String, Object> result = new HashMap<>();
        LiveSubscription liveSubscription=new LiveSubscription();
        int i = liveSubscriptionRepository.updateSubscription(id, liveSubscriptionDomain.getVideoId());
        if(i>0){
            result.put("result", "success");
        }else {
            result.put("result", "delete error");
        }
        return result;
    }

    public Map<String, Object> saveMessageComment(LiveCommentDomain liveCommentDomain, AccountDTO account) {

        Map<String, Object> result = new HashMap<>();
        LiveComment liveComment=new LiveComment();
        liveComment.setUserId(account.getId());
        liveComment.setText(liveCommentDomain.getText());
        liveComment.setName(account.getName());
        liveComment.setCommentTime(liveCommentDomain.getCommentTime());
        liveComment.setVideoId(liveCommentDomain.getVideoId());
        LiveComment save = liveCommentRepository.save(liveComment);
        if(save!=null){
            result.put("result", "success");
        }else{
            result.put("result", "error");
        }
        return result;
    }


    @SuppressWarnings({ "unchecked", "rawtypes", "static-access" })
	public PageInfo<LiveComment> queryMessageCommentList(Integer pageNumber, Integer pageSize, Long liveId) throws Exception {
        PageInfo pageInfo = liveQueryMessageJdbcTemplate.getPageInfo(pageSize, pageNumber, liveQueryMessageJdbcTemplate.liveContentMapper, null, new LiveQueryMessagePaginationSQL(liveId));
        return pageInfo;
    }
}
