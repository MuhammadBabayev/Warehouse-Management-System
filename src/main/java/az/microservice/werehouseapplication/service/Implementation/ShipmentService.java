package az.microservice.werehouseapplication.service.Implementation;

import az.microservice.werehouseapplication.enums.SalesOrderStatus;
import az.microservice.werehouseapplication.enums.ShipmentStatus;
import az.microservice.werehouseapplication.exception.NotFoundException;
import az.microservice.werehouseapplication.exception.old.MyException;
import az.microservice.werehouseapplication.model.dto.response.outbound.ShipmentResponseDto;
import az.microservice.werehouseapplication.model.entity.outbount.SalesOrder;
import az.microservice.werehouseapplication.model.entity.outbount.Shipment;
import az.microservice.werehouseapplication.model.entity.transfer.Transfer;
import az.microservice.werehouseapplication.repository.SalesOrderRepository;
import az.microservice.werehouseapplication.repository.ShipmentRepository;
import az.microservice.werehouseapplication.repository.UserRepository;
import az.microservice.werehouseapplication.service.Interface.ISalesOrderService;
import az.microservice.werehouseapplication.service.Interface.IShipmentService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static az.microservice.werehouseapplication.exception.ExceptionMessage.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShipmentService implements IShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final UserRepository userRepository;
    private final ISalesOrderService salesOrderService;
    private final SalesOrderRepository salesOrderRepository;
    private final EntityManager entityManager;

