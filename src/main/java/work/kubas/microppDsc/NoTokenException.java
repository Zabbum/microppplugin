package work.kubas.microppDsc;

public class NoTokenException extends RuntimeException {
    public NoTokenException() {
        super("No discord bot token set in config.yml!");
    }
}
