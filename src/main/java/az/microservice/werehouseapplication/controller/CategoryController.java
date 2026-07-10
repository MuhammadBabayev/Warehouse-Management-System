package az.microservice.werehouseapplication.controller;

import az.microservice.werehouseapplication.model.dto.request.category.CreateCategoryDto;
import az.microservice.werehouseapplication.model.dto.request.category.CreateChildCategoryDto;
import az.microservice.werehouseapplication.model.dto.response.category.CategoryResponseDto;
import az.microservice.werehouseapplication.model.dto.response.category.ChildCategoryResponseDto;
import az.microservice.werehouseapplication.service.Implementation.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasAuthority('product.create')")
    @Operation(summary = "create category")
    public ResponseEntity<CategoryResponseDto> createCategory(@RequestBody CreateCategoryDto dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(dto));
    }

    @GetMapping
    @Operation(summary = "get all category")
    public List<CategoryResponseDto> getAllCategories(){
        return categoryService.getAllCategories();
    }

    @GetMapping("/{id}")
    @Operation(summary = "get category by id")
    public CategoryResponseDto getById(@PathVariable Long id){
        return categoryService.getCategoryById(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('product.delete')")
    public String deleteCategory(@PathVariable Long id){
        categoryService.deleteCategory(id);
        return "category deleted succesfully";
    }

    @PostMapping("/{categoryId}/children")
    @PreAuthorize("hasAuthority('product.create')")
    public ResponseEntity<ChildCategoryResponseDto> createChildCategory(
            @PathVariable Long categoryId,
            @Valid @RequestBody CreateChildCategoryDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryService.createChildCategory(categoryId, dto));
    }

    @GetMapping("/{categoryId}/children")
    @PreAuthorize("hasAuthority('product.view')")
    public ResponseEntity<List<ChildCategoryResponseDto>> getAllChildCategories(
            @PathVariable Long categoryId) {
        return ResponseEntity.ok(categoryService.getAllChildCategoriesByCategoryId(categoryId));
    }

    @GetMapping("/children/{id}")
    @PreAuthorize("hasAuthority('product.view')")
    public ResponseEntity<ChildCategoryResponseDto> getChildCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getChildCategoryById(id));
    }

    @DeleteMapping("/children/{id}")
    @PreAuthorize("hasAuthority('product.delete')")
    public ResponseEntity<Void> deleteChildCategory(@PathVariable Long id) {
        categoryService.deleteChildCategory(id);
        return ResponseEntity.noContent().build();
    }
}