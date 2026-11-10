package funn.j2k.web.config;

public final class ConfigProvider {
    private ConfigProvider() {}

    public static String get(String key) {
        String v = System.getenv(key);
        if (v == null || v.isBlank()) v = System.getProperty(key);
        if (v == null || v.isBlank()) {
            throw new IllegalStateException("Missing required config: " + key);
        }
        return v;
    }

    public static String getOrDefault(String key, String def) {
        String v = System.getenv(key);
        if (v == null || v.isBlank()) v = System.getProperty(key);
        return (v == null || v.isBlank()) ? def : v;
    }

    public static long getLong(String key) {
        return Long.parseLong(get(key));
    }
}
