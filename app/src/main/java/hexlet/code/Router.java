package hexlet.code;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class Router {
    public static Map<String, String> serve(List<Map<String, Object>> routes, String request) {
        return routes.stream()
                .filter(item -> match((String) item.get("path"), request))
                .map(item -> item.get("handler"))
                .map(handler -> (Map<String, String>) handler)
                .findFirst().orElseThrow();
    }

    private static boolean match(String route, String request) {

        var routArray = route.split("/");
        var requestArray = request.split("/");
        if (routArray.length == requestArray.length) {
            return IntStream.range(0, requestArray.length)
                    .mapToObj(index -> (routArray[index].startsWith(":"))
                            || routArray[index].equals(requestArray[index]))
                    .allMatch(item -> item);
        } else {
            return false;
        }
    }
}
