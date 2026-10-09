package work.kubas.microppDsc.currencies;

import lombok.Getter;

public class InvalidCurrencyFormatException extends RuntimeException {
    @Getter
    private final String providedFormat;
    public InvalidCurrencyFormatException(String providedFormat) {
        this.providedFormat = providedFormat;
        super("Invalid currency format: " + providedFormat + ". Currency's id should be 3 uppercase characters.");
    }
}
