package com.delbot.danam.domain.post.repository;

import static com.delbot.danam.domain.member.entity.QMember.member;
import static com.delbot.danam.domain.post.entity.QPost.post;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.delbot.danam.domain.post.entity.Post;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PostCustomRepositoryImpl implements PostCustomRepository {
  //
  private final JPAQueryFactory queryFactory;

  @Override
  public Page<Post> findByAllTarget(String category, String keyword, Pageable pageable) {
    List<Post> result = queryFactory
              .selectFrom(post)
              .join(post.member, member)
              .where(post.category.eq(category)
                  .and(post.title.containsIgnoreCase(keyword)
                      .or(post.contents.containsIgnoreCase(keyword))
                      .or(post.member.nickname.containsIgnoreCase(keyword))))
              .offset(pageable.getOffset())
              .limit(pageable.getPageSize())
              .fetch();
    return new PageImpl<>(result, pageable, result.size());
  }

  @Override
  public Page<Post> findByTitleAndContents(String category, String keyword, Pageable pageable) {
    List<Post> result = queryFactory
            .selectFrom(post)
            .where(post.category.eq(category)
                .and(post.title.containsIgnoreCase(keyword)
                    .or(post.contents.containsIgnoreCase(keyword))))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    return new PageImpl<>(result, pageable, result.size());
  }

  @Override
  public Page<Post> findByTitle(String category, String keyword, Pageable pageable) {
    List<Post> result = queryFactory
            .selectFrom(post)
            .where(post.category.eq(category)
                .and(post.title.containsIgnoreCase(keyword)))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    return new PageImpl<>(result, pageable, result.size());
  }

  @Override
  public Page<Post> findByContents(String category, String keyword, Pageable pageable) {
    List<Post> result = queryFactory
            .selectFrom(post)
            .where(post.category.eq(category)
                .and(post.contents.containsIgnoreCase(keyword)))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    return new PageImpl<>(result, pageable, result.size());
  }

  @Override
  public Page<Post> findByWriter(String category, String keyword, Pageable pageable) {
    List<Post> result = queryFactory
            .selectFrom(post)
            .join(post.member, member)
            .where(post.category.eq(category)
                .and(post.member.nickname.containsIgnoreCase(keyword)))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    return new PageImpl<>(result, pageable, result.size());
  }
}