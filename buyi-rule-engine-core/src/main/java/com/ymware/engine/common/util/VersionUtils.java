package com.ymware.engine.common.util;

public class VersionUtils {
    private static final String VERSION = "4.0.0";
    public static final String INIT_VERSION = "1.0.0";

    public static String getVersion() {
        return VERSION;
    }

    public static String getNextVersion(String currentVersion) {
        if (currentVersion == null || currentVersion.trim().isEmpty()) {
            return INIT_VERSION;
        }
        try {
            String[] parts = currentVersion.split("\\.");
            if (parts.length > 0) {
                int lastIdx = parts.length - 1;
                int lastNum = Integer.parseInt(parts[lastIdx]);
                parts[lastIdx] = String.valueOf(lastNum + 1);
                return String.join(".", parts);
            }
        } catch (NumberFormatException e) {
            // fallback
        }
        return currentVersion + ".1";
    }
}

