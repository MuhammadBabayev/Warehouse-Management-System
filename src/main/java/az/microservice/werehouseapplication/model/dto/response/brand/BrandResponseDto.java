package az.microservice.werehouseapplication.model.dto.response.brand;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BrandResponseDto {
    private Long id;
    private String name;
    private String description;
    private String organizationName;
}