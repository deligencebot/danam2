package com.delbot.danam.domain.member.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.delbot.danam.config.TestQueryDslConfig;
import com.delbot.danam.domain.member.entity.Member;
import com.delbot.danam.domain.role.Role;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = NONE)
@Import(TestQueryDslConfig.class)
public class MemberRepositoryTest {
  //
  @Autowired
  MemberRepository memberRepository;

  Role role;
  Member member1;
  Member member2;
  Member savedMember1;
  Member savedMember2;

  @BeforeEach
  void setup() {
    role = new Role(1L, "ROLE_USER");
    member1 = new Member("test0001", "password", "테스트0001", "test0001@google.com");
    member2 = new Member("test0002", "password", "테스트0002", "test0002@google.com");
    member1.addRole(role);
    member2.addRole(role);
    savedMember1 = memberRepository.save(member1);
    savedMember2 = memberRepository.save(member2);
  }

  @Test
  @DisplayName("FindByName 테스트")
  void findByName_success() throws Exception {
    Member foundMember1 = memberRepository.findByName(member1.getName()).get();
    Member foundMember2 = memberRepository.findByName(member2.getName()).get();

    assertEquals(savedMember1, foundMember1);
    assertEquals(savedMember2, foundMember2);
  }

  @Test
  @DisplayName("FindByNickname 테스트")
  void findByNickname_success() throws Exception {
    Member foundMember1 = memberRepository.findByNickname(member1.getNickname()).get();
    Member foundMember2 = memberRepository.findByNickname(member2.getNickname()).get();

    assertEquals(savedMember1, foundMember1);
    assertEquals(savedMember2, foundMember2);
  }

  @Test
  @DisplayName("FindByEmail 테스트")
  void findByEmail_success() throws Exception {
    Member foundMember1 = memberRepository.findByEmail(member1.getEmail()).get();
    Member foundMember2 = memberRepository.findByEmail(member2.getEmail()).get();

    assertEquals(savedMember1, foundMember1);
    assertEquals(savedMember2, foundMember2);
  }
}
