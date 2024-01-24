package com.delbot.danam.util;

import static org.mockito.Mockito.mock;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.delbot.danam.domain.member.entity.Member;
import com.delbot.danam.domain.post.entity.Post;
import com.delbot.danam.domain.post.entity.PostFile;
import com.delbot.danam.domain.post.entity.PostImage;
import com.delbot.danam.domain.role.Role;
import com.delbot.danam.global.security.jwt.token.JwtAuthenticationToken;
import com.delbot.danam.global.security.jwt.util.LoginInfoDto;
import com.delbot.danam.global.security.jwt.util.LoginUserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class CustomTestUtils {
  //
    public static Member createMockMember() {
    Role role = new Role(1L, "ROLE_USER");
    Set<Role> roles = Set.of(role);

    return new Member(
            1L, 
            "user0001", 
            "asdf1234!", 
            "홍길동0001a", 
            "user0001@google.com", 
            LocalDateTime.now().truncatedTo(ChronoUnit.DAYS), 
            roles, 
            Collections.emptyList());
  }

  public static Post createMockPost(Member member) {
    return new Post(
            1L, 
            1L, 
            "board", 
            "Fake Title", 
            "Hello World!", 
            0L,
            false,
            false,
            true,
            LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), 
            null, 
            member,
            new ArrayList<>(),
            new ArrayList<>()
    );
  }

  public static Page<Post> generateMockPage(String category, Pageable pageable) {
    List<Post> postList = new ArrayList<>();

    for (Long i = 0L; i < 20; i++) {
      postList.add(new Post(
              i, 
              i, 
              category, 
              "Title" + String.valueOf(i), 
              "Hello!!", 
              0L,
              false,
              false,
              true,
              LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), 
              null, 
              mock(Member.class),
              null,
              null
      ));
    }

    int start = (int) pageable.getOffset();
    int end = Math.min((start + pageable.getPageSize()), postList.size());
    List<Post> subPostList = postList.subList(start, end);

    return new PageImpl<>(subPostList, pageable, postList.size());
  }

  public static PostFile createMockPostFile(Post post, Long l) {
    return new PostFile(
            l, 
            "mockUrl" + l, 
            "mockStoredName" + l, 
            "mockOriginalName" + l,
            1024*1024L,
            LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), 
            post);
  }

  public static PostImage createMockPostImage(Post post, Long l) {
    return new PostImage(
            l, 
            "mockUrl" + l, 
            "mockStoredName" + l, 
            "mockOriginalName" + l,
            1024*1024L,
            LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), 
            post);
  }

  public static LoginUserDto getLoginUserDto(Member member) {
    LoginUserDto loginUserDto = new LoginUserDto();
    loginUserDto.setMemberId(member.getMemberId());
    loginUserDto.setName(member.getName());
    loginUserDto.setNickname(member.getNickname());
    loginUserDto.addRole("ROLE_USER");

    return loginUserDto;
  }

  public static JwtAuthenticationToken getLoginUserJwtAuthenticationToken(Member member) {
    Set<GrantedAuthority> authorities = new HashSet<>();
    
    for (Role role : member.getRoles()) {
      authorities.add(new SimpleGrantedAuthority(role.getName()));
    }

    LoginInfoDto loginInfoDto = new LoginInfoDto();
    loginInfoDto.setMemberId(member.getMemberId());
    loginInfoDto.setName(member.getName());
    loginInfoDto.setNickname(member.getNickname());

    return new JwtAuthenticationToken(authorities, loginInfoDto, null);
  }

  public static String toJson(Object object) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.registerModule(new JavaTimeModule());

      return objectMapper.writeValueAsString(object);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException();
    }
  }
}
