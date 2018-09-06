package com.aizhixin.cloud.orgmanager.training.service;

import com.aizhixin.cloud.orgmanager.common.PageData;
import com.aizhixin.cloud.orgmanager.common.core.PageUtil;
import com.aizhixin.cloud.orgmanager.electrict.dto.ElectricFenceStatisticsDTO;
import com.aizhixin.cloud.orgmanager.electrict.entity.ElectricFenceStatistics;
import com.aizhixin.cloud.orgmanager.training.entity.Enterprise;
import com.aizhixin.cloud.orgmanager.training.repository.EnterpriseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Entity;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: Created by jianwei.wu
 * @E-mail: wujianwei@aizhixin.com
 * @Date: 2018-03-15
 */
@Service
@Transactional
public class EnterpriseService {
    @Autowired
    private EnterpriseRepository enterpriseRepository;
    @Autowired
    private EntityManager em;

    public Enterprise findById(Long id){
    	return enterpriseRepository.findOne(id);
    }

    /**
     * 获取企业信息
     * @param name
     */
    public Map<String,Object> getList(Long orgId, String name,Pageable pageable){
        Map<String,Object> result = new HashMap<>();
        Map<String, Object> condition = new HashMap<>();
        PageData<Enterprise> data = new PageData<>();
        try{
            StringBuilder cql = new StringBuilder("SELECT count(1) FROM t_enterprise e WHERE 1 = 1");
            StringBuilder sql = new StringBuilder("SELECT e.* FROM t_enterprise e WHERE 1 = 1");
            if(null!=orgId){
                cql.append(" AND e.ORG_ID = :orgId");
                sql.append(" AND e.ORG_ID = :orgId");
                condition.put("orgId",orgId);
            }
            if(null!=name&&!name.equals("")){
                cql.append(" AND e.NAME like :name");
                sql.append(" AND e.NAME like :name");
                condition.put("name", "%" + name + "%");
            }
            Query cq = em.createNativeQuery(cql.toString());
            Query sq = em.createNativeQuery(sql.toString(), Enterprise.class);
            for (Map.Entry<String, Object> e : condition.entrySet()) {
                cq.setParameter(e.getKey(), e.getValue());
                sq.setParameter(e.getKey(), e.getValue());
            }
            Long count = Long.valueOf(String.valueOf(cq.getSingleResult()));
            sq.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
            sq.setMaxResults(pageable.getPageSize());
            data.setData(sq.getResultList());
            data.getPage().setTotalElements(count);
            data.getPage().setPageNumber(pageable.getPageNumber());
            data.getPage().setPageSize(pageable.getPageSize());
            data.getPage().setTotalPages(PageUtil.cacalatePagesize(count, pageable.getPageSize()));
            result.put("success", true);
            result.put("data",data);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            result.put("success",false);
            result.put("message","获取企业信息失败！");
            return result;
        }

    }

    /**
     * 保存企业信息
     * @param enterprise
     */
    public Map<String,Object> save(Enterprise enterprise){
        Map<String,Object> result = new HashMap<>();
        try{
            enterpriseRepository.save(enterprise);
            result.put("success", true);
            result.put("message","企业信保存成功！");
            return result;
        }catch (Exception e){
            e.printStackTrace();
            result.put("success",false);
            result.put("message","企业信保存失败！");
            return result;
        }

    }

    /**
     * 更新企业信息
     * @param e
     */
    public Map<String,Object> update(Enterprise e){
        Map<String,Object> result = new HashMap<>();
        try{
            Enterprise enterprise = enterpriseRepository.findOne(e.getId());
            if(null!=e.getName()){
                enterprise.setName(e.getName());
            }
            if(null!=e.getProvince()){
                enterprise.setProvince(e.getProvince());
            }
            if(null!=e.getCity()){
                enterprise.setCity(e.getCity());
            }
            if(null!=e.getCounty()){
                enterprise.setCounty(e.getCounty());
            }
            if(null!=e.getAddress()){
                enterprise.setAddress(e.getAddress());
            }
            if(null!=e.getMailbox()){
                enterprise.setMailbox(e.getMailbox());
            }
            if(null!=e.getTelephone()){
                enterprise.setTelephone(e.getTelephone());
            }
            enterpriseRepository.save(enterprise);
            result.put("success", true);
            result.put("message","企业信更新成功！");
            return result;
        }catch (Exception ex){
            ex.printStackTrace();
            result.put("success",false);
            result.put("message","企业信更新失败！");
            return result;
        }
    }

    /**
     * 删除企业信息
     * @param id
     */
    public Map<String,Object> delete(Long id){
        Map<String,Object> result = new HashMap<>();
        try{
            enterpriseRepository.delete(id);
            result.put("success", true);
            result.put("message","企业信删除成功！");
            return result;
        }catch (Exception e){
            e.printStackTrace();
            result.put("success",false);
            result.put("message","企业信删除失败！");
            return result;
        }

    }





}
