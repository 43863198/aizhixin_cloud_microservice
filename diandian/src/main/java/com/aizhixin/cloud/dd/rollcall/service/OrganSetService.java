package com.aizhixin.cloud.dd.rollcall.service;

import com.aizhixin.cloud.dd.rollcall.entity.OrganSet;
import com.aizhixin.cloud.dd.rollcall.repository.OrganSetRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class OrganSetService {

    @Autowired
    private OrganSetRepository organSetRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = "CACHE.LOGINORGAN")
    public OrganSet findByOrganId(Long organId) {
        OrganSet os = null;
        try {
            os = organSetRepository.findByOrganId(organId);
        } catch (Exception e) {
            log.warn("Exception", e);
        }
        if (null == os) {
            os = new OrganSet();
            os.setOrganId(organId);
            os.setCalcount(11);
            os.setAnti_cheating(Boolean.TRUE);
            os.setConfilevel(60);
            os.setDeviation(800);
            os.setArithmetic(10);
        } else {
            if (os.getArithmetic() == null || os.getArithmetic().intValue() == 0) {
                os.setArithmetic(10);
            }
        }

        return os;
    }

    public OrganSet getByOrganId(Long organId) {
        OrganSet os = null;
        try {
            os = organSetRepository.findByOrganId(organId);
        } catch (Exception e) {
            log.warn("Exception", e);
        }
        if (null == os) {
            os = new OrganSet();
            os.setCalcount(11);
            os.setAnti_cheating(Boolean.TRUE);
            os.setConfilevel(60);
            os.setDeviation(500);
            os.setArithmetic(10);
        } else {
            if (os.getArithmetic() == null || os.getArithmetic().intValue() == 0) {
                os.setArithmetic(10);
            }
        }
        return os;
    }

    public void deleteByOrganId(Long organId) {
        try {
            organSetRepository.deleteByOrganId(organId);
        } catch (Exception e) {
            log.warn("Exception", e);
        }
    }

    public void save(OrganSet set) {
        organSetRepository.save(set);
    }
}
