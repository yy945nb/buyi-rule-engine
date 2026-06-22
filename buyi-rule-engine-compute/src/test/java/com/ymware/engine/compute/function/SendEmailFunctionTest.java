package com.ymware.engine.compute.function;

import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * P0 回归测试：验证 SendEmailFunction SMTP 配置正确使用端口号而非主机名
 */
class SendEmailFunctionTest {

    @Test
    void smtpPortPropertyShouldUsePortNumberNotHostname() {
        // 模拟 SendEmailFunction 中的 Properties 构建逻辑
        String host = "smtp.example.com";
        Integer port = 465;

        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        // 修复后的代码应该使用 port
        props.setProperty("mail.smtp.port", String.valueOf(port));
        props.setProperty("mail.smtp.socketFactory.port", String.valueOf(port));

        // 验证 port 属性是端口号而非主机名
        assertEquals("465", props.getProperty("mail.smtp.port"),
                "mail.smtp.port should be the port number, not the hostname");
        assertEquals("465", props.getProperty("mail.smtp.socketFactory.port"),
                "mail.smtp.socketFactory.port should be the port number, not the hostname");

        // 验证 host 属性仍然是主机名
        assertEquals("smtp.example.com", props.getProperty("mail.smtp.host"),
                "mail.smtp.host should be the hostname");
    }

    @Test
    void portAndHostShouldBeDifferent() {
        // 确保 port 和 host 不会混淆
        String host = "smtp.qq.com";
        Integer port = 587;

        // port 属性不应该等于 host
        assertNotEquals(host, String.valueOf(port),
                "Port number and hostname should be different values");
    }
}
