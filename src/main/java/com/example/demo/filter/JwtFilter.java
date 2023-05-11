package com.example.demo.filter;

import com.example.demo.entity.SysUser;
import com.example.demo.utils.JwtUtil;
import com.example.demo.vo.HttpResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * @USER: YangBaoLiang
 * 校验用户的请求中是否含有有效的jwt
 */
@Component
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    /**
     * 可以进行序列化(将对象..转化为json格式)以及反序列化（将json转化为对象...）
     */
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private JwtUtil jwtUtil;
    private static final String LOGIN_URL="/login";
    private static final String LOGIN_URLS="/login/toLogin";
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 所有的请求都会走JwtFilter中的doFilterInternal(被拦截到了doFilterInternal中)
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // TODO 判断url路径是否是前端用户认证需要表单提交action请求的路径(/login/doLogin)，需要在用户密码过滤器之后
        /**
         * http://localhost:8080/login/doLogin
         * getRequestURL()是获取完整的请求路径        http://localhost:8080/login/doLogin
         * getRequestURI()是获取请求路径的端口号后的那一截  /login/doLogin
         */
        log.info("进来的url:{}",request.getRequestURI());
        String requestURI = request.getRequestURI();
        //用户访问login要求登录
        boolean equals = requestURI.equals(LOGIN_URL);
        boolean equals1 = "/login/doLogin".equals(request.getRequestURI());
        if ((equals||equals1||LOGIN_URLS.equals(request.getRequestURI()))&&!request.getRequestURI().equals(LOGIN_URL)) {
            /**
             * 如果是登录的请求那么就直接对它放行,因为除登录外的其他请求我们都需要进行拦截(没考虑有图片验证码这些请求路径)
             */
            filterChain.doFilter(request, response);
            return;
        }
        //如果我们用户压根没有通过认证过(也就是没有登录成功过),那么就不对它放行,而是让他去登录
        String strAuth = request.getHeader("Authorization");
        /**
         * 校验jwt（看看用户请求中携带的jwt是否符合我们的要求,如果不符合那就不对其放行）
         */
        if (StringUtils.isEmpty(strAuth)) {
            if(request.getRequestURI().equals(LOGIN_URL)){
                /**
                 * 如果是登录的请求那么就直接对它放行,因为除登录外的其他请求我们都需要进行拦截(没考虑有图片验证码这些请求路径)
                 */
                filterChain.doFilter(request, response);
            }
            HttpResult httpResult = HttpResult.builder()
                    .code(0)
                    .msg("jwt为空,啦啦啦啦")
                    .build();
            printToken(request, response, httpResult);
            return;
        }
        /**
         * 操作一下拿到我们点击退出登录的对应用户的token
         */
        strAuth = strAuth. replace( "bearer ", "") ;
//校验不为null的jwt到底合不合法(也就是能不能通过我们服务端的校验)
        boolean b = jwtUtil.verifyToken(strAuth);
        if (!b){
            HttpResult httpResult = HttpResult.builder()
                            .code(1)
                    .msg("jwt未通过校验")
                    .build();
            printToken(request, response, httpResult);
            return;
        }
        //判断redis是否存在发送过来的请求中携带的jwt
        String redisToken = stringRedisTemplate.opsForValue().get ("logintoken" + strAuth) ;
        if (StringUtils. isEmpty (redisToken)) {
            HttpResult httpResult = HttpResult.builder()
                    .code(0)
                    .msg("您已经退出，请重新登录! ! !↓")
                    .build();
            printToken(request, response, httpResult);
            return;
        }
            //从jwt里获取用户信息和权限信息
        List<String> userAuth = jwtUtil.getUserAuth(strAuth);
        String userInfo = jwtUtil.getUserInfo(strAuth);
//反序列化成sysUser对象
        SysUser sysUser = objectMapper. readValue(userInfo, SysUser.class);
        List<SimpleGrantedAuthority> authorityList=userAuth. stream() .map (SimpleGrantedAuthority::new) .collect (Collectors.toList());
        /**
         * 拿到我们 用户名密码认证对应的token
         * 第一个参数为用户基本信息
         * 第二个参数为凭证(可以不传)
         * 第三个参数是对应用户所拥有的权限
         */
        UsernamePasswordAuthenticationToken ionToken=new  UsernamePasswordAuthenticationToken(sysUser,null,authorityList);
        log.info("ionToken是？：{}",ionToken);
/**
 *  以前是框架在我们用户认证成功后会帮我们用户创建一个用户密码token再将其放(set)到我们SpringSecurity中的安全上下文中（这种是基于session的方式）
 *  （而我们现在想要使用token的方式）而现在我们使用了jwt,则需要将从token中拿到的用户信息去手动生成一个用户密码token放(set)到我们的上下文中去(不使用SpringSecurity为我们创建的用户密码token，也就是干了这一步相当于不使用session的方式了，也就是基于token)
 */
        SecurityContextHolder.getContext().setAuthentication(ionToken);
        /**
         * 干完这一切就放行
         */
        doFilter(request,response,filterChain);
    }
        private void printToken(HttpServletRequest request, HttpServletResponse response,HttpResult httpResult) throws
        JsonProcessingException {
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
