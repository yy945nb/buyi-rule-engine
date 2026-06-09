package com.ymware.engine.vo.user;

import lombok.Data;


/**
 * @author dqw
 */
@Data
public class SelectUserResponse {

    private Long id;

    private String username;

    private String email;

    private Long phone;

    private String avatar;

    private String sex;


}
