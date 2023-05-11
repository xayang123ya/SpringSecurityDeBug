package com.example.demo.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Administrator
 * @USER: YangBaoLiang
 */
@Controller
@Slf4j
public class CaptchaController {
    /**
     * 如果在此处依赖注入我们的httpServletResponse对象的话,在我们用户请求/code/getCaptchaCode时图片就没法返回（总之别放在这里）
     *  @Resource
     *  private HttpServletResponse httpServletResponse;
     * */

    /**
     * 访问/code/getCaptchaCode厚可以拿到我们CaptchaUtil为我们生成的图片(具体来说是图片验证码)，每次请求/code/getCaptchaCode接口时CaptchaUtil为我们生成的图片验证码都是不一样的
     */
    @GetMapping("/code/getCaptchaCode")
    public void getCaptchaCode(HttpServletResponse httpServletResponse,HttpServletRequest servletRequest){
        /**
         * 第一个参数表示我们要生成的图片验证码有多长
         * 第二个参数表示我们要生成的图片验证码有多宽
         * 第三个参数表示我们要生成的图片验证码中有多少个字符(我们用户要输入的字符为几个)
         * 第四个参数表示我们要生成的图片验证码中有多少个圆点--可以干扰机器识别
         */
        CircleCaptcha circleCaptcha = CaptchaUtil.createCircleCaptcha(200, 100, 5, 9);
        /**
         * 获取到生成的图片的验证码中的字符是哪些
         */
        String code = circleCaptcha.getCode();
        log.info("生成的图片的验证码中的字符是哪些:{}",code);
        /**
         * 将获取到的生成的图片的验证码中的字符存在我们的session中
         */
        servletRequest.getSession().setAttribute("code",code);
        /**
         * 将CaptchaUtil为我们生成的图片进行输出
         * 第一个参数为图片文件(传入CaptchaUtil为我们生成的图片)
         * 第二个参数为图片的类型
         * 第三个参数就表示我们可以将图片输出到响应流中，需要传入响应流--就可以返回给我们的前端了（也就是前端使用img标签访问此接口后就可以在前端将新生成的验证码图片显示到img标签中）
         */
        try {
            ImageIO.write(circleCaptcha.getImage(),"jpeg",httpServletResponse.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
