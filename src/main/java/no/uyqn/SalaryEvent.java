package no.uyqn;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Objects;

public final class SalaryEvent {
    private final YearMonth period;
    private final BigDecimal salary;

    private SalaryEvent(YearMonth period, BigDecimal salary) {
        this.period = period;
        this.salary = salary;
    }

    public YearMonth getPeriod() {
        return period;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SalaryEvent that)) return false;
        return Objects.equals(getPeriod(), that.getPeriod()) && Objects.equals(getSalary(), that.getSalary());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPeriod(), getSalary());
    }

    @Override
    public String toString() {
        return "SalaryEvent{" +
                "period=" + period +
                ", salary=" + salary +
                ", annualSalary=" + salary.multiply(BigDecimal.valueOf(12)) +
                '}';
    }

    public static SalaryEvent of(YearMonth period, BigDecimal salary) {
        return new SalaryEvent(period, salary);
    }
}
