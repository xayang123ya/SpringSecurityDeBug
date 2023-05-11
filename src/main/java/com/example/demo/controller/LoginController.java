package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Administrator
 * @USER: YangBaoLiang
 */
@Controller
@RequestMapping("/login")
public class LoginController {
    /**
     * 认证失败会访问此接口,我们进行登录时也会访问此接口
     */
    @RequestMapping("/toLogin")
    public String toLogin(){
        /**
         * 返回逻辑视图，所以我们还需要有对应的thymeleaf物理视图，否则就会报页面无法找到的错
         * 物理视图=前缀+逻辑视图名+后缀
         * /templates/login.html
         * */
        return "toLogin";
    }
    /**
     * 跳转到主页(认证成功)
     */
    @RequestMapping("/toIndex")

    public String doLogin(){
        /**
         * 返回逻辑视图，所以我们还需要有对应的thymeleaf物理视图，否则就会报页面无法找到的错
         * 物理视图=前缀+逻辑视图名+后缀
         * /templates/toIndex.html
         * */
        return "toIndex";
    }
}
