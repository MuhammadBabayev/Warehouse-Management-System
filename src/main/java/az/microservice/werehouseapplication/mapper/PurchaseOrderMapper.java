package az.microservice.werehouseapplication.mapper;

import az.microservice.werehouseapplication.model.dto.response.purchaseOrder.PurchaseOrderItemResponseDto;
import az.microservice.werehouseapplication.model.dto.response.purchaseOrder.PurchaseOrderResponseDto;
import az.microservice.werehouseapplication.model.entity.inbound.PurchaseOrder;
import az.microservice.werehouseapplication.model.entity.inbound.PurchaseOrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)

public interface PurchaseOrderMapper {

//    @Mapping(source = "vendor.name", target = "vendorName")
    @Mapping(source = "warehouse.name", target = "warehouseName")
    @Mapping(source = "organization.name", target = "organizationName")
    @Mapping(source = "createdBy.username", target = "createdByUsername")
    PurchaseOrderResponseDto toResponseDto(PurchaseOrder order);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(expression = "java(item.getUnitPrice().multiply(java.math.BigDecimal.valueOf(item.getQuantity())))", target = "totalPrice")
    PurchaseOrderItemResponseDto toItemResponseDto(PurchaseOrderItem item);
}