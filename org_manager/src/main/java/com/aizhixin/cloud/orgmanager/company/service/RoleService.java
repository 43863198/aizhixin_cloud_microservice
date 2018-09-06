package com.aizhixin.cloud.orgmanager.company.service;

import com.aizhixin.cloud.orgmanager.common.core.DataValidity;
import com.aizhixin.cloud.orgmanager.company.dto.RoleDTO;
import com.aizhixin.cloud.orgmanager.company.entity.Role;
import com.aizhixin.cloud.orgmanager.company.repository.RoleRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: Created by jianwei.wu
 * @E-mail: wujianwei@aizhixin.com
 * @Date: 2018-03-30
 */
@Service
@Transactional
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public Map<String,Object> getRoleById(Long id){
        Map<String,Object> result = new HashMap<>();
        try {
            Role role = this.roleRepository.findOne(id);
            result.put("success",true);
            result.put("data",role);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            result.put("success",false);
            result.put("message","角色获取失败");
            return result;
        }
    }

    public Map<String,Object> save(RoleDTO roleDTO){
        Map<String,Object> result = new HashMap<>();
        try {
            Role r = null;
            if(null!=roleDTO.getId()) {
                r = this.roleRepository.findOne(roleDTO.getId());
            }
            if(null!=r){
                if(!StringUtils.isBlank(roleDTO.getRoleName())){
                    r.setRoleName(roleDTO.getRoleName());
                }
                if(!StringUtils.isBlank(roleDTO.getRoleGroup())){
                    r.setRoleGroup(roleDTO.getRoleGroup());
                }
                if(!StringUtils.isBlank(roleDTO.getRoleDescribe())){
                    r.setRoleDescribe(roleDTO.getRoleDescribe());
                }
                r.setLastModifiedDate(new Date());
                this.roleRepository.save(r);
            }else {
                if(null==roleDTO.getRoleName()){
                    result.put("success",false);
                    result.put("message","角色名称为空");
                    return result;
                }
                if(null==roleDTO.getRoleGroup()){
                    result.put("success",false);
                    result.put("message","角色分组为空");
                    return result;
                }
                r = new Role();
                r.setRoleName(roleDTO.getRoleName());
                r.setRoleGroup(roleDTO.getRoleGroup());
                r.setRoleDescribe(roleDTO.getRoleDescribe());
                r.setCreatedDate(new Date());
                r.setLastModifiedDate(new Date());
                this.roleRepository.save(r);
            }
            result.put("success",true);
            result.put("message","角色保存成功");
            return result;
        }catch (Exception e){
            e.printStackTrace();
            result.put("success",false);
            result.put("message","角色保存失败");
            return result;
        }
    }


    public Map<String,Object> delete(Long id){
        Map<String,Object> result = new HashMap<>();
        try {
            Role r = this.roleRepository.findOne(id);
            r.setDeleteFlag(DataValidity.INVALID.getState());
            r.setLastModifiedDate(new Date());
            this.roleRepository.save(r);
            result.put("success",true);
            result.put("message","角色删除成功");
            return result;
        }catch (Exception e){
            e.printStackTrace();
            result.put("success",false);
            result.put("message","角色删除失败");
            return result;
        }
    }

    @Transactional(readOnly = true)
    public Map<String,Object> findAllByRoleGroup(String roleGroup){
        Map<String,Object> result = new HashMap<>();
        try {
            List<Role> roleList = this.roleRepository.findByRoleGroupAndDeleteFlag(roleGroup, DataValidity.VALID.getState());
            result.put("success",true);
            result.put("data",roleList);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            result.put("success",false);
            result.put("message","根据角色组名称获取对应的所有角色失败");
            return result;
        }
    }










}
