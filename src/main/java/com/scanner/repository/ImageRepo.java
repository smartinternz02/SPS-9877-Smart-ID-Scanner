package com.scanner.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.scanner.entity.ImageClass;
import com.scanner.entity.User;
public interface ImageRepo extends JpaRepository<ImageClass, Integer> {

	@Query("from ImageClass as c where c.user.userid=:id")
	public Page<ImageClass> findByUser(@Param("id")int id,Pageable pageable);
	//searching of contact
	
	
}
