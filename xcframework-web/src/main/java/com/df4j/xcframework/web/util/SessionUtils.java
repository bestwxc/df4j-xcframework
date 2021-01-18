package com.df4j.xcframework.web.util;

import com.df4j.xcframework.base.exception.XcException;
import com.df4j.xcframework.base.util.JsonUtils;
import com.df4j.xcframework.web.pojo.LoginUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.springframework.web.context.request.RequestContextHolder.getRequestAttributes;


/**
 * 会话工具
 */
public class SessionUtils {

    private static Logger logger = LoggerFactory.getLogger(SessionUtils.class);

    public static String LOGIN_USER_SESSION_KEY = "LOGIN_USER@SESSION";


    /**
     * 将当前登陆的用户信息设置当线程变量里面
     * AOP切面使用
     *
     * @return
     */
    public static LoginUser getLoginUser() {
        try {
            HttpServletRequest request = getRequest();
            // 获取会话
            HttpSession session = request.getSession(false);
            if (!ObjectUtils.isEmpty(session)) {
                // 获取存放的session
                Object obj = session.getAttribute(LOGIN_USER_SESSION_KEY);
                if (!ObjectUtils.isEmpty(obj) && obj instanceof String) {
                    // 转化成对象
                    LoginUser loginUser = JsonUtils.parse((String) obj, LoginUser.class);
                    // 只有这里是设置了用户的，需要返回
                    return loginUser;
                }
            }
        } catch (Exception e) {
            logger.error("从会话获取当前用户失败", e);
        }
        return null;
    }

    /**
     * 将当前用户放入session 登陆逻辑使用
     *
     * @param loginUser
     */
    public static void setLoginUser(LoginUser loginUser) {
        try {
            HttpServletRequest request = getRequest();
            HttpSession session = request.getSession(true);
            session.setAttribute(LOGIN_USER_SESSION_KEY, JsonUtils.stringify(loginUser));
        } catch (Exception e) {
            throw new XcException(e);
        }
    }

    /**
     * 移除当前会话的登陆用户信息
     * 登陆逻辑使用
     */
    public static void removeLoginUser() {
        try {
            HttpServletRequest request = getRequest();
            // 获取会话
            HttpSession session = request.getSession(false);
            if (!ObjectUtils.isEmpty(session)) {
                // 移除线程变量
                // 移除会话中的当前用户属性
                session.removeAttribute(LOGIN_USER_SESSION_KEY);
                // 使会话无效
                session.invalidate();
            } else {
                // do nothing
            }
        } catch (Exception e) {
            logger.warn("从会话中移除当前登陆用户的信息出错", e);
        }
    }

    /**
     * 获取当前正在处理的请求对象
     *
     * @return
     */
    public static HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = getRequestAttributes();
        if (!ObjectUtils.isEmpty(requestAttributes)) {
            if (requestAttributes instanceof ServletRequestAttributes) {
                return ((ServletRequestAttributes) requestAttributes).getRequest();
            } else {
                logger.warn("当前requestAttributes不是ServletRequestAttributes类型，不能获取");
            }
        }
        return null;
    }

    /**
     * 获取当前正在处理的响应对象
     *
     * @return
     */
    public static HttpServletResponse getResponse() {
        RequestAttributes requestAttributes = getRequestAttributes();
        if (!ObjectUtils.isEmpty(requestAttributes)) {
            if (requestAttributes instanceof ServletRequestAttributes) {
                return ((ServletRequestAttributes) requestAttributes).getResponse();
            } else {
                logger.warn("当前requestAttributes不是ServletRequestAttributes类型，不能获取");
            }
        }
        return null;
    }
}
