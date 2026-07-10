package az.microservice.werehouseapplication.repository;

import az.microservice.werehouseapplication.enums.ItemStatus;
import az.microservice.werehouseapplication.model.entity.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByBarcodesBarcode(String barcode);

//    boolean existsByBrandIdAndStatus(Long brandId, ItemStatus status);

    boolean existsByCategoryIdAndStatus(Long categoryId, ItemStatus status);

    Optional<Product> findByIdAndStatus(Long productId, ItemStatus status);

    List<Product> findAllByStatus(ItemStatus status);


    List<Product> findAllByOrganizationIdAndStatus(Long organizationId, ItemStatus status);
//    List<Product> findAllByCategoryIdAndStatus(Long categoryId, ItemStatus status);
//    List<Product> findAllByBrandIdAndStatus(Long brandId, ItemStatus status);
//
//    Optional<Product> findByBarcodesBarcodeAndStatus(List<CreateBarcodeDto> barcodes, ItemStatus itemStatus);
}
