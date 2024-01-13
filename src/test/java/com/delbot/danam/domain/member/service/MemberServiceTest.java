package com.delbot.danam.domain.member.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.delbot.danam.domain.member.entity.Member;
import com.delbot.danam.domain.member.repository.MemberRepository;
import com.delbot.danam.domain.role.Role;
import com.delbot.danam.domain.role.RoleRepository;
import com.delbot.danam.util.CustomTestUtils;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {
  //
  @InjectMocks
  MemberService memberService;

  @Mock
  MemberRepository memberRepository;

  @Mock
  RoleRepository roleRepository;

  @Test
  @DisplayName("회원등록 테스트")
  void addMember_success() throws Exception {
    Member mockMember = CustomTestUtils.createMockMember();

    Member requestMember = new Member("user0001", "asdf1234!", "홍길동0001a", "user0001@google.com");

    given(roleRepository.findByName(anyString())).willReturn(Optional.of(mockMember.getRoles().iterator().next()));
    given(memberRepository.save(any(Member.class))).willReturn(mockMember);

    Role role = roleRepository.findByName("ROLE_USER").get();
    requestMember.addRole(role);
    Member member = memberRepository.save(requestMember);

    assertEquals(requestMember.getName(), member.getName());
    assertEquals(requestMember.getPassword(), member.getPassword());
    assertEquals(requestMember.getNickname(), member.getNickname());
    assertEquals(requestMember.getEmail(), member.getEmail());

    verify(memberRepository).save(any(Member.class));
  }

  @Test
  @DisplayName("멤버찾기_ID 테스트")
  void findById_success() throws Exception {
    Member mockMember = CustomTestUtils.createMockMember();

    given(memberRepository.findById(anyLong())).willReturn(Optional.of(mockMember));

    Member member = memberRepository.findById(1L).get();

    assertEquals(mockMember, member);

    verify(memberRepository).findById(anyLong());
  }

  @Test
  @DisplayName("멤버찾기_Name 테스트")
  void findByName_success() throws Exception {
    Member mockMember = CustomTestUtils.createMockMember();

    given(memberRepository.findByName(anyString())).willReturn(Optional.of(mockMember));

    Member member = memberRepository.findByName("user0001").get();

    assertEquals(mockMember, member);

    verify(memberRepository).findByName(anyString());
  }

  @Test
  @DisplayName("멤버찾기_Nickname 테스트")
  void findByNickname_success() throws Exception {
    Member mockMember = CustomTestUtils.createMockMember();

    given(memberRepository.findByNickname(anyString())).willReturn(Optional.of(mockMember));

    Member member = memberRepository.findByNickname("홍길동0001a").get();

    assertEquals(mockMember, member);

    verify(memberRepository).findByNickname(anyString());
  }

  @Test
  @DisplayName("멤버찾기_Email 테스트")
  void findByEmail_success() throws Exception {
    Member mockMember = CustomTestUtils.createMockMember();

    given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(mockMember));

    Member member = memberRepository.findByEmail("user0001@google.com").get();

    assertEquals(mockMember, member);

    verify(memberRepository).findByEmail(anyString());
  }
}
