package com.aizhixin.cloud.dd.rollcall.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import com.aizhixin.cloud.dd.rollcall.entity.Announcement;
import com.aizhixin.cloud.dd.rollcall.entity.AnnouncementFile;

public interface AnnouncementFileRepository extends JpaRepository<AnnouncementFile, Long>{
	@Modifying
	public void deleteByAnnouncement(Announcement  a);
	
	public List<AnnouncementFile> findByAnnouncement_id(Long id);
	
}
