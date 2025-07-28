package com.stelmah.steamanalysis.exchangerate.scheduler;


import com.stelmah.steamanalysis.exchangerate.external.exchangerateapi.mapper.ExchangeRateApiMapper;
import com.stelmah.steamanalysis.exchangerate.facade.ExchangeRateFacade;
import com.stelmah.steamanalysis.exchangerate.service.ExchangeRateSnapshotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "exchange.rate.scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class ExchangeRateScheduler {

    private final ExchangeRateFacade exchangeRateFacade;
    private final ExchangeRateSnapshotService exchangeRateSnapshotService;

    @Scheduled(cron = "${exchange.rate.scheduler.cron}")
    @SchedulerLock(name = "fetchLatestExchangeRatesLock", lockAtLeastFor = "PT15S", lockAtMostFor = "PT30S")
    public void fetchLatestExchangeRatesTask() {
        try {
            var snapshot = exchangeRateSnapshotService.findLatestByVendor(ExchangeRateApiMapper.VENDOR_NAME);
            var today = LocalDate.now().atStartOfDay();
            if (snapshot.isEmpty() || snapshot.get().getVendorTimestamp().isBefore(today)) {
                exchangeRateFacade.fetchAndSaveExchangeRates();
                log.info("Exchange rate fetch job done");
            } else {
                log.debug("Skipping exchange rate fetch job");
            }
        } catch (Exception e) {
            log.error("Failed to fetch latest exchange rates", e);
        }
    }
}
