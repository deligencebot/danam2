package com.delbot.danam.domain.post.repository;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.delbot.danam.config.TestQueryDslConfig;
import com.delbot.danam.domain.category.Category;
import com.delbot.danam.domain.category.CategoryRepository;
import com.delbot.danam.domain.category.QCategory;
import com.delbot.danam.domain.comment.entity.Comment;
import com.delbot.danam.domain.comment.entity.QComment;
import com.delbot.danam.domain.comment.repository.CommentRepository;
import com.delbot.danam.domain.member.entity.Member;
import com.delbot.danam.domain.member.entity.QMember;
import com.delbot.danam.domain.member.repository.MemberRepository;
import com.delbot.danam.domain.post.entity.Post;
import com.delbot.danam.domain.post.entity.QPost;
import com.delbot.danam.domain.role.Role;
import com.delbot.danam.domain.role.RoleRepository;
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
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class PostRepositoryTest {
  //
  @PersistenceContext
  EntityManager entityManager;

  @Autowired
  MemberRepository memberRepository;

  @Autowired
  PostRepository postRepository;

  @Autowired
  CommentRepository commentRepository;
  
  @Autowired
  CategoryRepository categoryRepository;

  @Autowired
  RoleRepository roleRepository;

  Pageable pageable;  
  QPost qPost;
  QMember qMember;
  QComment qComment;
  QCategory qCategory;
  JPAQueryFactory queryFactory;
  Member member1;
  Member member2;
  Category categoryA;
  Category categoryB;

  @Transactional
  @BeforeEach
  void setup() {
    queryFactory = new JPAQueryFactory(entityManager);

    Role role = new Role(1L, "ROLE_USER");
    roleRepository.save(role);

    Member mockMember1 = CustomTestUtils.createMockMember(1L);
    Member mockMember2 = CustomTestUtils.createMockMember(2L);
    member1 = memberRepository.save(mockMember1);
    member2 = memberRepository.save(mockMember2);

    Category mockCategoryA = new Category("board A");
    Category mockCategoryB = new Category("board B");
    categoryA = categoryRepository.save(mockCategoryA);
    categoryB = categoryRepository.save(mockCategoryB);

    pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "postNo");

    qPost = QPost.post;
    qMember = QMember.member;
    qComment = QComment.comment;
    qCategory = QCategory.category;

    for (Long l = 1L; l <= 20; l++) {
      if (l % 3 != 0) {
        Post tempA = postRepository.save(new Post(l, categoryA, "title" + l, "Hello World!!", member1));
        Post tempB = postRepository.save(new Post(l, categoryB, "title" + l, "Hello World!!", member1));
        commentRepository.save(new Comment("hello board A " + l, member1, tempA, 0, null));
        commentRepository.save(new Comment("hello board B " + l, member1, tempB, 0, null));
      } else {
        Post tempA = postRepository.save(new Post(l, categoryA, "title" + l, "Hello World!!", member2));
        Post tempB = postRepository.save(new Post(l, categoryB, "title" + l, "Hello World!!", member2));
        commentRepository.save(new Comment("hello board A " + l, member2, tempA, 0, null));
        commentRepository.save(new Comment("hello board B " + l, member2, tempB, 0, null));
      }
    }
  }

  @Test
  @Transactional
  @DisplayName("조회수 갱신 테스트")
  void updateHits_success() throws Exception {
    Post post = postRepository.findByCategoryAndPostNo(categoryB, 1L).get();
    postRepository.updateHits(post.getPostId());

    entityManager.flush();
    entityManager.clear();

    Post updatedPost = postRepository.findByCategoryAndPostNo(categoryB, 1L).get();

    assertEquals(1L, updatedPost.getHits());
  }

  @Test
  @Transactional
  @DisplayName("모든 게시판 게시글 검색(제목, 내용, 작성자) 테스트")
  void findByAllTarget_success() throws Exception {
    String keyword = "홍길동0002";

    List<Post> postList = queryFactory.selectFrom(qPost)
            .where(qPost.title.containsIgnoreCase(keyword)
                .or(qPost.contents.containsIgnoreCase(keyword))
                .or(qPost.member.nickname.containsIgnoreCase(keyword)))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

    Page<Post> postPage = new PageImpl<>(postList, pageable, postList.size());

    assertEquals(postRepository.findByAllTarget(keyword, pageable), postPage);

    printPageLog(postPage);
  }

  @Test
  @Transactional
  @DisplayName("모든 게시판 게시글 검색(제목, 내용) 테스트")
  void findByTitleAndContents_success() throws Exception {
    String keyword = "5";

    List<Post> postList = queryFactory.selectFrom(qPost)
            .where(qPost.title.containsIgnoreCase(keyword)
                .or(qPost.contents.containsIgnoreCase(keyword)))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

    Page<Post> postPage = new PageImpl<>(postList, pageable, postList.size());

    assertEquals(postRepository.findByTitleAndContents(keyword, pageable), postPage);

    printPageLog(postPage);
  }

  @Test
  @Transactional
  @DisplayName("모든 게시판 게시글 검색(제목) 테스트")
  void findByTitle_success() throws Exception {
    String keyword = "6";

    List<Post> postList = queryFactory.selectFrom(qPost)
            .where(qPost.title.containsIgnoreCase(keyword))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

    Page<Post> postPage = new PageImpl<>(postList, pageable, postList.size());

    assertEquals(postRepository.findByTitle(keyword, pageable), postPage);

    printPageLog(postPage);
  }

  @Test
  @Transactional
  @DisplayName("모든 게시판 게시글 검색(내용) 테스트")
  void findByContents_success() throws Exception {
    String keyword = "7";

    List<Post> postList = queryFactory.selectFrom(qPost)
            .where(qPost.contents.containsIgnoreCase(keyword))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

    Page<Post> postPage = new PageImpl<>(postList, pageable, postList.size());

    assertEquals(postRepository.findByContents(keyword, pageable), postPage);

    printPageLog(postPage);
  }

  @Test
  @Transactional
  @DisplayName("모든 게시판 게시글 검색(작성자) 테스트")
  void findByWriter_success() throws Exception {
    String keyword = "홍길동0001";

    List<Post> postList = queryFactory.selectFrom(qPost)
            .join(qPost.member, qMember)
            .where(qPost.member.nickname.containsIgnoreCase(keyword))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

    Page<Post> postPage = new PageImpl<>(postList, pageable, postList.size());

    assertEquals(postRepository.findByWriter(keyword, pageable), postPage);

    printPageLog(postPage);
  }

  @Test
  @Transactional
  @DisplayName("모든 게시판 게시글 검색(댓글) 테스트")
  void findByComment_success() throws Exception {
    String keyword = "A 1";

    List<Post> postList = queryFactory.selectFrom(qPost).distinct()
            .join(qPost.comments, qComment)
            .where(qComment.contents.containsIgnoreCase(keyword))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

    Page<Post> postPage = new PageImpl<>(postList, pageable, postList.size());

    assertEquals(postRepository.findByComment(keyword, pageable), postPage);

    printPageLog(postPage);
  }

  @Test
  @Transactional
  @DisplayName("게시글 검색(제목, 내용, 작성자) 테스트")
  void findByAllTargetWithCategory_success() throws Exception {
    String keyword = "5";
    
    List<Post> postList = queryFactory.selectFrom(qPost)
            .join(qPost.member, qMember)
            .where(qPost.category.eq(categoryA)
                .and(qPost.title.containsIgnoreCase(keyword)
                    .or(qPost.contents.containsIgnoreCase(keyword))
                    .or(qPost.member.nickname.containsIgnoreCase(keyword))))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

    Page<Post> postPage = new PageImpl<>(postList, pageable, postList.size());
    
    printPageLog(postPage);
    
    assertEquals(postRepository.findByAllTarget(categoryA, keyword, pageable), postPage);
  }

  @Test
  @Transactional
  @DisplayName("게시글 검색(제목, 내용) 테스트")
  void findByTitleAndContentsWithCategory_success() throws Exception {
    String keyword = "1";

    List<Post> postList = queryFactory.selectFrom(qPost)
            .where(qPost.category.eq(categoryB)
                .and(qPost.title.containsIgnoreCase(keyword)
                    .or(qPost.contents.containsIgnoreCase(keyword))))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

      Page<Post> postPage = new PageImpl<>(postList, pageable, postList.size());

      assertEquals(postRepository.findByTitleAndContents(categoryB, keyword, pageable), postPage);

      printPageLog(postPage);
    } 

  @Test
  @Transactional
  @DisplayName("게시글 검색(제목) 테스트")
  void findByTitleWithCategory_success() throws Exception {
    String keyword = "2";

    List<Post> postList = queryFactory.selectFrom(qPost)
            .where(qPost.category.eq(categoryA)
                .and(qPost.title.containsIgnoreCase(keyword)))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

      Page<Post> postPage = new PageImpl<>(postList, pageable, postList.size());

      assertEquals(postRepository.findByTitle(categoryA, keyword, pageable), postPage);

      printPageLog(postPage);
  }

  @Test
  @Transactional
  @DisplayName("게시글 검색(내용) 테스트")
  void findByContentsWithCategory_success() throws Exception {
    String keyword = "2";

    List<Post> postList = queryFactory.selectFrom(qPost)
            .where(qPost.category.eq(categoryB)
                .and(qPost.contents.containsIgnoreCase(keyword)))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

      Page<Post> postPage = new PageImpl<>(postList, pageable, postList.size());

      assertEquals(postRepository.findByContents(categoryB, keyword, pageable), postPage);

      printPageLog(postPage);
  }

  @Test
  @Transactional
  @DisplayName("게시글 검색(작성자) 테스트")
  void findByWriterWithCategory_success() throws Exception {
    String keyword = "3";

    List<Post> postList = queryFactory.selectFrom(qPost)
            .where(qPost.category.eq(categoryA)
                .and(qPost.member.nickname.containsIgnoreCase(keyword)))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

      Page<Post> postPage = new PageImpl<>(postList, pageable, postList.size());

      assertEquals(postRepository.findByWriter(categoryA, keyword, pageable), postPage);

      printPageLog(postPage);
  }

  @Test
  @Transactional
  @DisplayName("게시글 검색(댓글) 테스트")
  void findByCommentWithCategory_success() throws Exception {
    String keyword = "2";

    List<Post> postList = queryFactory.selectFrom(qPost).distinct()
            .join(qPost.comments, qComment)
            .where(qPost.category.eq(categoryB)
                .and(qComment.contents.containsIgnoreCase(keyword)))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

    Page<Post> postPage = new PageImpl<>(postList, pageable, postList.size());

    assertEquals(postRepository.findByComment(categoryB, keyword, pageable), postPage);

    printPageLog(postPage);
  }

  @Test
  @Transactional
  @DisplayName("멤버정보 조회 - 게시글 테스트")
  void getMemberInfoPosts_success() throws Exception {
    List<Post> postList = queryFactory.selectFrom(qPost)
            .leftJoin(qPost.member)
            .fetchJoin()
            .where(qPost.member.memberId.eq(member1.getMemberId()))
            .limit(10)
            .fetch();
    
    assertEquals(postRepository.getMemberInfoPosts(member1), postList);

    log.info("Member ID : {}", member1.getMemberId());
    for (Post post : postList) {
      log.info("----------------------------");
      log.info("Category: {}", post.getCategory().getName());
      log.info("Number: {}", post.getPostNo());
      log.info("Title: {}", post.getTitle());
      log.info("Contents: {}", post.getContents());
      log.info("Writer: {}", post.getMember().getNickname());
      log.info("----------------------------");
    }
  }

  private void printPageLog(Page<Post> postPage) {
    log.info("Page number: {}", postPage.getNumber());
    log.info("Page size: {}", postPage.getSize());
    log.info("Total elements: {}", postPage.getTotalElements());
    log.info("Total pages: {}", postPage.getTotalPages());
    log.info("--------  CONTENT  ---------");
    for (Post post : postPage.getContent()) {
        log.info("Category: {}", post.getCategory().getName());
        log.info("Number: {}", post.getPostNo());
        log.info("Title: {}", post.getTitle());
        log.info("Contents: {}", post.getContents());
        log.info("Writer: {}", post.getMember().getNickname());
        log.info("----------------------------");
    }
  }
}
