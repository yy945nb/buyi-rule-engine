package com.ymware.engine.controller;

import cn.hutool.core.util.TypeUtil;
import com.ymware.engine.common.vo.BaseResult;
import com.ymware.engine.common.vo.PageRequest;
import com.ymware.engine.common.vo.PageResult;
import com.ymware.engine.common.vo.PlainResult;
import com.ymware.engine.annotation.Auth;
import com.ymware.engine.service.WorkspaceMemberService;
import com.ymware.engine.vo.workspace.member.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

/**
 * 〈WorkspaceMemberController〉
 *
 * @author 丁乾文
 * @date 2021/6/23 10:01 上午
 * @since 1.0.0
 */
@Tag(name = "工作空间成员控制器")
@RestController
@RequestMapping("workspaceMember")
public class WorkspaceMemberController {

    @Resource
    private WorkspaceMemberService workspaceMemberService;

    /**
     * 工作空间下的成员
     *
     * @param pageRequest p
     * @return r
     */
    @PostMapping("list")
    @Operation(summary = "工作空间下的成员")
    public PageResult<WorkspaceMember> list(@RequestBody @Valid PageRequest<ListWorkspaceMemberRequest> pageRequest) {
        return this.workspaceMemberService.list(pageRequest);
    }

    /**
     * 绑定成员
     *
     * @param bindMemberRequest b
     * @return r
     */
    @PostMapping("bindMember")
    @Operation(summary = "绑定成员")
    public BaseResult bindMember(@RequestBody @Valid BindMemberRequest bindMemberRequest) {
        PlainResult<Boolean> plainResult = new PlainResult<>();
        plainResult.setData(workspaceMemberService.bindMember(bindMemberRequest));
        return plainResult;
    }

    /**
     * 可选工作空间列表人员
     *
     * @param pageRequest p
     * @return r
     */
    @PostMapping("optionalPersonnel")
    @Operation(summary = "可选工作空间列表人员")
    public PageResult<WorkspaceMember> optionalPersonnel(@RequestBody @Valid PageRequest<OptionalPersonnelRequest> pageRequest) {
        return this.workspaceMemberService.optionalPersonnel(pageRequest);
    }

    /**
     * 删除成员
     *
     * @param deleteMemberRequest d
     * @return r
     */
    @Auth(accessibleRole = Auth.Role.WORKSPACE_ADMINISTRATOR)
    @PostMapping("deleteMember")
    @Operation(summary = "删除成员")
    public BaseResult deleteMember(@RequestBody @Valid DeleteMemberRequest deleteMemberRequest) {
        PlainResult<Boolean> plainResult = new PlainResult<>();
        plainResult.setData(workspaceMemberService.deleteMember(deleteMemberRequest));
        return plainResult;
    }


    /**
     * 转移权限
     *
     * @param permissionTransferRequest p
     * @return r
     */
    @Auth
    @PostMapping("permissionTransfer")
    @Operation(summary = "转移权限")
    public BaseResult permissionTransfer(@RequestBody @Valid PermissionTransferRequest permissionTransferRequest) {
        PlainResult<Boolean> plainResult = new PlainResult<>();
        plainResult.setData(workspaceMemberService.permissionTransfer(permissionTransferRequest));
        return plainResult;
    }

    public static void main(String[] args) throws JsonProcessingException, ClassNotFoundException, NoSuchMethodException {
        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println(objectMapper.writeValueAsString("12"));
        System.out.println(Integer.class);
        System.out.println(objectMapper.readValue("12", getPrimitiveClass("int")));
        Method test = WorkspaceMemberController.class.getDeclaredMethod("test", int.class);
        for (Parameter parameter : test.getParameters()) {
            System.out.println(parameter.getType());
        }
        Type paramType = TypeUtil.getParamType(test, 0);
        System.out.println(int.class);
        System.out.println(paramType);
        System.out.println(getPrimitiveClass("int"));
    }

    public void test(int value) {

    }

    private static Class<?> getPrimitiveClass(String name) {
        switch (name) {
            case "int":
                return int.class;
            case "boolean":
                return boolean.class;
            case "char":
                return char.class;
            case "byte":
                return byte.class;
            case "short":
                return short.class;
            case "long":
                return long.class;
            case "float":
                return float.class;
            case "double":
                return double.class;
            default:
                try {
                    return Class.forName(name);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
        }
    }

}
