package work.kubas.microppDsc.currencies;

import lombok.Getter;

public class NoSuchCurrencyException extends RuntimeException {
    @Getter
    private final String currencyId;
    public NoSuchCurrencyException(String currencyId) {
        this.currencyId = currencyId;
        super("No such currency: " + currencyId);
    }
}
