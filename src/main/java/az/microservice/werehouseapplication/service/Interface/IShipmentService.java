package az.microservice.werehouseapplication.service.Interface;

import az.microservice.werehouseapplication.model.dto.response.outbound.ShipmentResponseDto;
import az.microservice.werehouseapplication.model.entity.outbount.Shipment;
import az.microservice.werehouseapplication.model.entity.transfer.Transfer;

import java.util.List;

public interface IShipmentService {
//    ShipmentResponseDto create(CreateShipmentDto dto);
    ShipmentResponseDto getById(Long id);
    List<ShipmentResponseDto> getAllBySalesOrderId(Long salesOrderId);
//    void ship(Long id);
//    void deliver(Long id);
//    void returnShipment(Long id);
    Shipment createForTransfer(Transfer transfer);
}
