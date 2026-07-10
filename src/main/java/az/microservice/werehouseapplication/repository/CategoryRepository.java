package az.microservice.werehouseapplication.repository;

import az.microservice.werehouseapplication.enums.ItemStatus;
import az.microservice.werehouseapplication.model.entity.product.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByStatus(ItemStatus productStatus);

    Optional<Category> findByIdAndStatus(Long id, ItemStatus status);

    boolean existsByName(String name);

    Optional<Category> findByNameAndStatus(String name, ItemStatus itemStatus);

    boolean existsByNameAndStatus(String name, ItemStatus itemStatus);
}
