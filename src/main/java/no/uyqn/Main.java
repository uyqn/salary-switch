package no.uyqn;

import javax.annotation.processing.SupportedSourceVersion;
import javax.print.DocFlavor;
import java.awt.image.AreaAveragingScaleFilter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        YearMonth endPeriod = YearMonth.of(2027, 5);
            double expectedSalaryAdjustment = 1.09;

        BigDecimal[] bonuses = {BigDecimal.valueOf(69854), BigDecimal.valueOf(59854), BigDecimal.valueOf(42998), BigDecimal.valueOf(36183)};
        BigDecimal averageBonus = Arrays.stream(bonuses).reduce(BigDecimal.ZERO, BigDecimal::add).divide(BigDecimal.valueOf(bonuses.length), 2, RoundingMode.DOWN);

        //736_878
        CompensationRecord tripletex = new CompensationRecord.Builder()
                .withCurrentAnnualSalary(BigDecimal.valueOf(750_000))
                .withExpectedSalaryMultiplier(expectedSalaryAdjustment)
                .withFirstSalaryAdjustment(YearMonth.of(2025, 4))
                .withStartPeriod(YearMonth.now())
                .build();

        CompensationRecord bouvetCorrection = new CompensationRecord.Builder()
                .withCurrentAnnualSalary(BigDecimal.valueOf(702000))
                .withStartPeriod(YearMonth.now())
                .build();
        CompensationRecord bouvet = new CompensationRecord.Builder()
                .withStartPeriod(YearMonth.of(2025, 1))
                .withCurrentAnnualSalary(BigDecimal.valueOf(750_000))
                .withCurrentBonus(BigDecimal.valueOf(70_000))
                .withExpectedSalaryMultiplier(expectedSalaryAdjustment)
                .withFirstSalaryAdjustment(YearMonth.of(2026, 5))
                .withFirstBonusPayout(YearMonth.of(2026, 5))
                .build();

        List<SalaryEvent> bouvetSalaryHistory = new ArrayList<>(bouvetCorrection.getSalaryHistory(YearMonth.of(2025, 1)));
        bouvetSalaryHistory.addAll(bouvet.getSalaryHistory(endPeriod));

        List<SalaryEvent> tripletexSalaryHistory = new ArrayList<>(tripletex.getSalaryHistory(endPeriod));

        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(' ');

        DecimalFormat formatter = new DecimalFormat("#,###.00", symbols);

        System.out.println("Average averageBonus: " + formatter.format(averageBonus));
        System.out.println("\t\t\t\t\tTripletex  \t|\t Bouvet");

        for (int i = 0; i < bouvetSalaryHistory.size(); i++) {
            SalaryEvent bouvetSalaryEvent = bouvetSalaryHistory.get(i);
            SalaryEvent tripletexSalaryEvent = tripletexSalaryHistory.get(i);

            String str = "Period: %s: \t%s \t|\t %s".formatted(
                    bouvetSalaryEvent.getPeriod(),
                    formatter.format(tripletexSalaryEvent.getSalary()),
                    formatter.format(bouvetSalaryEvent.getSalary())
            );
            System.out.println(str);
        }
        System.out.println("\t\t\t\t\tTripletex  \t|\t Bouvet");

        BigDecimal totalBouvetCompensation = bouvet.getTotalCompensation(endPeriod).add(bouvetCorrection.getTotalCompensation(YearMonth.of(2025, 1)));
        BigDecimal totalTripletexCompensation = tripletex.getTotalCompensation(endPeriod);

        System.out.println("-----------------");

        System.out.println("Total compensation calculated at " + endPeriod.getMonth() + " " + endPeriod.getYear());
        System.out.println("Total compensation for Bouvet: " + formatter.format(totalBouvetCompensation));
        System.out.println("Total compensation for Tripletex: " + formatter.format(totalTripletexCompensation));

        System.out.println("Difference: " + formatter.format(totalTripletexCompensation.subtract(totalBouvetCompensation)));
    }
}