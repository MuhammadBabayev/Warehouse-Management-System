package az.microservice.werehouseapplication.mapper;//package az.microservice.werehouseapplication.mapper;
//
//import az.microservice.werehouseapplication.model.dto.request.partner.CreateVendorDto;
//import org.mapstruct.Mapper;
//import org.mapstruct.Mapping;
//import org.mapstruct.NullValuePropertyMappingStrategy;
//import org.mapstruct.ReportingPolicy;
//
//@Mapper(
//        componentModel = "spring",
//        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
//        unmappedTargetPolicy = ReportingPolicy.IGNORE
//)
//public interface VendorMapper {
//    @Mapping(target = "id", ignore = true)
//    Vendor toEntity(CreateVendorDto dto);
//
//
//    VendorResponseDto toResponseDto(Vendor vendor);
//}
