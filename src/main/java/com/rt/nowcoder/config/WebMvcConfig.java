package com.rt.nowcoder.config;

import com.rt.nowcoder.controller.Interceptor.AlphaInterceptor;
import com.rt.nowcoder.controller.Interceptor.LoginRequiredInterceptor;
import com.rt.nowcoder.controller.Interceptor.LoginTicketInterceptor;
import com.rt.nowcoder.controller.Interceptor.MessageInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    //拦截器配置，实现接口
    @Autowired
    private AlphaInterceptor alphaInterceptor;
    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;
    @Autowired
    private LoginRequiredInterceptor loginRequiredInterceptor;

    @Autowired
    private MessageInterceptor messageInterceptor;
    //在某个方法实现接口
    //利用传进来的参数添加
    //静态资源不拦截
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(alphaInterceptor)
                .excludePathPatterns("/**/*.css","/**/.js","/**/*.png","/**/*.jpg","/**/*.jpeg")
                .addPathPatterns("/register","/login");
    //LoginTicketInterceptor
        registry.addInterceptor(loginTicketInterceptor)
                .excludePathPatterns("/**/*.css","/**/.js","/**/*.png","/**/*.jpg","/**/*.jpeg");
        //LoginRequired 不处理静态资源
        registry.addInterceptor(loginRequiredInterceptor)
                .excludePathPatterns("/**/*.css","/**/.js","/**/*.png","/**/*.jpg","/**/*.jpeg");
        //message
        registry.addInterceptor(messageInterceptor)
                .excludePathPatterns("/**/*.css","/**/.js","/**/*.png","/**/*.jpg","/**/*.jpeg");
    }
}
