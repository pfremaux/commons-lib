package commons.lib.server.socket.secured;

import commons.lib.server.socket.AsyncEvent;

import javax.crypto.spec.SecretKeySpec;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class ContactRegistry {

    public static final  Map<String, SecretKeySpec> SYMMETRIC_KEYS = new HashMap<>();
    public static final Map<String, List<PrivateKey>> PRIVATE_KEYS = new HashMap<>();
    // TODO method that combines public key and trusted
    public static final Map<String, List<PublicKey>> PUBLIC_KEYS = new HashMap<>();
    public static final Set<String> TRUSTED = new HashSet<>();
    public static final Map<AsyncEvent, CompletableFuture<String>> AWAITED_EVENT = new HashMap<>();
}
