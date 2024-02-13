package com.delbot.danam.domain.comment.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.delbot.danam.config.TestQueryDslConfig;
import com.delbot.danam.domain.comment.entity.Comment;
import com.delbot.danam.domain.comment.entity.QComment;
import com.delbot.danam.domain.member.entity.Member;
import com.delbot.danam.domain.member.entity.QMember;
import com.delbot.danam.domain.member.repository.MemberRepository;
import com.delbot.danam.domain.post.entity.Post;
import com.delbot.danam.domain.post.entity.QPost;
import com.delbot.danam.domain.post.repository.PostRepository;
import com.delbot.danam.util.CustomTestUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@DataJpaTest
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = NONE)
@Import(TestQueryDslConfig.class)
public class CommentRepositoryTest {
  //
  @PersistenceContext
  EntityManager entityManager;

  @Autowired
  MemberRepository memberRepository;

  @Autowired
  PostRepository postRepository;

  @Autowired
  CommentRepository commentRepository;

  Pageable pageable;  
  QPost qPost;
  QMember qMember;
  QComment qComment;
  JPAQueryFactory queryFactory;
  Member member;

  @BeforeEach
  void setup() {
    Member mockMember = CustomTestUtils.createMockMember(1L);
    member = memberRepository.save(mockMember);
    pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "postNo");

    qPost = QPost.post;
    qMember = QMember.member;
    qComment = QComment.comment;

    queryFactory = new JPAQueryFactory(entityManager);

    for (Long l = 1L; l <= 10; l++) {
      Post post = postRepository.save(new Post(l, "board", "title" + l, "Hello World!!", member));
      for (Long m = 1L; m <= 10; m++) {
        commentRepository.save(new Comment("Post No : " + l + " Comment No : " + m, member, post, 0, null));
      }
    }
  }

  @Test
  @DisplayName("게시글 댓글 불러오기 테스트")
  void findByPost_success() throws Exception {
    Post post = postRepository.findByCategoryAndPostNo("board", 1L).get();

    List<Comment> result = queryFactory
            .selectFrom(qComment)
            .leftJoin(qComment.parent)
            .fetchJoin()
            .where(qComment.post.postId.eq(post.getPostId()))
            .orderBy(qComment.parent.commentId.asc().nullsFirst(), qComment.createdTime.asc())
            .fetch();

    assertEquals(commentRepository.findByPost(post), result);

    printCommentLog(result);
  }

  @Test
  @DisplayName("멤버정보 조회 - 댓글 테스트")
  void getMemberInfoComments_success() throws Exception {
    List<Comment> result = queryFactory
            .selectFrom(qComment)
            .leftJoin(qComment.member)
            .fetchJoin()
            .where(qComment.member.memberId.eq(member.getMemberId()))
            .orderBy(qComment.createdTime.desc())
            .limit(10)
            .fetch();

    assertEquals(commentRepository.getMemberInfoComments(member), result);

    printCommentLog(result);
  }
  
  private void printCommentLog(List<Comment> commentList) {
    for (Comment comment : commentList) {
        log.info("Id: {}", comment.getCommentId());
        log.info("Contents: {}", comment.getContents());
        log.info("Writer: {}", comment.getMember().getNickname());
        log.info("----------------------------");
    }
  }
}
