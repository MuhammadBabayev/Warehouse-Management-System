package az.microservice.werehouseapplication.repository;

import az.microservice.werehouseapplication.enums.ItemStatus;
import az.microservice.werehouseapplication.model.entity.product.Category;
import az.microservice.werehouseapplication.model.entity.product.ChildCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChildCategoryRepository extends JpaRepository<ChildCategory, Long> {
    Optional<ChildCategory> findByIdAndStatus(Long id, ItemStatus status);
    List<ChildCategory> findAllByCategoryIdAndStatus(Long categoryId, ItemStatus status);
    boolean existsByNameAndCategory(String name, Category category);
}
