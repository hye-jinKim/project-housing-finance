package com.kakaoPay.housingFinance.finance.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kakaoPay.housingFinance.finance.model.Support;

public interface SupportRepository extends JpaRepository<Support, Long> {

	@Query("SELECT NEW Support(s.year, sum(s.amount)) FROM Support as s group by s.year")
	List<Support> findTotalAmountByYear(Sort sort);

	@Query("SELECT NEW Support(s.bank, sum(s.amount)) FROM Support as s where s.year = :year group by s.bank")
	List<Support> findDetailAmountByYear(@Param("year") int year);

	@Query("SELECT NEW Support(s.year, s.bank) FROM Support as s group by s.year, s.bank order by sum(s.amount) desc")
	List<Support> findInstituteByMostSupportAmounts(Pageable pageable);

	@Query("SELECT NEW Support(s.year, round(avg(s.amount))) FROM Support as s "
			+ "where s.bank = :bank and year between :start and :end " + "group by s.bank, s.year "
			+ "order by round(avg(s.amount)) asc")
	List<Support> findAverageAmountBy(@Param("bank") String bank, @Param("start") int start, @Param("end") int end);
	
	@Query(value = " select s.id, i.code as bank, s.year, s.month, s.amount from support s" + 
			" inner join institute i" + 
			" on s.bank = i.name", nativeQuery = true)
	List<Support> findAllInnerCode();
}
