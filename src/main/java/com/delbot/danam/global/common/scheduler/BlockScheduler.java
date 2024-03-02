package com.delbot.danam.global.common.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.delbot.danam.domain.blockInfo.BlockInfo;
import com.delbot.danam.domain.blockInfo.BlockInfoRepository;
import com.delbot.danam.domain.member.entity.Member;
import com.delbot.danam.domain.member.service.MemberService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BlockScheduler {
  //
  private final MemberService memberService;
  private final BlockInfoRepository blockInfoRepository;

  @Transactional
  @Scheduled(fixedRate = 60 * 1000)
  public void checkBlockedMembersExpiration() {
    List<BlockInfo> expiredBlockInfoList = blockInfoRepository.findByExpirationTimeAfter(LocalDateTime.now());

    expiredBlockInfoList.forEach(blockInfo -> {
            Member member = memberService.findById(blockInfo.getId());
            member.updateEnabled(true);
            memberService.addMember(member);
            blockInfoRepository.delete(blockInfo);
    });
  }
}
