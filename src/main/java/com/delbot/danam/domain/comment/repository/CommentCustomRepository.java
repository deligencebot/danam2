package com.delbot.danam.domain.comment.repository;

import java.util.List;

import com.delbot.danam.domain.comment.entity.Comment;
import com.delbot.danam.domain.member.entity.Member;
import com.delbot.danam.domain.post.entity.Post;

public interface CommentCustomRepository {
  //
  List<Comment> findByPost(Post post);
  List<Comment> getMemberInfoComments(Member member);
}
