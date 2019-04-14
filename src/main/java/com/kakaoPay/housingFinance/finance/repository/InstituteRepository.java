package com.kakaoPay.housingFinance.finance.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.kakaoPay.housingFinance.finance.model.Institute;


public interface InstituteRepository extends JpaRepository<Institute, Long>{

	@Query("SELECT i.name FROM Institute as i")
	List<String> findAllName();
	
	@Query("SELECT i.code FROM Institute as i where name = :name")
	String findCodeByName(String name);
}
