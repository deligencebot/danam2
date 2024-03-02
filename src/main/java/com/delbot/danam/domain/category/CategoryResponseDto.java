package com.delbot.danam.domain.category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
public class CategoryResponseDto {
  //
  private Long categoryId;
  private String name;
  private boolean isBoard;
  private List<CategoryResponseDto> children = new ArrayList<>();

  @Builder
  public CategoryResponseDto(Long categoryId, String name, boolean isBoard) {
    this.categoryId = categoryId;
    this.name = name;
    this.isBoard = isBoard;
  }

  public static CategoryResponseDto mapper(Category category) {
    return CategoryResponseDto.builder()
            .categoryId(category.getId())
            .name(category.getName())
            .isBoard(category.isBoard())
            .build();
  }

  public static List<CategoryResponseDto> listMapper(List<Category> categoryList) {
    List<CategoryResponseDto> categoryDtoList = new ArrayList<>();
    Map<Long, CategoryResponseDto> categoryDtoHashMap = new HashMap<>();

    categoryList.forEach(category -> {
            CategoryResponseDto categoryDto = CategoryResponseDto.mapper(category);
            categoryDtoHashMap.put(categoryDto.getCategoryId(), categoryDto);

            if (category.getParent() != null) {
              categoryDtoHashMap.get(category.getParent().getId()).getChildren().add(categoryDto);
            } else {
              categoryDtoList.add(categoryDto);
            }
    });
    
    return categoryDtoList;
  }
}
