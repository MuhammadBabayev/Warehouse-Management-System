package az.microservice.werehouseapplication.service.Interface;

import az.microservice.werehouseapplication.model.dto.request.category.CreateCategoryDto;
import az.microservice.werehouseapplication.model.dto.request.category.CreateChildCategoryDto;
import az.microservice.werehouseapplication.model.dto.response.category.CategoryResponseDto;
import az.microservice.werehouseapplication.model.dto.response.category.ChildCategoryResponseDto;
import az.microservice.werehouseapplication.model.entity.product.Category;
import az.microservice.werehouseapplication.model.entity.product.ChildCategory;

import java.util.List;

public interface ICategoryService {
    CategoryResponseDto createCategory(CreateCategoryDto dto);
    CategoryResponseDto getCategoryById(Long categoryId);
    List<CategoryResponseDto> getAllCategories();
    void deleteCategory(Long categoryId);
    Category getCategoryEntityById(Long id);

    ChildCategoryResponseDto createChildCategory(Long categoryId, CreateChildCategoryDto dto);
    ChildCategoryResponseDto getChildCategoryById(Long id);
    void deleteChildCategory(Long id);
    ChildCategory getChildCategoryEntityById(Long id);
    List<ChildCategoryResponseDto> getAllChildCategoriesByCategoryId(Long categoryId);

}
