package com.delbot.danam.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.delbot.danam.domain.blockInfo.BlockInfoRepository;
import com.delbot.danam.domain.member.entity.Member;
import com.delbot.danam.domain.member.repository.MemberRepository;
import com.delbot.danam.domain.role.Role;
import com.delbot.danam.domain.role.RoleRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class Initializer {
  //
  private final PasswordEncoder passwordEncoder;

  @Value("${admin.name}")
  private String name;
  @Value("${admin.password}")
  private String password;

  @Bean
  public CommandLineRunner init(RoleRepository roleRepository, MemberRepository memberRepository, BlockInfoRepository blockInfoRepository) {
    return args -> {
      if (roleRepository.count() == 0) {
        Role userRole = new Role(1L,"ROLE_USER");
        Role adminRole = new Role(2L, "ROLE_ADMIN");

        roleRepository.save(userRole);
        roleRepository.save(adminRole);

        Member admin = new Member(name, passwordEncoder.encode(password), "ADMIN", "example@aaa.com");
        admin.addRole(userRole);
        admin.addRole(adminRole);
        memberRepository.save(admin);
      }
    };
  }
}
