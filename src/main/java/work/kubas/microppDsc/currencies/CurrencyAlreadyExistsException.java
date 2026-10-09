package work.kubas.microppDsc.currencies;

import lombok.Getter;

public class CurrencyAlreadyExistsException extends RuntimeException {
    @Getter
    private final String currencyId;
    public CurrencyAlreadyExistsException(String currencyId) {
        this.currencyId = currencyId;
        super("Currency already exists: " + currencyId);
    }
}
