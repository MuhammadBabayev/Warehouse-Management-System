package az.microservice.werehouseapplication.model.dto.request.invoice;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CheckActionRequest {
    private String notes;   // optional
}
