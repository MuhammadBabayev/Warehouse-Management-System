package az.microservice.werehouseapplication.model.dto.request.barcode;

import az.microservice.werehouseapplication.enums.BarcodeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBarcodeDto {

    @NotBlank(message = "Barcode cannot be empty")
    private String barcode;

    @NotNull(message = "Barcode type cannot be empty")
    private BarcodeType type;

    private Integer quantity = 1;

    private boolean isPrimary = false;

    //productId path-dən gəlir, dto-dan yox. Frontend-dən ayrıca göndərməyə ehtiyac yoxdur.
    // eyni məlumat həm URL-də, həm body-də olacaq. Bu təkrardır və lazımsızdır.
    //Əgər productId-ni body-də də göndərsək —

//        private Long productId;
//        private  BarcodeType barcodeType;

}
