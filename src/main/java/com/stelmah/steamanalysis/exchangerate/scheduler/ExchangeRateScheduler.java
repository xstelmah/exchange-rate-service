package com.stelmah.steamanalysis.exchangerate.scheduler;


import com.stelmah.steamanalysis.exchangerate.facade.ExchangeRateFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "exchange.rate.scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class ExchangeRateScheduler {

    private final ExchangeRateFacade exchangeRateFacade;

    @Scheduled(cron = "${exchange.rate.scheduler.cron}")
    @SchedulerLock(name = "fetchLatestExchangeRatesLock", lockAtLeastFor = "PT15S", lockAtMostFor = "PT30S")
    public void fetchLatestExchangeRatesTask() {
        try {
            exchangeRateFacade.fetchAndSaveExchangeRates();
        } catch (Exception e) {
            log.error("Failed to fetch latest exchange rates", e);
        }
    }
}
