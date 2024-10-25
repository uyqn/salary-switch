package no.uyqn;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

public class CompensationRecord {
    private final YearMonth startPeriod;
    private final BigDecimal currentAnnualSalary;
    private final BigDecimal currentBonus;
    private final double expectedSalaryMultiplier;
    private final YearMonth firstSalaryAdjustment;
    private final YearMonth firstBonusPayout;

    public CompensationRecord(YearMonth startPeriod, BigDecimal currentAnnualSalary, BigDecimal currentBonus, double expectedSalaryMultiplier, YearMonth firstSalaryAdjustment, YearMonth firstBonusPayout) {
        this.startPeriod = startPeriod;
        this.currentAnnualSalary = currentAnnualSalary;
        this.currentBonus = currentBonus;
        this.expectedSalaryMultiplier = expectedSalaryMultiplier;
        this.firstSalaryAdjustment = firstSalaryAdjustment;
        this.firstBonusPayout = firstBonusPayout;
    }

    public List<SalaryEvent> getSalaryHistory(YearMonth endPeriod) {
        if (startPeriod.isAfter(endPeriod)) {
            throw new IllegalArgumentException("End period must be after " + startPeriod);
        }
        List<SalaryEvent> salaryEvents = new ArrayList<>();

        BigDecimal currentAnnualSalary = this.currentAnnualSalary;
        YearMonth currentPeriod = startPeriod;

        while (currentPeriod.isBefore(endPeriod)) {
            currentPeriod = currentPeriod.plusMonths(1);
            if (firstSalaryAdjustment != null && currentPeriod.getMonth().equals(firstSalaryAdjustment.getMonth()) && currentPeriod.getYear() >= firstSalaryAdjustment.getYear()) {
                currentAnnualSalary = currentAnnualSalary.multiply(BigDecimal.valueOf(expectedSalaryMultiplier));
            }
            if (firstBonusPayout != null && currentPeriod.getMonth().equals(firstBonusPayout.getMonth()) && currentPeriod.getYear() >= firstBonusPayout.getYear()) {
                salaryEvents.add(SalaryEvent.of(currentPeriod, (currentAnnualSalary.divide(BigDecimal.valueOf(12), 2, RoundingMode.DOWN)).add(Optional.ofNullable(currentBonus).orElse(BigDecimal.ZERO))));
                continue;
            }
            salaryEvents.add(SalaryEvent.of(currentPeriod, currentAnnualSalary.divide(BigDecimal.valueOf(12), 2, RoundingMode.DOWN)));
        }
        return salaryEvents;
    }

    public BigDecimal getTotalCompensation(YearMonth endPeriod) {
        return getSalaryHistory(endPeriod).stream()
                .map(SalaryEvent::getSalary)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static class Builder {
        private YearMonth startPeriod;
        private BigDecimal currentAnnualSalary;
        private BigDecimal currentBonus;
        private double expectedSalaryMultiplier;
        private YearMonth firstSalaryAdjustment;
        private YearMonth firstBonusPayout;

        public Builder withStartPeriod(YearMonth startPeriod) {
            this.startPeriod = startPeriod;
            return this;
        }

        public Builder withCurrentAnnualSalary(BigDecimal currentAnnualSalary) {
            this.currentAnnualSalary = currentAnnualSalary;
            return this;
        }

        public Builder withCurrentBonus(BigDecimal currentBonus) {
            this.currentBonus = currentBonus;
            return this;
        }

        public Builder withExpectedSalaryMultiplier(double expectedSalaryMultiplier) {
            this.expectedSalaryMultiplier = expectedSalaryMultiplier;
            return this;
        }

        public Builder withFirstSalaryAdjustment(YearMonth firstSalaryAdjustment) {
            this.firstSalaryAdjustment = firstSalaryAdjustment;
            return this;
        }

        public Builder withFirstBonusPayout(YearMonth firstBonusPayout) {
            this.firstBonusPayout = firstBonusPayout;
            return this;
        }

        public CompensationRecord build() {
            return new CompensationRecord(startPeriod, currentAnnualSalary, currentBonus, expectedSalaryMultiplier, firstSalaryAdjustment, firstBonusPayout);
        }
    }
}
