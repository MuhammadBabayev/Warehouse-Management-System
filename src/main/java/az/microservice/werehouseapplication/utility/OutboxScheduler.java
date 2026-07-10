package az.microservice.werehouseapplication.utility;

import az.microservice.werehouseapplication.enums.OutboxStatus;
import az.microservice.werehouseapplication.model.entity.OutboxEvent;
import az.microservice.werehouseapplication.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxScheduler {

    private final OutboxEventRepository outboxEventRepository;
    private final ProcessSingleEvent processSingleEvent;
    private static final int MAX_RETRY = 3;


    @Scheduled(fixedDelay = 60000)
    public void processOutboxEvents() {
        List<OutboxEvent> pendingEvents = outboxEventRepository
                .findByStatusAndRetryCountLessThan(OutboxStatus.PENDING, MAX_RETRY);

        if (pendingEvents.isEmpty()) return;

        for (OutboxEvent event : pendingEvents) {
            processSingleEvent.singleEvent(event);
        }
    }
}
