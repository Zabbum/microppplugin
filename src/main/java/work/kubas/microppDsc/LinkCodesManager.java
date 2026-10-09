package work.kubas.microppDsc;

import java.security.SecureRandom;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class LinkCodesManager {
    private final Map<String, UUID> pendingCodes = new ConcurrentHashMap<>();
    private static final SecureRandom random = new SecureRandom();

    public String generateCode(UUID mcUuid) {
        String code = String.format("%06d", random.nextInt(1000000));
        pendingCodes.put(code, mcUuid);
        return code;
    }

    public UUID match(String code) {
        return pendingCodes.remove(code);
    }
}
