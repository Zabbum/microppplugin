package work.kubas.microppDsc;

public class NoDiscordChannelException extends RuntimeException {
    public NoDiscordChannelException(long channelId) {
        super("Discord channel " + channelId + " not found.");
    }
}
