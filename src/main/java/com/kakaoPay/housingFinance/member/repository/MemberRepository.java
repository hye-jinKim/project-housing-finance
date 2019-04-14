package com.kakaoPay.housingFinance.member.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kakaoPay.housingFinance.member.model.Member;

public interface MemberRepository extends JpaRepository<Member, String> {
	@Query(value = "SELECT m.userId FROM member as m where m.userId = :userId and m.password = :password", nativeQuery = true)
	String findMemberByIdPassword(@Param("userId") String userId, @Param("password") String password);

	@Query(value = "SELECT m.token FROM member as m where m.userId = :userId and m.token = :token", nativeQuery = true)
	String findByIdAndToken(@Param("userId") String userId, @Param("token") String token);

	@Modifying
	@Transactional
	@Query(value = "UPDATE member as m SET m.token = :token where m.userId = :userId", nativeQuery = true)
	void update(@Param("userId") String userId, @Param("token") String token);
}
