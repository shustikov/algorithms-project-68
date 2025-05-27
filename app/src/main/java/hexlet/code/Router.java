package hexlet.code;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Router {
    public static Map<String, Object> serve(List<Map<String, Object>> routes, String request) {
        var root = buildPrefixTree(routes);
        return getHandler(root, request);
    }

    private static Map<String, Object> getHandler(PrefixTreeNode root, String request) {
        var requestArray = request.split("/");
        var node = root;
        for (var requestItem : requestArray) {
            if (node.children.size() == 1) {
                var childKey = node.children.keySet().iterator().next();
                if (childKey.startsWith(":")) {
                    node = node.children.get(childKey);
                    continue;
                }
            }
            node = node.children.get(requestItem);
        }
        return node.handler;
    }

    private static PrefixTreeNode buildPrefixTree(List<Map<String, Object>> routes) {
        var root = new PrefixTreeNode("", null);
        for (var routeRule : routes) {
            var node = root;
            var routArray = ((String) routeRule.get("path")).split("/");
            for (var routeItem : routArray) {
                if (routeItem.startsWith(":")) {
                    routeItem = ":param";
                }
                if (!node.children.isEmpty() && node.children.containsKey(routeItem)) {
                    node = node.children.get(routeItem);
                } else {
                    node = node.addNode(routeItem, null);
                }
            }
            node.setHandler((Map<String, Object>) routeRule.get("handler"));
        }
        return root;
    }

    private static class PrefixTreeNode {
        private final String value;
        private HashMap<String, PrefixTreeNode> children;
        private Map<String, Object> handler;

        PrefixTreeNode(String value, Map<String, Object> handler) {
            this.handler = handler;
            this.value = value;
            this.children = new HashMap<>();
        }

        public PrefixTreeNode addNode(String value, Map<String, Object> handler) {
            var node = new PrefixTreeNode(value, handler);
            this.children.put(value, node);
            return node;
        }

        public void setHandler(Map<String, Object> handler) {
            this.handler = handler;
        }
    }
}
