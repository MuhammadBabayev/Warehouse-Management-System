package az.microservice.werehouseapplication.utility;

import az.microservice.werehouseapplication.enums.OutboxStatus;
import az.microservice.werehouseapplication.model.dto.request.transfer.TransferOutboxPayload;
import az.microservice.werehouseapplication.model.entity.OutboxEvent;
import az.microservice.werehouseapplication.repository.OutboxEventRepository;
import az.microservice.werehouseapplication.service.Implementation.InventoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Component
@RequiredArgsConstructor
public class ProcessSingleEvent {
    private final InventoryService inventoryService;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    private static final int MAX_RETRY = 3;

    @Transactional
    public void singleEvent(OutboxEvent event) {
        try {
            if ("TRANSFER_CREATED".equals(event.getEventType())) {
                TransferOutboxPayload payload = objectMapper
                        .readValue(event.getPayload(), TransferOutboxPayload.class);

                payload.getItems().forEach(item ->
                        inventoryService.decreaseStock(
                                item.getProductId(),
                                payload.getFromLocationId(),
                                item.getQuantity())
                );
            }

            event.setStatus(OutboxStatus.PROCESSED);
            event.setProcessedAt(LocalDateTime.now());

        } catch (Exception e) {
            event.setRetryCount(event.getRetryCount() + 1);
            if (event.getRetryCount() >= MAX_RETRY) {
                event.setStatus(OutboxStatus.FAILED);
            }
        }

        outboxEventRepository.save(event);
    }
}
