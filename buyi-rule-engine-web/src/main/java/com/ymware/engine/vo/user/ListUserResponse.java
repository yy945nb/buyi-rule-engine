package com.ymware.engine.vo.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 〈ListUserRequest〉
 *
 * @author 丁乾文
 * @date 2021/6/23 3:16 下午
 * @since 1.0.0
 */
@Data
public class ListUserResponse {

    private Long id;

    private String username;

    private String email;

    private String sex;

    private String avatar;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

}
