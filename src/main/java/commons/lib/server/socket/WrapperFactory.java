package commons.lib.server.socket;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * This factory must contain all the responses you want to manage with the Client/Server provided by this lib.
 *
 * @see Server
 * @see Client
 * @see Wrapper
 */
public class WrapperFactory {

    private final Map<Integer, Function<List<byte[]>, Wrapper>> functionMap;

    public WrapperFactory(Map<Integer, Function<List<byte[]>, Wrapper>> functionMap) {
        this.functionMap = functionMap;
    }

    public Map<Integer, Function<List<byte[]>, Wrapper>> getFunctionMap() {
        return functionMap;
    }

}
