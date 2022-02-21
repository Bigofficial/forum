package com.rt.nowcoder.controller.advice;

import com.rt.nowcoder.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


//只去扫描controller注解了的bean
@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);
    //处理错误情况方法
    //处理那些异常呢
    @ExceptionHandler({Exception.class})
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        //记日志
        logger.error("服务器发送异常" + e.getMessage());
        //异常详细信息
        for(StackTraceElement element :e.getStackTrace()){
            logger.error(element.toString());
        }
        //异步请求返回json重定向没用
        //判断请求是同步还是异步
        //查看请求方式
        String xRequestWith = request.getHeader("x-requested-with");
        //XML是异步请求
        if("XMLHttpRequest".equals(xRequestWith)){
            //这是异步请求
            //返回普通字符串，可以是json格式
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(CommunityUtil.getJSONString(1,"服务器异常"));
        }else {
            response.sendRedirect(request.getContextPath() + "/error");
        }

    }
}
