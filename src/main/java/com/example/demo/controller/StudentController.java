package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Administrator
 * @USER: YangBaoLiang
 * 测试用，用来证明能访问此接口成功的用户确实是拥有student:select权限
 */
@RequestMapping("/student")
@RestController
public class StudentController {
    @RequestMapping("/test")
    public String test(){
        return "我有student:select权限哦";
    }
}
