package com.rt.forum.controller.Interceptor;

import com.rt.forum.entity.LoginTicket;
import com.rt.forum.entity.User;
import com.rt.forum.service.UserService;
import com.rt.forum.util.CookieUtil;
import com.rt.forum.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;
    //一开始就用cookie中的ticket找用户
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //从request获得cookie，我们封装起来CookieUtil
        String ticket = CookieUtil.getValue(request,"ticket");
        //登入了，就调用service查询
        if(ticket != null){
            //查询凭证
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            //判断凭证是否失效
            if(loginTicket != null && loginTicket.getStatus()==0 && loginTicket.getExpired().after(new Date())){
                //根据凭证查询用户
                User user = userService.findUserById(loginTicket.getUserId());
                //在本次请求持有用户，服务器处理请求是多线程，存用户并发可能冲突，考虑线程隔离，ThreadLocal。
                //把数据存档当前线程里
                hostHolder.setUsers(user);
            }
        }
        return true;
    }
    //模板引擎使用前要用，post是在模板使用之前调用的，而且它有modelandview

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if(user!=null && modelAndView != null){
            modelAndView.addObject("loginUser",user);
        }
        //之后模板执行
    }
    //模板执行完，清除掉
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}


