package com.delbot.danam.domain.category;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {
  //
  private final CategoryService categoryService;

  @GetMapping()
  public ResponseEntity<List<CategoryResponseDto>> getCategory() {
    List<Category> categoryList = categoryService.getCategories();

    List<CategoryResponseDto> response = CategoryResponseDto.listMapper(categoryList);
    
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
