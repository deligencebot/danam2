package com.delbot.danam.admin.dto;

import java.time.LocalDateTime;
import java.util.List;
import com.delbot.danam.domain.member.entity.Member;
import com.delbot.danam.domain.role.Role;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Data;

@Data
public class AdminMemberResponseDto {
  //
  private Long memberId;
  private String name;
  private String nickname;
  private String email;
  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
  private LocalDateTime createdDate;
  private String role;
  private boolean isEnable;

  @Builder
  public AdminMemberResponseDto(Long memberId, String name, String nickname, String email, LocalDateTime createdDate, String role, boolean isEnable) {
    this.memberId = memberId;
    this.name = name;
    this.nickname = nickname;
    this.email = email;
    this.createdDate = createdDate;
    this.role = role;
    this.isEnable = isEnable;
  }

  public static AdminMemberResponseDto mapper(Member member) {
    return AdminMemberResponseDto.builder()
            .memberId(member.getMemberId())
            .name(member.getName())
            .nickname(member.getNickname())
            .email(member.getEmail())
            .createdDate(member.getCreatedDate())
            .role(sortRole(member.getRoles().stream().map(Role::getName).toList()))
            .isEnable(member.isEnabled())
            .build();
  }

  private static String sortRole(List<String> roles) {
    if (roles.contains("ROLE_ADMIN")) {
      return "admin";
    } else {
      return "user";
    }
  }
}
