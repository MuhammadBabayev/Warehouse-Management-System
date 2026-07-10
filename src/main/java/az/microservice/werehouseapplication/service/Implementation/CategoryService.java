package az.microservice.werehouseapplication.service.Implementation;

import az.microservice.werehouseapplication.enums.ItemStatus;
import az.microservice.werehouseapplication.exception.AlreadyExistException;
import az.microservice.werehouseapplication.exception.ExceptionMessage;
import az.microservice.werehouseapplication.exception.InternalServerError;
import az.microservice.werehouseapplication.exception.NotFoundException;
import az.microservice.werehouseapplication.mapper.CategoryMapper;
import az.microservice.werehouseapplication.mapper.ChildCategoryMapper;
import az.microservice.werehouseapplication.model.dto.request.category.CreateCategoryDto;
import az.microservice.werehouseapplication.model.dto.request.category.CreateChildCategoryDto;
import az.microservice.werehouseapplication.model.dto.response.category.CategoryResponseDto;
import az.microservice.werehouseapplication.model.dto.response.category.ChildCategoryResponseDto;
import az.microservice.werehouseapplication.model.entity.product.Category;
import az.microservice.werehouseapplication.model.entity.product.ChildCategory;
import az.microservice.werehouseapplication.repository.CategoryRepository;
import az.microservice.werehouseapplication.repository.ChildCategoryRepository;
import az.microservice.werehouseapplication.repository.ProductRepository;
import az.microservice.werehouseapplication.service.Interface.ICategoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static az.microservice.werehouseapplication.exception.ExceptionMessage.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService implements ICategoryService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final CategoryMapper categoryMapper;
    private final ChildCategoryRepository childCategoryRepository;
    private final ChildCategoryMapper childCategoryMapper;

    @Transactional
    public CategoryResponseDto createCategory(CreateCategoryDto dto) {
        log.info("Creating category with name: {}", dto.getName());

        if (categoryRepository.existsByNameAndStatus(dto.getName(), ItemStatus.ACTIVE)) {
            throw new AlreadyExistException(CATEGORY_ALREADY_EXIST.getMessage());
        }

        Optional<Category> inactiveCategory = categoryRepository
                .findByNameAndStatus(dto.getName(), ItemStatus.INACTIVE);

        if(inactiveCategory.isPresent()){
            Category category = inactiveCategory.get();
            category.setStatus(ItemStatus.ACTIVE);
            category.setDescription(dto.getDescription());

            List<ChildCategory> children = dto.getChildren().stream()
                    .map(childDto -> ChildCategory.builder()
                            .status(ItemStatus.ACTIVE)
                            .name(childDto.getName())
                            .description(childDto.getDescription())
                            .category(category)
                            .build())
                    .toList();

            return categoryMapper.toResponseDto(categoryRepository.save(category));
        }

        Category category = Category.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .build();

        categoryRepository.save(category);

        List<ChildCategory> children = dto.getChildren().stream()
                .map(childDto -> ChildCategory.builder()
                        .name(childDto.getName())
                        .description(childDto.getDescription())
                        .category(category)
                        .build())
                .collect(Collectors.toList());

        category.setChildCategories(children);
        Category saved = categoryRepository.save(category);

        return categoryMapper.toResponseDto(saved);
    }

    public List<CategoryResponseDto> getAllCategories(){
        log.info("Fetching all categories");
        List<Category> categories = categoryRepository.findAllByStatus(ItemStatus.ACTIVE);
        return categoryMapper.toResponseDtoList(categories);
    }

    public CategoryResponseDto getCategoryById(Long categoryId){
        log.info("Fetching category with id: {}", categoryId);
        Category category = findActiveCategoryById(categoryId);
        return categoryMapper.toResponseDto(category);
    }

    @Transactional
    public void deleteCategory(Long categoryId){
        log.info("Deleting category with id: {}", categoryId);

        Category category = findActiveCategoryById(categoryId);
        boolean hasActiveProducts = productRepository.existsByCategoryIdAndStatus(categoryId, ItemStatus.ACTIVE);

        if(hasActiveProducts){
            throw new InternalServerError(ExceptionMessage.INTERNAL_SERVER_ERROR.getMessage());
        }

        category.getChildCategories()
                        .forEach(child -> child.setStatus(ItemStatus.INACTIVE));

        category.setStatus(ItemStatus.INACTIVE);
        categoryRepository.save(category);
    }

    @Override
    public Category getCategoryEntityById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(CATEGORY_NOT_FOUND.getMessage()));
    }

    @Override
    @Transactional
    public ChildCategoryResponseDto createChildCategory(Long categoryId, CreateChildCategoryDto dto) {

        log.info("Creating child category with name: {}", dto.getName());

        Category category = findActiveCategoryById(categoryId);

        if (childCategoryRepository.existsByNameAndCategory(dto.getName(), category)) {
            throw new AlreadyExistException(CATEGORY_ALREADY_EXIST.getMessage());
        }


        ChildCategory childCategory = ChildCategory.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .category(category)
                .build();

        ChildCategory saved= childCategoryRepository.save(childCategory);
        return childCategoryMapper.toResponseDto(saved);
    }

    @Override
    public ChildCategoryResponseDto getChildCategoryById(Long id) {
        log.info("Fetching child category with id: {}", id);
        ChildCategory childCategory=childCategoryRepository.findByIdAndStatus(id, ItemStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException(CATEGORY_NOT_FOUND.getMessage()));
        return childCategoryMapper.toResponseDto(childCategory);
    }

    @Override
    public List<ChildCategoryResponseDto> getAllChildCategoriesByCategoryId(Long categoryId) {
        log.info("Fetching child categories for category id: {}", categoryId);
        return childCategoryRepository.findAllByCategoryIdAndStatus(categoryId, ItemStatus.ACTIVE)
                .stream()
                .map(this::toChildCategoryResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteChildCategory(Long id) {
        log.info("Deleting child category with id: {}", id);
        ChildCategory childCategory = childCategoryRepository.findByIdAndStatus(id, ItemStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException(CHILD_CATEGORY_NOT_FOUND.getMessage()));

        childCategory.setStatus(ItemStatus.INACTIVE);

        childCategoryRepository.save(childCategory);
    }

    @Override
    public ChildCategory getChildCategoryEntityById(Long id) {
        return childCategoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(CHILD_CATEGORY_NOT_FOUND.getMessage()));
    }


    private Category findActiveCategoryById(Long id){
        return categoryRepository.findByIdAndStatus(id, ItemStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException(CATEGORY_NOT_FOUND.getMessage()));
    }

    private ChildCategoryResponseDto toChildCategoryResponse(ChildCategory childCategory) {
        return ChildCategoryResponseDto.builder()
                .id(childCategory.getId())
                .name(childCategory.getName())
                .description(childCategory.getDescription())
                .categoryId(childCategory.getCategory().getId())
                .categoryName(childCategory.getCategory().getName())
                .build();
    }


}
