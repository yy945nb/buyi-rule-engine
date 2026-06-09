package com.ymware.engine.permission;

/**
 * 租户插件异常类
 */
public class PermissionPluginException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PermissionPluginException(String message) {
        super(message);
    }

    public PermissionPluginException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public static PermissionPluginException tenantPluginException(String msg, Throwable t, Object... params) {
        return new PermissionPluginException(String.format(msg, params), t);
    }

    public static PermissionPluginException tenantPluginException(String msg, Object... params) {
        return new PermissionPluginException(String.format(msg, params));
    }

}
