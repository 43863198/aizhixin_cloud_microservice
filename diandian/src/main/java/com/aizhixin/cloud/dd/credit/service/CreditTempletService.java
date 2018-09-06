package com.aizhixin.cloud.dd.credit.service;

import com.aizhixin.cloud.dd.common.core.DataValidity;
import com.aizhixin.cloud.dd.common.domain.PageData;
import com.aizhixin.cloud.dd.common.domain.PageDomain;
import com.aizhixin.cloud.dd.credit.domain.CreditTempletDomain;
import com.aizhixin.cloud.dd.credit.dto.CreditTempletDTO;
import com.aizhixin.cloud.dd.credit.dto.CreditTempletQuesDTO;
import com.aizhixin.cloud.dd.credit.entity.CreditTemplet;
import com.aizhixin.cloud.dd.credit.entity.CreditTempletQues;
import com.aizhixin.cloud.dd.credit.repository.CreditTempletQuesRepository;
import com.aizhixin.cloud.dd.credit.repository.CreditTempletRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CreditTempletService {

    @Autowired
    private CreditTempletRepository templetRepository;
    @Autowired
    private CreditTempletQuesRepository quesRepository;

    public void initTemplet(Long orgId) {

    }

    public List<CreditTemplet> findTempletByOrgid(Long orgId) {
        return templetRepository.findByOrgIdAndDeleteFlagOrderByIdDesc(orgId, DataValidity.VALID.getState());
    }

    public PageData<CreditTemplet> findTempletPageByOrgid(Pageable pageable, Long orgId) {
        Page<CreditTemplet> page = templetRepository.findByOrgIdAndDeleteFlagOrderByIdDesc(pageable, orgId, DataValidity.VALID.getState());
        PageData<CreditTemplet> pageData = new PageData<>();
        pageData.setData(page.getContent());
        PageDomain pageDomain = new PageDomain();
        pageDomain.setPageSize(page.getSize());
        pageDomain.setPageNumber(page.getNumber());
        pageDomain.setTotalElements(page.getTotalElements());
        pageDomain.setTotalPages(page.getTotalPages());
        pageData.setPage(pageDomain);
        return pageData;
    }

    public CreditTempletDomain findTempletById(Long templetId) {
        CreditTempletDomain domain = new CreditTempletDomain();
        CreditTemplet templet = templetRepository.findOne(templetId);
        if (templet != null) {
            BeanUtils.copyProperties(templet, domain);
            List list = quesRepository.findByTempletIdAndDeleteFlag(templet.getId(), DataValidity.VALID.getState());
            domain.setQuesList(list);
        }
        return domain;
    }

    @Transactional
    public void saveTemplet(CreditTempletDTO dto) {
        //原模板标记删除，创建新模板
        if (dto.getId() != null && dto.getId() > 0) {
            CreditTemplet templet = templetRepository.findOne(dto.getId());
            if (templet != null) {
                templet.setDeleteFlag(DataValidity.INVALID.getState());
                templetRepository.save(templet);
            }
        }
        CreditTemplet templet = new CreditTemplet();
        templet.setOrgId(dto.getOrgId());
        templet.setName(dto.getName());
        templet = templetRepository.save(templet);
        Float totalScore = 0f;
        List<CreditTempletQues> ques = new ArrayList<>();
        if (dto.getQuesList() != null && dto.getQuesList().size() > 0) {
            for (CreditTempletQuesDTO quesDTO : dto.getQuesList()) {
                CreditTempletQues q = new CreditTempletQues();
                BeanUtils.copyProperties(quesDTO, q);
                q.setTempletId(templet.getId());
                totalScore += q.getMaxScore();
                ques.add(q);
            }
            quesRepository.save(ques);
        }
        templet.setTotalScore(totalScore);
        templetRepository.save(templet);
    }

    @Transactional
    public void deleteTemplet(Long templetId) {
        if (templetId != null && templetId > 0) {
            CreditTemplet templet = templetRepository.findOne(templetId);
            if (templet != null) {
                templet.setDeleteFlag(DataValidity.INVALID.getState());
                templetRepository.save(templet);
            }
        }
    }

}
