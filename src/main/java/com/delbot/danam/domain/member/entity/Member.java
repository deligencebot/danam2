package com.delbot.danam.domain.member.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import com.delbot.danam.domain.comment.entity.Comment;
import com.delbot.danam.domain.post.entity.Post;
import com.delbot.danam.domain.role.Role;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
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

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "tb_member_role",
          joinColumns = @JoinColumn(name = "member_id"),
          inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<Role> roles = new HashSet<>();

  @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Post> postList = new ArrayList<>();

  @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Comment> commentList = new ArrayList<>();

  private boolean isEnabled;

  @Builder
  public Member(String name, String password, String nickname, String email) {
    this.name = name;
    this.password = password;
    this.nickname = nickname;
    this.email = email;
    this.isEnabled = true;
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
    this.roles.add(role);
  }

  public void removeRole(Role role) {
    this.roles.remove(role);
  }

  public void updateDetails(String nickname, String email) {
    this.nickname = nickname;
    this.email = email;
  }

  public void updatePassword(String password) {
    this.password = password;
  }

  public void updateEnabled(boolean isEnabled) {
    this.isEnabled = isEnabled;
  }
}
