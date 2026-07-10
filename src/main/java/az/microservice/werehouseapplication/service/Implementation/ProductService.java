package az.microservice.werehouseapplication.service.Implementation;

import az.microservice.werehouseapplication.enums.ItemStatus;
import az.microservice.werehouseapplication.exception.NotFoundException;
import az.microservice.werehouseapplication.mapper.BarcodeMapper;
import az.microservice.werehouseapplication.mapper.ProductMapper;
import az.microservice.werehouseapplication.model.dto.request.barcode.CreateBarcodeDto;
import az.microservice.werehouseapplication.model.dto.request.product.CreateProductDto;
import az.microservice.werehouseapplication.model.dto.request.product.UpdateProductDto;
import az.microservice.werehouseapplication.model.dto.response.barcode.BarcodeResponseDto;
import az.microservice.werehouseapplication.model.dto.response.product.ProductResponseDto;
import az.microservice.werehouseapplication.model.entity.product.Brand;
import az.microservice.werehouseapplication.model.entity.product.Category;
import az.microservice.werehouseapplication.model.entity.product.Product;
import az.microservice.werehouseapplication.model.entity.product.WarehouseProductBarcode;
import az.microservice.werehouseapplication.model.entity.warehouse.Organization;
import az.microservice.werehouseapplication.repository.*;
import az.microservice.werehouseapplication.service.Interface.IProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static az.microservice.werehouseapplication.exception.ExceptionMessage.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService implements IProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final WarehouseProductBarcodeRepository barcodeRepository;
    private final BarcodeMapper barcodeMapper;
    private final ProductMapper productMapper;


    private final OrganizationRepository organizationRepository;

    @Override
    @Transactional
    public ProductResponseDto createProduct(CreateProductDto dto) {

        log.info("Creating product with name: {}", dto.getName());

        Organization organization = organizationRepository.findByIdAndStatus(dto.getOrganizationId(), ItemStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException(ORGANIZATION_NOT_FOUND.getMessage()));

        Category category = categoryRepository.findByIdAndStatus(dto.getCategoryId(), ItemStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException(CATEGORY_NOT_FOUND.getMessage()));

        Brand brand = brandRepository.findByIdAndStatus(dto.getBrandId(), ItemStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException(BRAND_NOT_FOUND.getMessage()));

        // Check if any barcode from dto already exists in DB with INACTIVE status
        if (dto.getBarcodes() != null && !dto.getBarcodes().isEmpty()) {
            List<String> incomingBarcodes = dto.getBarcodes().stream()
                    .map(b -> b.getBarcode())
                    .collect(Collectors.toList());

            Optional<WarehouseProductBarcode> existingBarcode = barcodeRepository
                    .findFirstByBarcodeInAndStatus(incomingBarcodes, ItemStatus.INACTIVE);

            if (existingBarcode.isPresent()) {
                Product existingProduct = existingBarcode.get().getProduct();

                existingProduct.setStatus(ItemStatus.ACTIVE);
                existingProduct.setName(dto.getName());
                existingProduct.setDescription(dto.getDescription());
                existingProduct.setCategory(category);
                existingProduct.setBrand(brand);
                existingProduct.setOrganization(organization);
                existingProduct.setUnit(dto.getUnit());
                existingProduct.setImageUrl(dto.getImageUrl());
                existingProduct.setPurchasePrice(dto.getPurchasePrice());
                existingProduct.setSellingPrice(dto.getSellingPrice());
                existingProduct.setMinStock(dto.getMinStockLevel());
                existingProduct.setWeight(dto.getWeight());
                existingProduct.setProductCount(dto.getProductCount());

                List<WarehouseProductBarcode> existingBarcodes = existingProduct.getBarcodes();
                existingBarcodes.forEach(b -> b.setStatus(ItemStatus.INACTIVE)); // reset all first

                for (WarehouseProductBarcode existingB : existingBarcodes) {
                    dto.getBarcodes().stream()
                            .filter(d -> d.getBarcode().equals(existingB.getBarcode()))
                            .findFirst()
                            .ifPresent(matchedDto -> {
                                existingB.setStatus(ItemStatus.ACTIVE);
                                existingB.setType(matchedDto.getType());
                                existingB.setQuantity(matchedDto.getQuantity());
                                existingB.setPrimary(matchedDto.isPrimary());
                            });
                }

                List<String> existingBarcodeValues = existingBarcodes.stream()
                        .map(WarehouseProductBarcode::getBarcode)
                        .collect(Collectors.toList());

                dto.getBarcodes().stream()
                        .filter(d -> !existingBarcodeValues.contains(d.getBarcode()))
                        .forEach(newBarcodeDto -> existingBarcodes.add(
                                WarehouseProductBarcode.builder()
                                        .barcode(newBarcodeDto.getBarcode())
                                        .type(newBarcodeDto.getType())
                                        .quantity(newBarcodeDto.getQuantity())
                                        .isPrimary(newBarcodeDto.isPrimary())
                                        .product(existingProduct)
                                        .build()
                        ));

                return toResponseDto(productRepository.save(existingProduct));
            }
        }

        Product product = Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .category(category)
                .brand(brand)
                .organization(organization)
                .unit(dto.getUnit())
                .imageUrl(dto.getImageUrl())
                .sku(generateSku(brand.getName(), category.getName()))
                .purchasePrice(dto.getPurchasePrice())
                .sellingPrice(dto.getSellingPrice())
                .minStock(dto.getMinStockLevel())
                .weight(dto.getWeight())
                .productCount(dto.getProductCount())
                .build();
        productRepository.save(product);

        if (dto.getBarcodes() != null && !dto.getBarcodes().isEmpty()) {
            List<WarehouseProductBarcode> barcodeList = dto.getBarcodes().stream()
                    .map(barcodeDto -> WarehouseProductBarcode.builder()
                            .barcode(barcodeDto.getBarcode())
                            .type(barcodeDto.getType())
                            .quantity(barcodeDto.getQuantity())
                            .isPrimary(barcodeDto.isPrimary())
                            .product(product)
                            .build())
                    .collect(Collectors.toList());

            barcodeRepository.saveAll(barcodeList);
            product.setBarcodes(barcodeList);
        }

        return toResponseDto(product);
    }

    @Override
    @Transactional
    public BarcodeResponseDto addBarcode(Long productId, CreateBarcodeDto dto) {

        Product product = productRepository.findByIdAndStatus(productId, ItemStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND.getMessage()));

        WarehouseProductBarcode barcode = barcodeMapper.toEntity(dto);
        barcode.setProduct(product);
        barcodeRepository.save(barcode);
        return barcodeMapper.toResponseDto(barcode);
    }

    @Override
    @Transactional
    public ProductResponseDto addProductCount(Long productId, Integer productCount){
        Product product = productRepository.findByIdAndStatus(productId, ItemStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND.getMessage()));
        Integer newCount = productCount + product.getProductCount();

        product.setProductCount(newCount);
        productRepository.save(product);

        return toResponseDto(product);
    }

    @Override
    public List<ProductResponseDto> getAllProducts(){
        return productRepository.findAllByStatus(ItemStatus.ACTIVE)
                .stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponseDto> getProductsByOrganization(Long organizationId) {

        Organization organization = organizationRepository.findByIdAndStatus(organizationId, ItemStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException(ORGANIZATION_NOT_FOUND.getMessage()));

            return productRepository.findAllByOrganizationIdAndStatus(
                            organization.getId(), ItemStatus.ACTIVE)
                    .stream()
                    .map(this::toResponseDto)
                    .collect(Collectors.toList());
    }

    @Override
    public ProductResponseDto getProductById(Long productId) {
        Product product = productRepository.findByIdAndStatus(productId, ItemStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND.getMessage()));

        return toResponseDto(product);
    }

    @Override
    public ProductResponseDto getProductByBarcode(String barcode) {
        Product product = productRepository.findByBarcodesBarcode(barcode)
                .orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND.getMessage()));

        return toResponseDto(product);
    }

    @Override
    @Transactional
    public ProductResponseDto updateProduct(Long productId, UpdateProductDto updatedProduct) {
        Product existingProduct = productRepository.findByIdAndStatus(productId, ItemStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND.getMessage()));

        productMapper.updateProductFromDto(updatedProduct, existingProduct);
        return toResponseDto(existingProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long productId) {

        Product product = productRepository.findByIdAndStatus(productId, ItemStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND.getMessage()));

        product.getBarcodes()
                        .forEach(barcodes -> barcodes.setStatus(ItemStatus.INACTIVE));

        product.setStatus(ItemStatus.INACTIVE);
        productRepository.save(product);
    }

    @Override
    @Transactional
    public void deleteBarcode(Long productId, Long barcodeId) {
        WarehouseProductBarcode barcode = barcodeRepository.findByIdAndProductIdAndStatus(barcodeId, productId, ItemStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException(BARCODE_NOT_FOUND.getMessage()));

        barcode.setStatus(ItemStatus.INACTIVE);
        barcodeRepository.save(barcode);
    }

    @Override
    public Product getProductEntityById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND.getMessage()));
    }

    //Helper Methods

    private String generateSku(String brandName, String categoryName) {
        String brandCode = brandName.substring(0, Math.min(3, brandName.length())).toUpperCase();
        String categoryCode = categoryName.substring(0, Math.min(3, categoryName.length())).toUpperCase();
        String uniquePart = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return brandCode + "-" + categoryCode + "-" + uniquePart;
    }

    private ProductResponseDto toResponseDto(Product dto) {
        Organization organization = organizationRepository.findByIdAndStatus(dto.getOrganization().getId(), ItemStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException(ORGANIZATION_NOT_FOUND.getMessage()));

        Category category = categoryRepository.findByIdAndStatus(dto.getCategory().getId(), ItemStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException(CATEGORY_NOT_FOUND.getMessage()));

        Brand brand = brandRepository.findByIdAndStatus(dto.getBrand().getId(), ItemStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException(BRAND_NOT_FOUND.getMessage()));

        List<BarcodeResponseDto> barcodeDtos = dto.getBarcodes() == null
                ? List.of()
                : dto.getBarcodes().stream()
                .map(b -> BarcodeResponseDto.builder()
                        .id(b.getId())
                        .barcode(b.getBarcode())
                        .type(b.getType())
                        .quantity(b.getQuantity())
                        .isPrimary(b.isPrimary())
                        .build())
                .toList();

        return ProductResponseDto.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .categoryName(category.getName())
                .brandName(brand.getName())
                .organizationName(organization.getName())
                .unit(dto.getUnit())
                .imageUrl(dto.getImageUrl())
                .sku(generateSku(brand.getName(), category.getName()))
                .purchasePrice(dto.getPurchasePrice())
                .sellingPrice(dto.getSellingPrice())
                .minStock(dto.getMinStock())
                .weight(dto.getWeight())
                .barcode(barcodeDtos)
                .productCount(dto.getProductCount())
                .build();
    }
}

