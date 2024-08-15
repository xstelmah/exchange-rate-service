package com.stelmah.steamanalysis.exchangerate.scheduler;


import com.stelmah.steamanalysis.exchangerate.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExchangeRateScheduler {

    private final ExchangeRateService exchangeRateService;

    @Scheduled(cron = "${exchange.rate.scheduler.cron}")
    @SchedulerLock(name = "fetchLatestExchangeRatesLock")
    public void fetchLatestExchangeRatesTask() {
        try {
            exchangeRateService.fetchLatestExchangeRates();
        } catch (Exception e) {
            log.error("Failed to fetch latest exchange rates", e);
        }
    }
}
