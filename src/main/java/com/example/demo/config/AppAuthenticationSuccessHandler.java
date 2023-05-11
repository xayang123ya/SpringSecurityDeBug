package com.example.demo.config;

import com.example.demo.entity.SysUser;
import com.example.demo.utils.JwtUtil;
import com.example.demo.vo.HttpResult;
import com.example.demo.vo.SecurityUser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * @USER: YangBaoLiang
 * 用户登录成功后,我们可以通过此AppAuthenticationSuccessHandler实现类来进行后续的处理
 * 实现这个接口就表示我们不使用SpringSecurity为我们提供的默认的认证成功处理器
 * 用户认证成功（在登录页将密码输对了）后就会调用我们AppAuthenticationSuccessHandler实现类中的onAuthenticationSuccess方法（如果不实现并重写此方法的话,那么默认我们的SpringSecurity就会在认证成功后为我重定向到对应的接口）
 */
@Component
@Slf4j
public class AppAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private JwtUtil jwtUtil;
    /**
     * 可以进行序列化(将对象..转化为json格式)以及反序列化（将json转化为对象...）
     */
    @Resource
    private ObjectMapper objectMapper;
    /**
     * 认证成功后......
     * @param request
     * @param response
     * @param authentication
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        /**
         * 用户认证成功后该用户的信息会存储在我们安全上下文中；
         *          a用户认证成功后进入到我们的认证成功处理器中，我们就可以在认证成功处理器的方法内获取a用户的信息，
         *          b用户认证成功后进入到我们的认证成功处理器中，我们就可以在认证成功处理器的方法内获取a用户的信息。
         *              也就是：用户与用户之间是隔离的，前者在前者的处理器方法中可以获取前者自己的用户信息，后者在后者的处理器方法中可以获取后者自己的用户信息
         */
        Object principal = authentication.getPrincipal();
        /**
         * 因为我们存储在安全上下文中的用户信息其实(我们认证成功的用户)就是SecurityUser类型，因为我们返回的认证成功的对象的类型就是我们的SecurityUser
         */
        SecurityUser securityUser= (SecurityUser) principal;
        //获取用户信息但是不包含用户所拥有的权限
        SysUser sysUser = securityUser.getSysUser();
        //对我们用户信息作json格式转换
        log.info("获取了用户信息：{}",sysUser);
        String userInfo = objectMapper.writeValueAsString(sysUser);
        log.info("序列化后的用户信息：{}",userInfo);
        //只是单纯获取用户所拥有的权限
        List<SimpleGrantedAuthority> authorities=(List<SimpleGrantedAuthority>)securityUser.getAuthorities();
            //将类型为List<SimpleGrantedAuthority>的authorities转化为List<String>的authorities
        List<String> authoritieCollect = authorities.stream().map(SimpleGrantedAuthority::getAuthority).collect(Collectors.toList());
        //创建/生成jwt，然后再将创建/生成的jwt响应到我们的浏览器页面上
        String jwt = jwtUtil.Secret(userInfo, authoritieCollect);
        //再将创建的jwt放到我们的redis中去
        /**
         * 第一个参数为键
         * 第二参数为值
         * 第三个参数为设置的键的存活时间(2)
         * 第四个参数则是设置存活时间的单位(2小时)
         */
        stringRedisTemplate.opsForValue().set("logintoken"+jwt,objectMapper.writeValueAsString(authentication),2, TimeUnit.HOURS);
        //先将创建好了的jwt封装一下
        printToken(request,response,HttpResult.builder().code(200).msg("jwt创建成功").data(jwt).build());
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
