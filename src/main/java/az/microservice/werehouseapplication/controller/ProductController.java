package az.microservice.werehouseapplication.controller;

import az.microservice.werehouseapplication.model.dto.request.barcode.CreateBarcodeDto;
import az.microservice.werehouseapplication.model.dto.request.product.CreateProductDto;
import az.microservice.werehouseapplication.model.dto.request.product.UpdateProductDto;
import az.microservice.werehouseapplication.model.dto.response.barcode.BarcodeResponseDto;
import az.microservice.werehouseapplication.model.dto.response.product.ProductResponseDto;
import az.microservice.werehouseapplication.service.Interface.IProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
public class ProductController {

    private final IProductService productService;

    @PostMapping
    @PreAuthorize("hasAuthority('product.create')")
    public ResponseEntity<ProductResponseDto> createProduct(@Valid @RequestBody CreateProductDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(dto));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('product.view')")
    public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/organization/{organizationId}")
    @PreAuthorize("hasAuthority('product.view')")
    public ResponseEntity<List<ProductResponseDto>> getProductsByOrganization(@PathVariable Long organizationId) {
        return ResponseEntity.ok(productService.getProductsByOrganization(organizationId));
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('product.view')")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/barcode/{barcode}")
    @PreAuthorize("hasAuthority('product.view')")
    public ResponseEntity<ProductResponseDto> getProductByBarcode(@PathVariable String barcode) {
        return ResponseEntity.ok(productService.getProductByBarcode(barcode));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('product.update')")
    public ResponseEntity<ProductResponseDto> updateProduct(@PathVariable Long id,
                                                            @RequestBody UpdateProductDto dto) {
        return ResponseEntity.ok(productService.updateProduct(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('product.delete')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // region Barcode

    @PostMapping("/{id}/barcodes")
    @PreAuthorize("hasAuthority('product.create')")
    public ResponseEntity<BarcodeResponseDto> addBarcode(@PathVariable Long id,
                                                         @Valid @RequestBody CreateBarcodeDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.addBarcode(id, dto));
    }

    @PatchMapping("/add-product/{id}")
    @PreAuthorize("hasAuthority('product.update')")
    public ResponseEntity<ProductResponseDto> addProductCount(@PathVariable Long id,
                                                              @RequestParam Integer productCount){
        return ResponseEntity.ok(productService.addProductCount(id, productCount));
    }

    @DeleteMapping("/{id}/barcodes/{barcodeId}")
    @PreAuthorize("hasAuthority('product.delete')")
    public ResponseEntity<Void> deleteBarcode(@PathVariable Long id,
                                              @PathVariable Long barcodeId) {
        productService.deleteBarcode(id, barcodeId);
        return ResponseEntity.noContent().build();
    }
    // endregion
}
