package com.delbot.danam.domain.member.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;

import com.delbot.danam.domain.role.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class Member {
  //
  @Id
  @Column(name = "member_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long memberId;

  @Column(nullable = false, length = 50)
  private String name;
  
  @Column(nullable = false, length = 500)
  private String password;

  @Column(nullable = false, length = 50)
  private String nickname;

  @Column(length = 255)
  private String email;

  @CreationTimestamp
  private LocalDateTime createdDate;

  @ManyToMany
  @JoinTable(name = "tb_member_role",
          joinColumns = @JoinColumn(name = "member_id"),
          inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<Role> roles = new HashSet<>();

  @Builder
  public Member(String name, String password, String nickname, String email) {
    this.name = name;
    this.password = password;
    this.nickname = nickname;
    this.email = email;
  }

  @Override
  public String toString() {
    return "User{" +
            " memberId = " + getMemberId() +
            ", email = '" + getEmail() + "'" +
            ", name = '" + getName() + "'" +
            ", password = '" + getPassword() +
            ", regdate = '" + getCreatedDate() +
            "}";
  }

  public void addRole(Role role) {
    roles.add(role);
  }

  public void updateDetails(String nickname, String email) {
    this.nickname = nickname;
    this.email = email;
  }
}
