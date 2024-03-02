package com.delbot.danam.admin.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.delbot.danam.admin.dto.AdminCategoryRequestDto;
import com.delbot.danam.admin.exception.AdminErrorCode;
import com.delbot.danam.domain.category.Category;
import com.delbot.danam.domain.category.CategoryResponseDto;
import com.delbot.danam.domain.category.CategoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/category")
@RequiredArgsConstructor
public class AdminCategoryController {
  //
  private final CategoryService categoryService;

  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  @PostMapping()
  public ResponseEntity<?> addCategory(@RequestBody @Valid AdminCategoryRequestDto.Info request, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      throw AdminErrorCode.INVALID_INPUT_VALUE.defaultException();
    }

    Category category = new Category(request.getName());
    
    if (!request.getParent().isBlank()) {
      Category parent = categoryService.findByName(request.getParent());
      category.updateParent(parent);
    }
    
    categoryService.addCategory(category);

    List<Category> categoryList = categoryService.getCategories();
    List<CategoryResponseDto> response = CategoryResponseDto.listMapper(categoryList);

    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  @PutMapping()
  public ResponseEntity<?> alterCategory(@RequestBody @Valid AdminCategoryRequestDto.Info request, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      throw AdminErrorCode.INVALID_INPUT_VALUE.defaultException();
    }

    Category category = categoryService.findByName(request.getName());
    category.updateName(request.getName());

    if (!request.getParent().isBlank()) {
      Category parent = categoryService.findByName(request.getParent());
      category.updateParent(parent);
    }

    categoryService.addCategory(category);

    List<Category> categoryList = categoryService.getCategories();
    List<CategoryResponseDto> response = CategoryResponseDto.listMapper(categoryList);

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  @DeleteMapping()
  public ResponseEntity<?> deleteCategory(@RequestBody AdminCategoryRequestDto.Delete request) {
    Category category = categoryService.findByName(request.getName());

    if (category.getChildren() != null) {
      throw AdminErrorCode.OBJECT_HAS_CHILDREN.defaultException();
    }

    categoryService.deleteCategory(category);

    List<Category> categoryList = categoryService.getCategories();
    List<CategoryResponseDto> response = CategoryResponseDto.listMapper(categoryList);
    
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
