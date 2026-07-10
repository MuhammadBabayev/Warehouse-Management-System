package az.microservice.werehouseapplication.service.Interface;

import az.microservice.werehouseapplication.model.dto.request.barcode.CreateBarcodeDto;
import az.microservice.werehouseapplication.model.dto.request.product.CreateProductDto;
import az.microservice.werehouseapplication.model.dto.request.product.UpdateProductDto;
import az.microservice.werehouseapplication.model.dto.response.barcode.BarcodeResponseDto;
import az.microservice.werehouseapplication.model.dto.response.product.ProductResponseDto;
import az.microservice.werehouseapplication.model.entity.product.Product;

import java.util.List;

public interface IProductService {

    ProductResponseDto createProduct(CreateProductDto dto);
    BarcodeResponseDto addBarcode(Long productId, CreateBarcodeDto dto);
    ProductResponseDto addProductCount(Long productId, Integer productCount);
    List<ProductResponseDto> getAllProducts();
    List<ProductResponseDto> getProductsByOrganization(Long organizationId);
    ProductResponseDto getProductById(Long productId);
    ProductResponseDto getProductByBarcode(String barcode);
    ProductResponseDto updateProduct(Long productId, UpdateProductDto updatedProduct);
    void deleteProduct(Long productId);
    void deleteBarcode(Long productId, Long barcodeId);
    Product getProductEntityById(Long id);

}
