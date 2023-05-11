package com.example.demo.config;

import com.example.demo.utils.JwtUtil;
import com.example.demo.vo.HttpResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Administrator
 * @USER: YangBaoLiang
 * 用户退出登录成功后,我们可以通过此AppLogOutSuccessHandler实现类来进行后续的处理
 * 实现这个接口就表示我们不使用SpringSecurity为我们提供的默认的退出登录处理器
 * 用户点击退出登录后（在退出登录页点击了退出登录）后就会调用我们LogoutSuccessHandler实现类中的AppLogOutSuccessHandler方法（如果不实现并重写此方法的话,那么默认我们的SpringSecurity就会在点击退出登录后为我重定向到登录页）
 */
@Component
public class AppLogOutSuccessHandler implements LogoutSuccessHandler {
    @Resource
    private JwtUtil jwtUtil;
    /**
     * 可以进行序列化(json格式)以及反序列化
     */
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
     /**
     * 退出登录后......
     * @param request
     * @param response
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        /**
         * 获取到用户的请求头中的token(不完整的)
         */
        String jwtToken = request. getHeader("Authorization") ;
        /**
         * 判断我们的用户是否是未登录过就来作退出登录操作
         */
        if (StringUtils.isEmpty(jwtToken)) {
            HttpResult httpResult = HttpResult.builder()
                    .code(0)
                    .msg("jwt为空")
                    .build();
            printToken(request, response, httpResult);
            return;
        }
        /**
         * 操作一下拿到我们点击退出登录的对应用户的token
         */
        jwtToken = jwtToken. replace( "bearer ", "") ;
        /**
         * 校验一下其token合不合法(可能是过期了)
         */
        boolean result = jwtUtil.verifyToken(jwtToken);
        if (!result){
            HttpResult httpResult = HttpResult.builder()
                    .code(1)
                    .msg("jwt未通过校验")
                    .build();
            printToken(request, response, httpResult);
            return;
        }
        //将对应用户的jwt从我们的redis中进行删除(这样的话他就无法再通过该token访问我们的程序了)
        stringRedisTemplate.delete("logintoken"+jwtToken);
        /**
         * 设置我们用户退出登录后我们的后端要返回的数据
         */
        HttpResult httpResult=HttpResult. builder()
                . code (100)
                .msg ("退出登录成功啦")
                .build() ;
        printToken(request, response, httpResult);
    }
    private void printToken(HttpServletRequest request, HttpServletResponse response,HttpResult httpResult) throws JsonProcessingException {
        /**
         * 设置要响应到页面上的字符的字符编码
         */
        response . setCharacterEncoding ("UTF-8") ;
        /**
         * 字符格式为json格式
         */
        response . setContentType ("application/json;charset=utf-8") ;
        /**
         * 将数据响应到页面上
         */
        PrintWriter writer = null;
        try {
            writer = response .getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //我们需要统一响应对象,所以需要将我们创建的jwt封装到我们自定义的响应对象中去,并且还要返回响应码，然后将其显示在我们的浏览器页面中
        //将响应对象转为json格式
        String s = objectMapper.writeValueAsString(httpResult);
        writer.println (s) ;
        writer.flush() ;
    }
}
