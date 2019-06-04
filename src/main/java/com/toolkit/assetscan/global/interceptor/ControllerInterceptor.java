package com.toolkit.assetscan.global.interceptor;


import com.toolkit.assetscan.global.params.Const;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.SystemPropertyUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class ControllerInterceptor implements HandlerInterceptor {
    private org.slf4j.Logger logger = LoggerFactory.getLogger(ControllerInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            // 获取类名
            String className = ((HandlerMethod) handler).getBean().getClass().getName();
            // 获取方法名
            String methodName = ((HandlerMethod) handler).getMethod().getName();
            // 会话id和用户名
            String sessionId = request.getSession().getId();
            String userName = (String)request.getSession().getAttribute(Const.USER);
            //
//            logger.info("---PreHandle---:" + "\tClass: " + className + "\tMethod: " + methodName +
//                    "\tsessionId: " + sessionId + "\tuserName: " + userName);
            logger.info("---PreHandle---:" + "\tMethod: " + methodName +
                    "\tsessionId: " + sessionId + "\tuserName: " + userName);

            String exceptionName = ((HandlerMethod) handler).getMethod().getExceptionTypes().getClass().getName();
            logger.info(exceptionName);
        } else {

        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object obj, ModelAndView modelAndView) throws Exception {
        if (obj instanceof HandlerMethod) {
            // 获取类名
            String className = ((HandlerMethod) obj).getBean().getClass().getName();
            // 获取方法名
            String methodName = ((HandlerMethod) obj).getMethod().getName();
//            logger.info("---PostHandle---:" + "\tClass: " + className + "\tMethod: " + methodName);
            logger.info("---PostHandle---:" + "\tMethod: " + methodName);
        } else {

        }

    }

}
