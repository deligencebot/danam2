package com.delbot.danam.domain.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.delbot.danam.domain.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
  //
  Optional<Member> findByName(String name);
  Optional<Member> findByNickname(String name);
  Optional<Member> findByEmail(String email);
}
