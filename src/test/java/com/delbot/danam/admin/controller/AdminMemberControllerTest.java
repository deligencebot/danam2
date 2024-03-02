package com.delbot.danam.admin.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.delbot.danam.admin.dto.AdminMemberRequestDto;
import com.delbot.danam.config.TestSecurityConfig;
import com.delbot.danam.domain.blockInfo.BlockInfo;
import com.delbot.danam.domain.blockInfo.BlockInfoRepository;
import com.delbot.danam.domain.member.entity.Member;
import com.delbot.danam.domain.member.service.MemberService;
import com.delbot.danam.domain.role.Role;
import com.delbot.danam.global.security.jwt.token.JwtAuthenticationToken;
import com.delbot.danam.util.CustomTestUtils;

@WebMvcTest(controllers = AdminMemberController.class)
@Import(TestSecurityConfig.class)
@AutoConfigureWebMvc
public class AdminMemberControllerTest {
  //
  @Autowired
  MockMvc mockMvc;

  @MockBean
  MemberService memberService;

  @MockBean
  BlockInfoRepository blockInfoRepository;

  Member admin;
  JwtAuthenticationToken jwtAuthenticationToken;
  List<Member> memberList = new ArrayList<>();

  @BeforeEach
  void setup() {
    Role adminRole = new Role(2L, "ROLE_ADMIN");
    admin = CustomTestUtils.createMockMember();
    admin.addRole(adminRole);

    jwtAuthenticationToken = CustomTestUtils.getLoginUserJwtAuthenticationToken(admin);
    memberList.add(admin);

    for (Long l = 2L; l <= 10; l++) {
      memberList.add(CustomTestUtils.createMockMember(l));
    }
  }

  // http://localhost:8080/admin/members/management
  @Test
  @DisplayName("Member 목록 불러오기 테스트")
  void getMemberList_success() throws Exception {
    given(memberService.findAllMember()).willReturn(memberList);

    mockMvc.perform(
            get("/admin/members/management")
            .with(authentication(jwtAuthenticationToken)))
            .andDo(print())
            .andExpect(status().isOk());
  }

  // http://localhost:8080/admin/members/ban
  @Test
  @DisplayName("Member 차단 테스트")
  void banMember_success() throws Exception {
    Member member = CustomTestUtils.createMockMember(11L);
    member.updateEnabled(false);
    memberList.add(member);

    AdminMemberRequestDto.Ban request = new AdminMemberRequestDto.Ban();
    request.setMemberId(11L);
    request.setDuration(7 * 24 * 60 * 60 * 1000L);

    given(memberService.findById(anyLong())).willReturn(member);

    given(memberService.findAllMember()).willReturn(memberList);

    mockMvc.perform(
            post("/admin/members/management/ban")
            .content(CustomTestUtils.toJson(request))
            .contentType(MediaType.APPLICATION_JSON)
            .with(authentication(jwtAuthenticationToken)))
            .andDo(print())
            .andExpect(status().isOk());
  }

  // http://localhost:8080/admin/members/release
  @Test
  @DisplayName("Member 차단해제 테스트")
  void releaseMember_success() throws Exception {
    Member member = CustomTestUtils.createMockMember(11L);
    memberList.add(member);

    AdminMemberRequestDto.Id request = new AdminMemberRequestDto.Id();
    request.setMemberId(11L);

    given(memberService.findById(anyLong())).willReturn(member);

    given(blockInfoRepository.findByMember(any(Member.class))).willReturn(Optional.of(mock(BlockInfo.class)));

    given(memberService.findAllMember()).willReturn(memberList);

    mockMvc.perform(
            post("/admin/members/management/release")
            .content(CustomTestUtils.toJson(request))
            .contentType(MediaType.APPLICATION_JSON)
            .with(authentication(jwtAuthenticationToken)))
            .andDo(print())
            .andExpect(status().isOk());
  }

  // http://localhost:8080/admin/members/withdraw
  @Test
  @DisplayName("Member 삭제 테스트") 
  void withdrawMember_success() throws Exception {
    Member member = CustomTestUtils.createMockMember(11L);

    AdminMemberRequestDto.Id request = new AdminMemberRequestDto.Id();
    request.setMemberId(11L);

    given(memberService.findById(anyLong())).willReturn(member);

    given(memberService.findAllMember()).willReturn(memberList);

    mockMvc.perform(
            post("/admin/members/management/withdraw")
            .content(CustomTestUtils.toJson(request))
            .contentType(MediaType.APPLICATION_JSON)
            .with(authentication(jwtAuthenticationToken)))
            .andDo(print())
            .andExpect(status().isOk());
  }
  
}
