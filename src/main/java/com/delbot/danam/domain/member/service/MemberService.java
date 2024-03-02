package com.delbot.danam.domain.member.service;

import java.util.Optional;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
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
  private final PasswordEncoder passwordEncoder;

  @Transactional(readOnly = true)
  public Member login(String name, String password) {
    Member member = memberRepository.findByName(name).orElseThrow(() -> MemberErrorCode.LOGIN_FAILED.defaultException());
    if (!passwordEncoder.matches(password, member.getPassword())) {
      throw MemberErrorCode.LOGIN_FAILED.defaultException();
    } else if (!member.isEnabled()) {
      throw MemberErrorCode.BANNED_MEMBER.defaultException();
    }
    return member;
  }

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

  @Transactional(readOnly = true)
  public List<Member> findAllMember() {
    return memberRepository.findAll();
  }

  @Transactional
  public void deleteMember(Member member) {
    memberRepository.delete(member);
  }
}
