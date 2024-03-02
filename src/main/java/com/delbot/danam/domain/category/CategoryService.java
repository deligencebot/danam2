package com.delbot.danam.domain.category;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {
  //
  private final CategoryRepository categoryRepository;

  @Transactional(readOnly = true)
  public List<Category> getCategories() {
    return categoryRepository.findAll();
  }
  
  @Transactional
  public void addCategory(Category category) {
    categoryRepository.save(category);
  } 

  @Transactional
  public void deleteCategory(Category category) {
    categoryRepository.delete(category);
  }

  @Transactional(readOnly = true)
  public Category findByName(String name) {
    return categoryRepository.findByName(name).orElseThrow(() -> CategoryErrorCode.NOT_FOUND_CATEGORY.defaultException());
  }
}
