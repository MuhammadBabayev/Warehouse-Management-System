package az.microservice.werehouseapplication.repository;

import az.microservice.werehouseapplication.enums.ItemStatus;
import az.microservice.werehouseapplication.model.entity.product.WarehouseProductBarcode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseProductBarcodeRepository extends JpaRepository<WarehouseProductBarcode, Long> {

//    List<WarehouseProductBarcode> findAllByProductId(Long productId);

//    Optional<WarehouseProductBarcode> findByIdAndProductId(Long id, Long productId);

//    boolean existsByBarcode(String barcode);

    Optional<WarehouseProductBarcode> findByIdAndProductIdAndStatus(Long barcodeId, Long productId, ItemStatus itemStatus);

    Optional<WarehouseProductBarcode> findFirstByBarcodeInAndStatus(List<String> incomingBarcodes, ItemStatus itemStatus);
}
