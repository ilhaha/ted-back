package top.continew.admin.common.config.mybatis;

import cn.hutool.core.convert.Convert;
import top.continew.admin.common.context.UserContextHolder;
import top.continew.starter.extension.datapermission.enums.DataScope;
import top.continew.starter.extension.datapermission.filter.DataPermissionUserContextProvider;
import top.continew.starter.extension.datapermission.model.RoleContext;
import top.continew.starter.extension.datapermission.model.UserContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;

import java.util.stream.Collectors;

public class DefaultDataPermissionUserContextProvider implements DataPermissionUserContextProvider {

    @Override
    public boolean isFilter() {
        // 获取当前请求 URI
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpServletRequest request = attrs.getRequest();
            String uri = request.getRequestURI();
            if (uri.startsWith("/training/trainingCheckin/do")) {
                return false;
            }
        }
        return !UserContextHolder.isAdmin();
    }

    @Override
    public UserContext getUserContext() {
        top.continew.admin.common.context.UserContext context = UserContextHolder.getContext();
        if (context == null) {
            // 匿名接口没有上下文，返回空或抛异常由拦截器决定
            return null;
        }
        UserContext userContext = new UserContext();
        userContext.setUserId(Convert.toStr(context.getId()));
        userContext.setDeptId(Convert.toStr(context.getDeptId()));
        userContext.setRoles(context.getRoles()
                .stream()
                .map(r -> new RoleContext(Convert.toStr(r.getId()), DataScope.valueOf(r.getDataScope().name())))
                .collect(Collectors.toSet()));
        return userContext;
    }
}
