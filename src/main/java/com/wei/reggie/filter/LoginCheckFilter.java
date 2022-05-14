package com.wei.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.wei.reggie.common.BaseContext;
import com.wei.reggie.common.R;
import com.wei.reggie.entity.Employee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@Slf4j
@WebFilter(filterName = "loginCheckFilter",urlPatterns ="/*" )
public class LoginCheckFilter implements Filter {
    public static final AntPathMatcher PATH_MATCHER=new AntPathMatcher();//匹配器
    //定义不需要的请求路径
    String[] urls=new String[]{
            "/employee/login",
            "/employee/logout",
            "/backend/**",
            "/front/**",
            "/common/**",
            "/user/sendMsg",
            "/user/login",
            "/user/logout"

    };

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request=(HttpServletRequest) servletRequest;
        HttpServletResponse response=(HttpServletResponse) servletResponse;
        String requestURI=request.getRequestURI();
        log.info("拦截到请求:{}",requestURI);
        boolean check=check(urls,requestURI);
        if(check){
            log.info("本次请求:{}无需处理",requestURI);
            filterChain.doFilter(request,response);
            return;
        }
        //判断后台管理登录状态
        Long empId = (Long)request.getSession().getAttribute("employee");
        if(empId!=null){
            log.info("用户已登录，用户id为:{}",empId);
            BaseContext.setCurrentId(empId);
            filterChain.doFilter(request,response);
            return;
        }
        //判断顾客登录状态
        Long userId = (Long)request.getSession().getAttribute("user");
        if(userId!=null){
            log.info("用户已登录，用户id为:{}",userId);
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(request,response);
            return;
        }
        
        
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    public boolean check(String[] urls,String requestURI){
        for(String url:urls){
            boolean match = PATH_MATCHER.match(url, requestURI);
            if(match){
                return true;
            }

        }
        return false;
    }
}
