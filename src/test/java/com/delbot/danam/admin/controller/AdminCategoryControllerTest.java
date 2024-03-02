package com.delbot.danam.admin.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.ArrayList;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.delbot.danam.admin.dto.AdminCategoryRequestDto;
import com.delbot.danam.config.TestSecurityConfig;
import com.delbot.danam.domain.category.Category;
import com.delbot.danam.domain.category.CategoryService;
import com.delbot.danam.domain.member.entity.Member;
import com.delbot.danam.global.security.jwt.token.JwtAuthenticationToken;
import com.delbot.danam.util.CustomTestUtils;

@WebMvcTest(controllers = AdminCategoryController.class)
@Import(TestSecurityConfig.class)
@AutoConfigureWebMvc
public class AdminCategoryControllerTest {
  //
  @Autowired
  MockMvc mockMvc;

  @MockBean
  CategoryService categoryService;

  // http://localhost:8080/admin/cateogry
  @Test
  @DisplayName("Category 추가 테스트")
  void addCategory_success() throws Exception {
    Category category = CustomTestUtils.categoryMockCategory("board");
    Category parent = new Category(2L, "parent", false, null, List.of(category), null);
    
    category.updateParent(parent);

    List<Category> categoryList = new ArrayList<>();
    categoryList.add(parent);
    categoryList.add(category);

    Member member = CustomTestUtils.createMockMember();
    JwtAuthenticationToken jwtAuthenticationToken = CustomTestUtils.getAdminJwtAuthenticationToken(member);

    AdminCategoryRequestDto.Info request = new AdminCategoryRequestDto.Info();
    request.setName("board");
    request.setParent("parent");

    given(categoryService.findByName(anyString())).willReturn(parent);

    given(categoryService.getCategories()).willReturn(categoryList);

    mockMvc.perform(
            post("/admin/category")
            .content(CustomTestUtils.toJson(request))
            .contentType(MediaType.APPLICATION_JSON)
            .with(authentication(jwtAuthenticationToken)))
            .andDo(print())
            .andExpect(status().isCreated());
  }
}
