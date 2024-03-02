package com.delbot.danam.domain.blockInfo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.delbot.danam.domain.member.entity.Member;

public interface BlockInfoRepository extends JpaRepository<BlockInfo, Long> {
  //
  @Query("SELECT b FROM BlockInfo b WHERE b.blockExpirationTime < :now")
  List<BlockInfo> findByExpirationTimeAfter(LocalDateTime now);
  Optional<BlockInfo> findByMember(Member member);
}
