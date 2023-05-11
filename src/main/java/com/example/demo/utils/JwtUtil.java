package com.example.demo.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 * @USER: YangBaoLiang
 */
@Component
@Slf4j
public class JwtUtil {
    /**
     * 定义密钥
     */
   private  static  final String SECRET="lalala";
    /**
     * 设置签发时间
     */
   Date issDate=new Date();
    /**
     * 设置JWT的过期时间
     * 在当前时间的基础上加上两个小时
     */
    Date expireDate=new Date (issDate.getTime() +1000*60*60*2) ;

    public String Secret(String userInfo, List<String>
            authList){
       /**
        * 创建我们的JWT
        */
       Map<String, Object> headerClaims=new HashMap<>(200);
       //定义我们头部中要指定的算法
       headerClaims.put("alg","HS256");
       //定义我们头部中要指定的token的类型(只能是JWT)
       headerClaims.put("typ","JWT");
       /**
        * 设置JWT头部
        */
   //先来定义我们要创建的JWT的头部
     return   JWT.create().withHeader(headerClaims)
               /**
                * 设置JWT负载(载荷)
                * with...表示的是公共声明(公有字段)
                * withClaim表示的是私有声明(私有字段)
                */
               //设置签发人（谁创建/生成了此JWT）
       .withIssuer("tomas")
                //设置JWT签发时间(签发人（创建了此JWT的人）什么时候创建了此JWT）
       .withIssuedAt(issDate)
               //设置JWT的过期时间
               .withExpiresAt(expireDate)
//               //自定义声明（声明用户id
//               .withClaim("userId", userId)
//               //自定义声明（声明用户对应的用户名）
//.withClaim( "userName" , userName)
             //自定义声明（声明用户id)与定义声明（声明用户对应的用户名）
             .withClaim("userInfo", userInfo)
               //自定义声明（声明用户拥有的权限）
.withClaim( "userAuth",authList)

        /**
         * 设置JWT签名
         */
               //使用HMAC256算法去进行签名,拿我们的常量SECRET来做为密钥
       .sign(Algorithm.HMAC256(SECRET));
   }

    /**
     * 校验token
     * 如果使用已经创建过但是已经过期了的jwt是无法被我们的校验器校验成功的
     * @return
     */
   public boolean verifyToken(String jwtToken){
       try {
//创建校验器(我们对头部和负载作签名时用的什么方法那我们校验签名时就需要使用对应的算法),需要传入密钥
           JWTVerifier build = JWT.require(Algorithm.HMAC256(SECRET)).build();
           //对我们的json格式token(jwt)进行校验，可以拿到解码后的jwt
                //如果token校验成功了就不会报错,否则就会抛出异常
           DecodedJWT verify = build.verify(jwtToken);
           log.info("token校验成功");
           //获取jwt中的声明(我们想要拿到的是jwt中负载(Playload)内的某些个私有字段对应的值)
           //asString()表示获取到的私有字段对应的数据类型为String
           //asList(String.class)表示获取到的私有字段对应的数据类型为String类型的集合.....剩下的以此类推
           String s = verify.getClaim("userInfo").asString();
           List<String> userAuths = verify.getClaim("userAuth").asList(String.class);
           return true;
       } catch (Exception e) {
           log.info("token可能是非法的/过期的（也是非法的）");
           return false;
       }
   }
    /**
     * 校验token
     * 如果使用已经创建过但是已经过期了的jwt是无法被我们的校验器校验成功的,我们需要在此方法中获取jwt内携带的用户详细信息中的用户信息
     * @return
     */
    public String getUserInfo(String jwtToken){
        try {
//创建校验器(我们对头部和负载作签名时用的什么方法那我们校验签名时就需要使用对应的算法),需要传入密钥
            JWTVerifier build = JWT.require(Algorithm.HMAC256(SECRET)).build();
            //对我们的json格式token(jwt)进行校验，可以拿到解码后的jwt
            //如果token校验成功了就不会报错,否则就会抛出异常
            DecodedJWT verify = build.verify(jwtToken);
            log.info("token校验成功");
            //获取jwt中的声明(我们想要拿到的是jwt中负载(Playload)内的某些个私有字段对应的值)
            //asString()表示获取到的私有字段对应的数据类型为String.....剩下的以此类推
            String s = verify.getClaim("userInfo").asString();
            return s;
        } catch (Exception e) {
            log.info("token可能是非法的/过期的（也是非法的）");
            return "";
        }
    }
    /**
     * 校验token
     * 如果使用已经创建过但是已经过期了的jwt是无法被我们的校验器校验成功的,我们需要在此方法中获取jwt内携带的用户详细信息中的用户所拥有的权限信息
     * @return
     */
    public List<String> getUserAuth(String jwtToken){
        try {
//创建校验器(我们对头部和负载作签名时用的什么方法那我们校验签名时就需要使用对应的算法),需要传入密钥
            JWTVerifier build = JWT.require(Algorithm.HMAC256(SECRET)).build();
            //对我们的json格式token(jwt)进行校验，可以拿到解码后的jwt
            //如果token校验成功了就不会报错,否则就会抛出异常
            DecodedJWT verify = build.verify(jwtToken);
            log.info("token校验成功");
            //获取jwt中的声明(我们想要拿到的是jwt中负载(Playload)内的某些个私有字段对应的值)
            //asList(String.class)表示获取到的私有字段对应的数据类型为String类型的集合.....剩下的以此类推
            List<String> userAuths = verify.getClaim("userAuth").asList(String.class);
            return userAuths;
        } catch (Exception e) {
            log.info("token可能是非法的/过期的（也是非法的）");
            return null;
        }
    }
}
