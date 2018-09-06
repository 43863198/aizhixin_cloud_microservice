package com.aizhixin.cloud.studentpractice.summary.repository;



import org.springframework.data.jpa.repository.JpaRepository;

import com.aizhixin.cloud.studentpractice.summary.entity.EnterpriseCount;




public interface EnterpriseCountRepository extends JpaRepository<EnterpriseCount, String> {
	
}
