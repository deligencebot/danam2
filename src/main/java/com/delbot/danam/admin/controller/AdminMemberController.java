package com.delbot.danam.admin.controller;

import java.util.List;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.delbot.danam.admin.dto.AdminMemberRequestDto;
import com.delbot.danam.admin.dto.AdminMemberResponseDto;
import com.delbot.danam.admin.exception.AdminErrorCode;
import com.delbot.danam.domain.blockInfo.BlockInfo;
import com.delbot.danam.domain.blockInfo.BlockInfoRepository;
import com.delbot.danam.domain.member.entity.Member;
import com.delbot.danam.domain.member.service.MemberService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/members")
@RequiredArgsConstructor
public class AdminMemberController {
  //
  private final MemberService memberService;
  private final BlockInfoRepository blockInfoRepository;

  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  @GetMapping("/management")
  public ResponseEntity<?> getMemberList() {
    List<Member> memberList = memberService.findAllMember();
    List<AdminMemberResponseDto> response = memberList.stream().map(AdminMemberResponseDto::mapper).toList();
    
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  @PostMapping("/management/ban")
  public ResponseEntity<?> banMember(@RequestBody AdminMemberRequestDto.Ban request) {
    Member member = memberService.findById(request.getMemberId());
    BlockInfo banInfo = BlockInfo.builder()
            .member(member)
            .blockExpirationTime(LocalDateTime.now().plusHours(request.getDuration()))
            .build();
    member.updateEnabled(false);
    memberService.addMember(member);
    blockInfoRepository.save(banInfo);

    List<Member> memberList = memberService.findAllMember();
    List<AdminMemberResponseDto> response = memberList.stream().map(AdminMemberResponseDto::mapper).toList();

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  @PostMapping("/management/release")
  public ResponseEntity<?> releaseMember(@RequestBody AdminMemberRequestDto.Id request) {
    Member member = memberService.findById(request.getMemberId());
    BlockInfo blockInfo = blockInfoRepository.findByMember(member).orElseThrow(() -> AdminErrorCode.NOT_BLOCKED_MEMBER.defaultException());

    blockInfoRepository.delete(blockInfo);
    member.updateEnabled(true);

    List<Member> memberList = memberService.findAllMember();
    List<AdminMemberResponseDto> response = memberList.stream().map(AdminMemberResponseDto::mapper).toList();

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  @PostMapping("/management/withdraw")
  public ResponseEntity<?> withdrawMember(@RequestBody AdminMemberRequestDto.Id request) {
    Member member = memberService.findById(request.getMemberId());
    memberService.deleteMember(member);

    List<Member> memberList = memberService.findAllMember();
    List<AdminMemberResponseDto> response = memberList.stream().map(AdminMemberResponseDto::mapper).toList();

    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
