package com.delbot.danam.domain.post.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.delbot.danam.config.TestQueryDslConfig;
import com.delbot.danam.domain.member.entity.Member;
import com.delbot.danam.domain.member.entity.QMember;
import com.delbot.danam.domain.post.entity.Post;
import com.delbot.danam.domain.post.entity.QPost;
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
public class PostRepositoryTest {
  //
  @PersistenceContext
  EntityManager entityManager;

  @Autowired
  PostRepository postRepository;

  Pageable pageable;  
  QPost qPost;
  QMember qMember;
  JPAQueryFactory queryFactory;

  @BeforeEach
  void setup() {
    Member mockMember = CustomTestUtils.createMockMember();
    pageable = PageRequest.of(0, 5, Sort.Direction.DESC, "postNo");
    qPost = QPost.post;
    qMember = QMember.member;
    queryFactory = new JPAQueryFactory(entityManager);
    for (Long i = 1L; i <= 20; i++) {
      postRepository.save(new Post(i, "board A", "title" + i, "Hello World!!", mockMember));
    }
    for (Long i = 1L; i <= 20; i++) {
      postRepository.save(new Post(i, "board B", "title" + i, "Hello World!!", mockMember));
    }
  }

  @Test
  @DisplayName("게시글 리스트 불러오기 테스트")
  void findByCategory_list_success() throws Exception {
    List<Post> postList = postRepository.findByCategory("board A");

    assertNotNull(postList.get(0).getPostId());
    assertNotNull(postList.get(0).getUpdatedTime());
    assertEquals(postList.get(0).getCategory(), postList.get(5).getCategory());
    assertEquals(20, postList.size());
  }

  @Test
  @DisplayName("게시글 페이지 리스트 불러오기 테스트")
  void findByCategory_page_success() throws Exception {
    Page<Post> postPage = postRepository.findByCategory("board B", pageable);
    List<Post> postList = postPage.getContent();

    assertNotNull(postList.get(0));
    assertEquals(4, postPage.getTotalPages());
    assertEquals(20, postPage.getTotalElements());
    assertEquals(pageable, postPage.getPageable());
  }

  @Test
  @DisplayName("게시글 불러오기 테스트")
  void findByCategoryAndPostNo_success() throws Exception {
    Post post = postRepository.findByCategoryAndPostNo("board A", 10L).get();

    assertEquals(10L, post.getPostNo());
  }

  @Test
  @Transactional
  @DisplayName("조회수 갱신 테스트")
  void updateHits_success() throws Exception {
    Post post = postRepository.findByCategoryAndPostNo("board B", 1L).get();
    postRepository.updateHits(post.getPostId());

    entityManager.flush();
    entityManager.clear();

    Post updatedPost = postRepository.findByCategoryAndPostNo("board B", 1L).get();

    assertEquals(1L, updatedPost.getHits());
  }

  @Test
  @Transactional
  @DisplayName("검색(모든 조건) 테스트")
  void findByAllTarget_success() throws Exception {
    String category = "board A";
    String keyword = "5";
    
    List<Post> postList = queryFactory.selectFrom(qPost)
            .join(qPost.member, qMember)
            .where(qPost.category.eq(category)
                .and(qPost.title.containsIgnoreCase(keyword)
                    .or(qPost.contents.containsIgnoreCase(keyword))
                    .or(qPost.member.nickname.containsIgnoreCase(keyword))))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

    Page<Post> postPage = new PageImpl<>(postList, pageable, postList.size());
    
    assertEquals(postRepository.findByAllTarget(category, keyword, pageable), postPage);

    printPageLog(postPage);
  }

  @Test
  @Transactional
  @DisplayName("검색(제목&내용) 테스트")
  void findByTitleAndContents_success() throws Exception {
    String category = "board B";
    String keyword = "1";

    List<Post> postList = queryFactory.selectFrom(qPost)
            .where(qPost.category.eq(category)
                .and(qPost.title.containsIgnoreCase(keyword)
                    .or(qPost.contents.containsIgnoreCase(keyword))))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

      Page<Post> postPage = new PageImpl<>(postList, pageable, postList.size());

      assertEquals(postRepository.findByTitleAndContents(category, keyword, pageable), postPage);

      printPageLog(postPage);
    } 

  @Test
  @Transactional
  @DisplayName("검색(제목) 테스트")
  void findByTitle_success() throws Exception {
    String category = "board A";
    String keyword = "2";

    List<Post> postList = queryFactory.selectFrom(qPost)
            .where(qPost.category.eq(category)
                .and(qPost.title.containsIgnoreCase(keyword)))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

      Page<Post> postPage = new PageImpl<>(postList, pageable, postList.size());

      assertEquals(postRepository.findByTitle(category, keyword, pageable), postPage);

      printPageLog(postPage);
  }

  @Test
  @Transactional
  @DisplayName("검색(내용) 테스트")
  void findByContents_success() throws Exception {
    String category = "board A";
    String keyword = "2";

    List<Post> postList = queryFactory.selectFrom(qPost)
            .where(qPost.category.eq(category)
                .and(qPost.contents.containsIgnoreCase(keyword)))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

      Page<Post> postPage = new PageImpl<>(postList, pageable, postList.size());

      assertEquals(postRepository.findByContents(category, keyword, pageable), postPage);

      printPageLog(postPage);
  }

  @Test
  @Transactional
  @DisplayName("검색(내용) 테스트")
  void findByWriter_success() throws Exception {
    String category = "board B";
    String keyword = "3";

    List<Post> postList = queryFactory.selectFrom(qPost)
            .where(qPost.category.eq(category)
                .and(qPost.member.nickname.containsIgnoreCase(keyword)))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

      Page<Post> postPage = new PageImpl<>(postList, pageable, postList.size());

      assertEquals(postRepository.findByWriter(category, keyword, pageable), postPage);

      printPageLog(postPage);
  }


  private void printPageLog(Page<Post> postPage) {
    log.info("Page number: {}", postPage.getNumber());
    log.info("Page size: {}", postPage.getSize());
    log.info("Total elements: {}", postPage.getTotalElements());
    log.info("Total pages: {}", postPage.getTotalPages());
    log.info("--------  CONTENT  ---------");
    for (Post post : postPage.getContent()) {
        log.info("Category: {}", post.getCategory());
        log.info("Number: {}", post.getPostNo());
        log.info("Title: {}", post.getTitle());
        log.info("Contents: {}", post.getContents());
        log.info("Writer: {}", post.getMember().getNickname());
        log.info("----------------------------");
    }
  }
}
