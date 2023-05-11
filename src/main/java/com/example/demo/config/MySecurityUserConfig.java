package com.example.demo.config;

import com.example.demo.filter.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;

/**
 * 目的：配置密码编码器
 * @author Administrator
 * @USER: YangBaoLiang
 * */
@Configuration
public class MySecurityUserConfig extends WebSecurityConfigurerAdapter {
    @Resource
    private  AppAuthenticationSuccessHandler appAuthenticationSuccessHandler;
    @Resource
    private  AppLogOutSuccessHandler appLogOutSuccessHandler;
    /**
     * 自定义的过滤器
     */
    @Resource
    private JwtFilter jwtFilter;
    /**
     *自定义用户必须配置密码编码器(可以对用户输入的明文密码进行加密)
     * ----------------对前端进行认证（用户前端登录时）输入的密码作加密
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        /**
         * 1、直接返回的话,我们前端虽然输入的明文会被编码为密文,但是如果我们的密文直接与我们认证需要填入的明文进行比较是肯定不会成功的,所以我们还需要将用户需要输入的正确密码(明文)也进行加密
         */
            return new BCryptPasswordEncoder();
    }
    @Override
    /**
     *1、只要是通过认证的用户,那么该程序中未作权限控制的的所有接口我们的用户都可以访问
     *2、设置了只允许对应权限的用户访问的路径之后,这些路径只能被拥有对应权限的用户访问成功,其他权限的用户访问该页面响应码均会报403
     *3、设置了只允许对应权限的用户访问的路径之后,这些用户仍然可以访问成功其他没有被限制为指定权限的用户才能访问的路径
     *4、若是不写http.formLogin().permitAll()的话,那么用户在没访问的情况下访问我们程序的路径会被提示为访问被拒绝
     *      因为我们将登录页的访问权限也设置死了(所有请求(不管是什么权限的用户)都没办法进入登录页)，所以我们需要
     *          http.formLogin().permitAll()来将登录页放开
     */
    protected void configure (HttpSecurity http) throws Exception {
        /**
         * 将我们自定义的图片校验过滤器放在对我们认证输入的用户名与密码作校验的过滤器的前面(可以先一步去对我们的请求中的信息作校验)
         */
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        /**
         * 表示授权http请求
         */
        http.authorizeHttpRequests()
                /**
                 * 只有在我们通过认证的用户有student:select时,该对应用户才可以访问/login/toIndex,否则就会被我们的SpringSecurity重定向到403页面
                 * "/login/toIndex"前面的"/"是必须的,否则如果写成"login/toIndex"的话，限制的路径也不同
                 * */
                .mvcMatchers("/student/**").hasAuthority("student:select")
                /**
                 * 将生成图片验证码的接口放开，因为我们是在未登录的情况下才需要图片验证码来辅助我们用户登录来着
                 * 不让我们SpringSecurity中的过滤器将该请求拦截
                 */
                .mvcMatchers("/code/getCaptchaCode").permitAll()
                /**
                 * 表示任何请求
                 */
                .anyRequest()
                /**
                 * 发送过来的请求都必须是已经在我们的系统中认证过的,否则就需要先进行登录
                 * 设置了authenticated()和denyAll()无异,都可以将所有请求进行拒绝
                 */
                .authenticated();
        /**
         * 不论有没有拒绝所有请求的访问，我们的登录页肯定是要能访问到的(因为我们用户需要登录啊，这样才能确认它所拥有的权限)，至于登录成功后我们的用户能不能去进行接口访问，再另作操作
         * 允许用户访问登录页
         * loginPage表示我们要自己指定登录页.而不使用SpringSecurity为我们提供的默认登录页，括号内填写接口路径(需要保证访问该接口可以被转发到thymeleaf下的登录页中，否则写着也没必要)
         *配置上我们自定义的认证成功处理器
         * * */
        http.formLogin()
        /**
         * loginPage和logoutSuccessUrl中的接口路径最好一样
         * loginPage中的接口路径与usernameParameter、passwordParameter、loginProcessingUrl都相关
        */
                /**
                 * 设置在我们认证失败、未认证时，我们的SpringSecurity就会为我们重定向到/login/toLogin接口，若是不设置则默认都是访问SpringSecurity为我们提供的login接口
                 *  loginPage中设置的要访问的是我们访问login后SpringSecurity为我们重定向到的登录页面(这是替代了我们SpringSecurity为我们提供的自定义的登录页面,
                 * */
                .loginPage("/login/toLogin")
                /**
                 * （用户名）SpringSecurity在我们的登录页提交后默认会获取访问/login/toString后转发到的登录页中的表单元素中name的值为username的input框中内容，所以我们需要
                 *      让其来获取在我们的登录页提交后默认会获取表单元素中name的值为uname的input框中内容
                 * */
                .usernameParameter("uname")
                /**
                 * （密码）SpringSecurity在我们的登录页提交后默认会获取访问/login/toLogin后转发到的登录页中的表单元素中name的值为password的input框中内容，所以我们需要
                 *      让其来获取在我们的登录页提交后默认会获取表单元素中name的值为uname的input框中内容
                 **/
                .passwordParameter("pwd")
                /**
                 * （点击表单提交后会访问的接口）SpringSecurity在我们的登录页（访问/login/toLogin后转发到的登录页）提交后我们需要重定向到/login/doLogin中
                 *  /login/doLogin是我们的SpringSecurity为我们提供的,他负责对我们传入进的表单数据作校验,所以不用管它
                 *  /login/doLogin接口是无法被直接访问的，因为普通的请求都被SpringSecurity内部的拦截器拦截了
                 * */
                .loginProcessingUrl("/login/doLogin")
                /**
                 * （点击表单提交后并且认证失败后）SpringSecurity在我们的登录页提交并且认证失败后默认会为我们重定向到默认的路径中(也就是我们已经指定了的登录接口（未指定的话就会访问我们的login接口---SpringSecurity为我们提供的）)
                 *      而我们却想让在我们的登录页提交并且认证失败后访问/login/toLogin接口，也就是会跳到我们的登录页
                 **/
                .failureForwardUrl("/login/toLogin")
                /**
                 * （点击表单提交后并且认证成功后）SpringSecurity在我们的登录页提交并且认证成功后默认会为我们重定向到默认的路径中(也就是"",啥也没写)
                 *      而我们却想让在我们的登录页提交并且认证成功后访问/login/toIndex接口
                 **/
//                .successForwardUrl("/login/toIndex")
                /**
                 * 在用户点击表单提交后，我们的SpringSecurity将前端输入的用户名和密码（只是拿到而已，这一步密码/账号是啥玩意都没关系）都拿到之后方才会去对账户名和密码进行校验
                 */
                .permitAll();
        /**
         * 配置退出成功处理器
         * logoutSuccessUrl配置我们退出成功后会重定向到的接口，一般也是去访问我们的登录页：loginPage指定的/login/toLogin
         */
        http.logout().logoutSuccessUrl("/login/toLogin");
        /**
         * 开启跨域请求保护，可以有效的防止黑客进行攻击（开启的话需要我们的前端传token进来，所以我们此处选择将跨域请求保护关闭）
         * 发现前端请求怎么都访问不到后端某个接口，则很有可能是我们的跨域请求保护没有关掉
         * */
        http.csrf().disable();
        http.formLogin().successHandler(appAuthenticationSuccessHandler).permitAll();
        /**
         * 禁用/不使用/创建session
         */
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        //配置退出成功处理器
        http.logout().logoutSuccessHandler(appLogOutSuccessHandler);
    }
}