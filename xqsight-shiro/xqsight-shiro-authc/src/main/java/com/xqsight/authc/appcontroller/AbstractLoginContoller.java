package com.xqsight.authc.appcontroller;

import com.xqsight.common.model.BaseResult;
import com.xqsight.common.model.constants.Constants;
import com.xqsight.common.web.WebUtils;
import com.xqsight.sso.authc.SSOUsernamePasswordToken;
import com.xqsight.sso.exceptions.CustomAuthcException;
import com.xqsight.sso.shiro.constants.WebConstants;
import com.xqsight.sso.subject.SSOSubject;
import com.xqsight.sso.utils.SSOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @version ShiroContoller.java, v 0.1 2015年7月3日 上午9:49:26
 */
public abstract class AbstractLoginContoller {

    protected Logger logger = LogManager.getLogger(getClass());

    private String DEFAULT_SIGNED_LOGINED_URL = "/index.html";

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    protected RedirectView login(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        SSOSubject currentUser = SSOUtils.getCurrentUserSubject();
        logger.info("we take u are: name={}, isAuthc={}, isRememberMe={}, isRunAs={}", currentUser.getPrincipal(), currentUser.isAuthenticated(), currentUser.isRemembered(), currentUser.isRunAs());
        if (!currentUser.isAuthenticated()) {
            try {
                currentUser.login(getUsernamePasswrdToken(request));
            } catch (AuthenticationException e) {
                request.setAttribute(Constants.KEY_STATUS, Constants.FAILURE);
                if (e instanceof UnknownAccountException) {
                    request.setAttribute(Constants.KEY_MESSAGE, "用户名不存在");
                } else if (e instanceof IncorrectCredentialsException) {
                    request.setAttribute(Constants.KEY_MESSAGE, "用户名或密码错误");
                } else if (e instanceof LockedAccountException) {
                    request.setAttribute(Constants.KEY_MESSAGE, "用户已锁定，请联系客服");
                } else if (e instanceof CustomAuthcException) {
                    request.setAttribute(Constants.KEY_MESSAGE, e.getMessage());
                } else {
                    logger.error("登陆发生异常", e);
                    request.setAttribute(Constants.KEY_MESSAGE, "系统繁忙，请稍后再试");
                }
            }
        }
        return getRedirectView(request);
    }

    @RequestMapping("/ajaxlogin")
    protected Object ajaxlogin(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        SSOSubject currentUser = SSOUtils.getCurrentUserSubject();
        logger.info("we take u are: name={}, isAuthc={}, isRememberMe={}, isRunAs={}", currentUser.getPrincipal(), currentUser.isAuthenticated(), currentUser.isRemembered(), currentUser.isRunAs());
        Map<String, Object> map = new HashMap<String, Object>();
        if (!currentUser.isAuthenticated()) {
            try {
                currentUser.login(getUsernamePasswrdToken(request));
            } catch (AuthenticationException e) {
                map.put(Constants.KEY_STATUS, Constants.FAILURE);
                if (e instanceof UnknownAccountException) {
                    map.put(Constants.KEY_MESSAGE, "用户名不存在");
                } else if (e instanceof IncorrectCredentialsException) {
                    map.put(Constants.KEY_MESSAGE, "用户名或密码错误");
                } else if (e instanceof LockedAccountException) {
                    map.put(Constants.KEY_MESSAGE, "用户已锁定，请联系客服");
                } else if (e instanceof CustomAuthcException) {
                    map.put(Constants.KEY_MESSAGE, e.getMessage());
                } else {
                    logger.error("登陆发生异常", e);
                    map.put(Constants.KEY_MESSAGE, "系统繁忙，请稍后再试");
                }
                return new BaseResult(map);
            }
        }
        map.put(Constants.KEY_STATUS, Constants.SUCCESS);
        return new BaseResult(map);
    }

    protected SSOUsernamePasswordToken getUsernamePasswrdToken(HttpServletRequest request) {
        String username = request.getParameter(WebConstants.LOGIN_ID);
        String password = request.getParameter(WebConstants.PASSWORD);
        String rememberMe = request.getParameter(WebConstants.REMEMBER_ME);
        logger.info("get userInfo, username={}, password={}, rememberMe={}", username, password, rememberMe);
        return chooseTokenInstance(username, password, org.apache.shiro.web.util.WebUtils.isTrue(request, WebConstants.REMEMBER_ME));
    }

    /**
     * 生成对应的Token
     *
     * @param userName
     * @param password
     * @param isRememberMe
     * @return
     */
    protected abstract SSOUsernamePasswordToken chooseTokenInstance(String userName, String password, boolean isRememberMe);

    protected RedirectView getRedirectView(HttpServletRequest request) throws UnsupportedEncodingException {
        String gotoUrl = URLDecoder.decode(request.getParameter(WebConstants.GO_TO), "UTF-8");
        return new RedirectView(getRedirectUrl(gotoUrl), StringUtils.hasLength(gotoUrl), true);
    }

    private String getRedirectUrl(String gotoUrl) throws UnsupportedEncodingException {
        return StringUtils.hasLength(gotoUrl) ? gotoUrl : getDefaultGotoUrl();
    }

    protected String getDefaultGotoUrl() {
        return DEFAULT_SIGNED_LOGINED_URL;
    }

}