//    @Override
//    @Transactional
//    public ShipmentResponseDto create(CreateShipmentDto dto) {
//        log.info("Creating shipment for sales order id: {}", dto.getSalesOrderId());
//
//        SalesOrder salesOrder = salesOrderService.getSalesOrderEntityById(dto.getSalesOrderId());
//
//        if (salesOrder.getStatus() != SalesOrderStatus.PICKING) {
//            throw new ShipmentCreationNotAllowedException(SHIPMENT_CREATION_REQUIRES_COMPLETED_PICKING.getMessage());
//        }
//
//        User driver = null;
//        if (dto.getDriverId() != null) {
//            driver = userRepository.findById(dto.getDriverId())
//                    .orElseThrow(() -> new NotFoundException(DRIVER_NOT_FOUND.getMessage()));
//        }
//
//        String year = String.valueOf(LocalDateTime.now().getYear());
//        String sequence = String.format("%04d", shipmentRepository.count() + 1);
//        String trackingNumber = "TRK-" + year + "-" + sequence;
//
//        Shipment shipment = Shipment.builder()
//                .salesOrder(salesOrder)
//                .driver(driver)
//                .trackingNumber(trackingNumber)
//                .notes(dto.getNotes())
//                .build();
//
//        shipmentRepository.save(shipment);
//
//        return toResponse(shipment);
//    }

    @Override
    public ShipmentResponseDto getById(Long id) {
        log.info("Fetching shipment with id: {}", id);

        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(SHIPMENT_NOT_FOUND.getMessage()));

        return toResponse(shipment);
    }

    @Override
    public List<ShipmentResponseDto> getAllBySalesOrderId(Long salesOrderId) {
        log.info("Fetching shipments for sales order id: {}", salesOrderId);

        return shipmentRepository.findAllBySalesOrderId(salesOrderId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

//    @Override
//    @Transactional
//    public void ship(Long id) {
//        log.info("Shipping shipment with id: {}", id);
//
//        Shipment shipment = shipmentRepository.findById(id)
//                .orElseThrow(() -> new NotFoundException(SHIPMENT_NOT_FOUND.getMessage()));
//
//        if (shipment.getStatus() != ShipmentStatus.PREPARING) {
//            throw new ShipmentNotReadyForShippingException(ONLY_PREPARING_SHIPMENTS_CAN_BE_SHIPPED.getMessage());
//        }
//
//        shipment.setStatus(ShipmentStatus.SHIPPED);
//        shipment.setShippedAt(LocalDateTime.now());
//        shipmentRepository.save(shipment);
//
//        // SalesOrder statusunu SHIPPED-ə dəyiş
//        salesOrderService.updateStatus(shipment.getSalesOrder().getId(), SalesOrderStatus.SHIPPED);
//    }

//    @Override
//    @Transactional
//    public void deliver(Long id) {
//        log.info("Delivering shipment with id: {}", id);
//
//        Shipment shipment = shipmentRepository.findById(id)
//                .orElseThrow(() -> new NotFoundException(SHIPMENT_NOT_FOUND.getMessage()));
//
//        if (shipment.getStatus() != ShipmentStatus.SHIPPED) {
//            throw new ShipmentNotReadyForShippingException(ONLY_SHIPPED_SHIPMENTS_CAN_BE_DELIVERED.getMessage());
//        }
//
//        shipment.setStatus(ShipmentStatus.DELIVERED);
//        shipment.setDeliveredAt(LocalDateTime.now());
//        shipmentRepository.save(shipment);
//
//        // SalesOrder statusunu DELIVERED-ə dəyiş
//        salesOrderService.updateStatus(shipment.getSalesOrder().getId(), SalesOrderStatus.DELIVERED);
//    }

//    @Override
//    @Transactional
//    public void returnShipment(Long id) {
//        log.info("Returning shipment with id: {}", id);
//
//        Shipment shipment = shipmentRepository.findById(id)
//                .orElseThrow(() -> new NotFoundException(SHIPMENT_NOT_FOUND.getMessage()));
//
//        if (shipment.getStatus() != ShipmentStatus.SHIPPED) {
//            throw new ShipmentReturnNotAllowedException(ONLY_SHIPPED_SHIPMENTS_CAN_BE_RETURNED.getMessage());
//        }
//
//        shipment.setStatus(ShipmentStatus.RETURNED);
//        shipmentRepository.save(shipment);
//    }

    @Transactional
    public Shipment createForTransfer(Transfer transfer) {
        log.info("Creating Shipment for transfer: {}", transfer.getTransferNumber());

        SalesOrder salesOrder = salesOrderRepository.findByTransfer(transfer)
                .orElseThrow(() -> new MyException("SalesOrder not found for transfer: " + transfer.getId()));

        Long nextVal = (Long) entityManager
                .createNativeQuery("SELECT NEXTVAL('shipment_tracking_seq')")
                .getSingleResult();
        String trackingNumber = "SHP-" + LocalDateTime.now().getYear() + "-" + String.format("%04d", nextVal);

        Shipment shipment = Shipment.builder()
                .salesOrder(salesOrder)
                .trackingNumber(trackingNumber)
                .status(ShipmentStatus.SHIPPED)                   // ✅ auto-shipped for transfer
                .shippedAt(LocalDateTime.now())
                .notes("Auto-created for transfer: " + transfer.getTransferNumber())
                .build();

        shipmentRepository.save(shipment);

        // ✅ update SalesOrder status
        salesOrder.setStatus(SalesOrderStatus.SHIPPED);
        salesOrderRepository.save(salesOrder);

        log.info("Shipment created: {}", trackingNumber);
        return shipment;
    }

    private ShipmentResponseDto toResponse(Shipment shipment) {
        return ShipmentResponseDto.builder()
                .id(shipment.getId())
                .trackingNumber(shipment.getTrackingNumber())
                .status(shipment.getStatus())
                .salesOrderId(shipment.getSalesOrder().getId())
                .salesOrderNumber(shipment.getSalesOrder().getOrderNumber())
                .driverUsername(shipment.getDriver() != null ? shipment.getDriver().getUsername() : null)
                .shippedAt(shipment.getShippedAt())
                .deliveredAt(shipment.getDeliveredAt())
                .createdAt(shipment.getCreatedAt())
                .notes(shipment.getNotes())
                .build();
    }
}