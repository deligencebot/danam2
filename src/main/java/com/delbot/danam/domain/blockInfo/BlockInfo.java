package com.delbot.danam.domain.blockInfo;

import java.time.LocalDateTime;

import com.delbot.danam.domain.member.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_block_info")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class BlockInfo {
  //
  @Id
  @Column(name = "block_info_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  private LocalDateTime blockExpirationTime;

  @Builder
  public BlockInfo(Member member, LocalDateTime blockExpirationTime) {
    this.member = member;
    this.blockExpirationTime = blockExpirationTime;
  }

  public boolean isExpired() {
    return getBlockExpirationTime() != null && getBlockExpirationTime().isBefore(LocalDateTime.now());
  }
}
