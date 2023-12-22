package com.delbot.danam.domain.member.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.delbot.danam.domain.member.entity.Member;
import com.delbot.danam.domain.member.exception.MemberErrorCode;
import com.delbot.danam.domain.member.repository.MemberRepository;
import com.delbot.danam.domain.role.Role;
import com.delbot.danam.domain.role.RoleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {
  //
  private final MemberRepository memberRepository;
  private final RoleRepository roleRepository;

  @Transactional
  public Member addMember(Member member) {
    Role role = roleRepository.findByName("ROLE_USER").get();
    member.addRole(role);
    return memberRepository.save(member);
  }

  @Transactional(readOnly = true)
  public Member findById(Long id) {
    return memberRepository.findById(id).orElseThrow(
      () -> MemberErrorCode.NOT_FOUND_MEMBER.defaultException());
  }

  @Transactional(readOnly = true)
  public Optional<Member> findByName(String name) {
    return memberRepository.findByName(name);
  }

  @Transactional(readOnly = true)
  public Optional<Member> findByNickname(String nickname) {
    return memberRepository.findByNickname(nickname);
  }

  @Transactional(readOnly = true)
  public Optional<Member> findByEmail(String email) {
    return memberRepository.findByEmail(email);
  }
}
