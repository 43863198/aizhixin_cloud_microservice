package com.aizhixin.cloud.dd.dorms.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.common.core.DataValidity;
import com.aizhixin.cloud.dd.common.core.PageUtil;
import com.aizhixin.cloud.dd.common.domain.PageDomain;
import com.aizhixin.cloud.dd.dorms.domain.FloorDomain;
import com.aizhixin.cloud.dd.dorms.entity.Floor;
import com.aizhixin.cloud.dd.dorms.repository.BedStuRepository;
import com.aizhixin.cloud.dd.dorms.repository.FloorRepository;
import com.aizhixin.cloud.dd.dorms.repository.RoomAssginRepository;
import com.aizhixin.cloud.dd.dorms.repository.RoomRepository;
import com.aizhixin.cloud.dd.dorms.thread.CleanFloorInfoThread;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;

@Service
@Transactional
public class FloorSerivce {
	@Autowired
	private FloorRepository floorRepository;
	@Autowired
	private RoomRepository roomRepository;
	@Autowired
	private BedStuRepository bedStuRepository;
	@Autowired
	private RoomAssginRepository roomAssginRepository;

	
	public Floor findByNameAndOrgId(String name,Long orgId) {
		return floorRepository.findByNameAndOrgIdAndDeleteFlag(name,orgId,DataValidity.VALID.getState());
		
	}
	
	
	public Floor findByNameAndOrgId(String name,Long orgId,Long id) {
		return floorRepository.findByNameAndOrgIdAndDeleteFlagAndIdNot(name,orgId,DataValidity.VALID.getState(),id);
	}
	/**
	 * @Title: save
	 * @Description: 楼栋信息保存
	 * @param floorDomain
	 * @param adt
	 * @return: Floor
	 */
	public Floor save(FloorDomain floorDomain, AccountDTO adt) {
		Floor f = new Floor();
		BeanUtils.copyProperties(floorDomain, f);
		f.setId(null);
		f.setCreatedBy(adt.getId());
		f.setLastModifiedBy(adt.getId());
		f.setOrgId(adt.getOrganId());
		return floorRepository.save(f);
	}
	/**
	 * @Title: update
	 * @Description: 修改楼栋信息
	 * @param floorDomain
	 * @param adt
	 * @return: Floor
	 */
	public Floor update(FloorDomain floorDomain, AccountDTO adt) {
		Floor f = floorRepository.findByIdAndDeleteFlag(floorDomain.getId(), DataValidity.VALID.getState());
		if (null != f) {
			f.setName(floorDomain.getName());
			f.setFloorType(floorDomain.getFloorType());
			f.setFloorNum(floorDomain.getFloorNum());
			f.setUnitNum(floorDomain.getUnitNum());
			f.setFloorDesc(floorDomain.getFloorDesc());
			f.setFloorImage(floorDomain.getFloorImage());
			f.setLastModifiedBy(adt.getId());
			f.setLastModifiedDate(new Date());
			f = floorRepository.save(f);
		}
		return f;
	}
	/**
	 * @Title: deleteFloor
	 * @Description: 删除楼栋信息
	 * @return: void
	 */
	public Floor deleteFloor(Floor f) {
		f.setDeleteFlag(DataValidity.INVALID.getState());
		CleanFloorInfoThread ct=new CleanFloorInfoThread(roomRepository, bedStuRepository, f.getId(),roomAssginRepository);
		ct.start();
		return floorRepository.save(f);
	}
	
	/**
	 * 
	 * @Title: findById 
	 * @Description: 根据id查询楼栋信息
	 * @param id
	 * @return: Floor
	 */
	public Floor findById(Long id) {
		return floorRepository.findByIdAndDeleteFlag(id, DataValidity.VALID.getState());
	}
	
	/**
	 * 
	 * @Title: findByFloorInfo 
	 * @Description: 分页获取楼栋信息
	 * @param pageNumber
	 * @param pageSize
	 * @param orgId
	 * @param result
	 * @return: Map<String,Object>
	 */
	public Map<String, Object> findByFloorInfo(Integer pageNumber,Integer pageSize,Long orgId,Map<String, Object> result,String name) {
		Pageable page=PageUtil.createNoErrorPageRequest(pageNumber, pageSize);
		Page<Floor> fp=null;
		if(StringUtils.isEmpty(name)){
			 fp=floorRepository.findByOrgIdAndDeleteFlagOrderByCreatedDateDesc(page,orgId, DataValidity.VALID.getState());
		}else{
			 fp=floorRepository.findByOrgIdAndDeleteFlagAndNameLikeOrderByCreatedDateDesc(page,orgId, DataValidity.VALID.getState(),"%"+name+"%");
		}
		PageDomain pd=new PageDomain();
		pd.setPageNumber(fp.getNumber());
		pd.setPageSize(fp.getSize());
		pd.setTotalElements(fp.getTotalElements());
		pd.setTotalPages(fp.getTotalPages());
		List<Floor> fl=fp.getContent();
		List<FloorDomain> fdl=new ArrayList<>();
	    for (int i = 0; i < fl.size(); i++) {
	    	FloorDomain fd=new FloorDomain();
	    	fd.setNo((pageNumber-1)*pageSize+i+1);
	    	BeanUtils.copyProperties(fl.get(i),fd);
	    	fdl.add(fd);
		}
		result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
		result.put(ApiReturnConstants.DATA, fdl);
		result.put(ApiReturnConstants.PAGE, pd);
		return result;
	}
	
//	public boolean findByFloorId(Long floorId) {
//		List<Room>  rl=roomRepository.findByFloorIdAndDeleteFlag(floorId, DataValidity.VALID.getState());
//		if(null!=rl&&0<rl.size()){
//			return true;
//		}
//		return false;
//	}
}
