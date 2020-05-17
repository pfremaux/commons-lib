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
    private final Map<Integer, Function<List<String>, Wrapper>> functionMap;

    // TODO replace "<String" (action code) by an Integer

    /**
     * @param functionMap A map of functions per action code.
     *                    These functions expects a list of data.
     *                    Depending on this data the function will return a wrapper.
     *                    The wrapper must contain a response.
     */
    public WrapperFactory(Map<Integer, Function<List<String>, Wrapper>> functionMap) {
        this.functionMap = functionMap;
    }

    public Map<Integer, Function<List<String>, Wrapper>> getFunctionMap() {
        return functionMap;
    }
}
