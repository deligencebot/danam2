package com.delbot.danam.domain.comment.repository;

import static com.delbot.danam.domain.comment.entity.QComment.comment;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.delbot.danam.domain.comment.entity.Comment;
import com.delbot.danam.domain.member.entity.Member;
import com.delbot.danam.domain.post.entity.Post;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CommentCustomRepositoryImpl implements CommentCustomRepository{
  
  private final JPAQueryFactory queryFactory;

  @Override
  public List<Comment> findByPost(Post post) {
    return queryFactory.selectFrom(comment)
            .leftJoin(comment.parent)
            .fetchJoin()
            .where(comment.post.postId.eq(post.getPostId()))
            .orderBy(comment.parent.commentId.asc().nullsFirst(), comment.createdTime.asc())
            .fetch();
  }

  @Override
  public List<Comment> getMemberInfoComments(Member member) {
    return queryFactory.selectFrom(comment)
          .leftJoin(comment.member)
          .fetchJoin()
          .where(comment.member.memberId.eq(member.getMemberId()))
          .orderBy(comment.createdTime.desc())
          .limit(10)
          .fetch();
  }
}
