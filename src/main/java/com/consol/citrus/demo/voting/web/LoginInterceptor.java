package com.consol.citrus.demo.voting.web;

import com.consol.citrus.demo.voting.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * @author Christoph Deppisch
 */
public class LoginInterceptor extends HandlerInterceptorAdapter implements EnvironmentAware {

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(LoginInterceptor.class);

    @Autowired
    private UserService userService;

    private Environment environment;

    /** Location to redirect to in case project configuration is not set */
    private String redirect;

    /** Locations that get excluded from interceptor */
    private String[] excludes;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getRequestURI().startsWith(request.getContextPath() + redirect)
                || request.getRequestURI().startsWith(redirect)
                || isExcluded(request.getRequestURI(), request.getContextPath())) {
            return true;
        }

        String token = getUserToken(request);
        if (StringUtils.hasText(token) && userService.verify(token)) {
            log.info("Verified user OK");
            return true;
        } else {
            log.info("Unauthorized user - redirecting to login page");
            response.sendRedirect(redirect);
            return false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        String token = getUserToken(request);
        if (StringUtils.hasText(token) &&
                modelAndView != null &&
                modelAndView.getModel() != null &&
                !modelAndView.getModel().containsKey("token")) {
            log.info("Setting user token: " + token);
            modelAndView.getModel().put("token", token);

            if (modelAndView.getView() instanceof RedirectView) {
                ((RedirectView) modelAndView.getView()).setPropagateQueryParams(true);
            }
        }

        super.postHandle(request, response, handler, modelAndView);
    }

    /**
     * Gets user token from request header or query parameter.
     * @param request
     * @return
     */
    private String getUserToken(HttpServletRequest request) {
        if (request.getParameter("token") != null) {
            return request.getParameter("token");
        }

        if (request.getHeader("X-Auth-Token") != null) {
            return request.getHeader("X-Auth-Token");
        }

        return "";
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    /**
     * Checks whether request uri matches one of the excluded entries.
     * @param requestURI
     * @param contextPath
     * @return
     */
    private boolean isExcluded(String requestURI, String contextPath) {
        for (String exclude : excludes) {
            if (exclude.endsWith("*")) {
                if (requestURI.startsWith(contextPath + exclude.substring(0, exclude.length() - 1).trim())) {
                    return true;
                }
            } else {
                if (requestURI.equals(contextPath + exclude.trim())) {
                    return true;
                }
            }

        }

        return false;
    }

    /**
     * Gets the value of the redirect property.
     *
     * @return the redirect
     */
    public String getRedirect() {
        return redirect;
    }

    /**
     * Sets the redirect.
     * @param redirect the redirect to set
     */
    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

    /**
     * Gets the excludes.
     * @return the excludes the excludes to get.
     */
    public String[] getExcludes() {
        return Arrays.copyOf(excludes, excludes.length);
    }

    /**
     * Sets the excludes.
     * @param excludes the excludes to set
     */
    public void setExcludes(String[] excludes) {
        this.excludes = Arrays.copyOf(excludes, excludes.length);
    }
}
