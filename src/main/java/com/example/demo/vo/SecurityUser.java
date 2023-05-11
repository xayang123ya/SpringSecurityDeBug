package com.example.demo.vo;

import com.example.demo.entity.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * @author Administrator
 * @USER: YangBaoLiang
 * 用来定义可变的可以通过认证的用户的信息，可以替代我们之前案例中声明的MySecurityUserConfig类（在MySecurityUserConfig类中只能将可以通过系统认证的用户的个数写死）
 *      但是配置密码编码器我们还是得在MySecurityUserConfig中写
 */
@Slf4j
public class SecurityUser implements UserDetails {
    /**
     * 用于存储权限的list
     */
    private List<SimpleGrantedAuthority> authorities;
    private final SysUser sysUser;
    /**
     * 在SecurityUser对象被返回给安全上下文前，就将我们认证已成功的用户信息的实体类传入进来(不包含用户所拥有的权限),从而实现可以让我们动态获取认证成功了的用户信息的目的
     * 在SecurityUser对象被返回给安全上下文前，就将我们认证已成功的用户所拥有的对应权限传入进来,从而实现可以让我们动态获取认证成功了的用户的所拥有的权限的目的
     * @param sysUser
     */
    public SecurityUser(SysUser sysUser,List<SimpleGrantedAuthority> authorities){
        this.sysUser=sysUser;
        this.authorities=authorities;
    };

    public SysUser getSysUser() {
        return sysUser;
    }

    /**
     * 返回的是用户所拥有的权限是什么(一个用户可以有一个,也可以多个,甚至是可以不设置)
     * @return
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        log.info("啦啦啦啦，用户输入的权限:{}",authorities);
        return authorities;
    }
    /**
     * 返回的是用户认证时所输入的密码并且还被我们的编码器加密过了--也就是加密后的密码(密文)
     * @return
     */
    @Override
    public String getPassword() {
        log.info("啦啦啦啦，用户输入的密码:{}",sysUser.getPassword());
        return sysUser.getPassword();
    }
    /**
     * 返回的是用户认证时所输入对应的账号(用户名)
     * @return
     */
    @Override
    public String getUsername() {
        log.info("啦啦啦啦，用户输入的用户名:{}",sysUser.getUsername());
        return sysUser.getUsername();
    }

    /**
     * 返回的是状态：账户是否未过期（需要返回true-账户未过期,如果账号过期了我们的系统肯定不允许其再能访问我们系统中的任意接口）
     * @return
     */
    @Override
    public boolean isAccountNonExpired() {
        return sysUser.getAccountNoExpired().equals("1");
    }

    /**
     * 返回的是状态：账户是否为锁定
     * @return
     */
    @Override
    public boolean isAccountNonLocked() {
        return sysUser.getAccountNoLocked().equals("1");
    }

    /**
     * 返回的是状态：用户凭据(密码)是否未过期
     * @return
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return sysUser.getCredentialsNoExpired().equals("1");
    }

    /**
     * 返回的是状态：此账号是否能被使用
     * @return
     */
    @Override
    public boolean isEnabled() {
        return sysUser.getEnabled().equals("1");
    }
}
