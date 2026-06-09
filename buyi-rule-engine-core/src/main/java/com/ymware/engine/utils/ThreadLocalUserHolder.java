package com.ymware.engine.utils;

/**
 * ThreadLocal holder for user information
 */
public class ThreadLocalUserHolder {

    private static final ThreadLocal<UserInfo> USER_HOLDER = new ThreadLocal<>();

    public static void setUser(UserInfo user) {
        USER_HOLDER.set(user);
    }

    public static UserInfo getUser() {
        return USER_HOLDER.get();
    }

    public static void clear() {
        USER_HOLDER.remove();
    }

    /**
     * User info interface for permission checking
     */
    public interface UserInfo {
        Boolean getIsAdmin();
        Boolean getIsRoot();
    }
}
