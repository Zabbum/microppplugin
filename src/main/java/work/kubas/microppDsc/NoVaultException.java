package work.kubas.microppDsc;

public class NoVaultException extends RuntimeException {
    public NoVaultException() {
        super("Vault plugin not installed!");
    }
}
