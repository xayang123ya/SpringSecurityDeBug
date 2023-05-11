package com.example.demo.vo;

import com.example.demo.entity.SysUser;
import com.example.demo.mapper.SysMenuMapper;
import com.example.demo.mapper.SysUserMapper;
import com.example.demo.service.SysMenuService;
import com.example.demo.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 * @USER: YangBaoLiang
 * 用户在认证输入账号密码后会执行此SecurityUserService实现类中的loadUserByUsername方法，我们可以在loadUserByUsername中对前端需要认证而传入的账号作校验(密码框架来为我们校验),判断其是否有效(若是未将UserDetailsService重写的话，则我们的SpringSecurity框架会现在UserDetailsService找到已经定义了的用户，然后自己去为我们传入的账号与密码作校验)
 * 如果用户在认证输入账号密码是正确的话,那么就在需要此实现类将该用户的信息返回(将其存在我们的安全上下文中)
 * 此UserDetailsService的实现类中的loadUserByUsername用于判断用户是否存在(原先是由SpringSecurity为我们做的，只不过在我们实现UserDetailsService后用户名就需要我们来判定了，而用户对应的密码从始至终都是由SpringSecurity为我们判断)
 * */
@Component
@Slf4j
public class SecurityUserService implements UserDetailsService {
    @Resource
   private SysUserService sysUserService;
    @Resource
    private SysMenuService sysMenuService;

    /**
     * 根据用户名来获取用户的详细信息
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        /**
         * StringUtils.hasText(username)用来判断String类型变量是否为null(判断前端传过来的用户名是不是null的),为null的话就会返回true
         */
        if (StringUtils.hasText(username)){
            /**
             * 如果用户名不存在就抛出异常
             */
            new UsernameNotFoundException("用户名不存在");
        }
        /**
         * 如果我们传入的用户名是无效的话,也就是在我们的数据库中找不到的话
         */
        if (sysUserService.getSysUser(username)==null){
            /**
             * 如果我们传入的用户名是无效的话就抛出异常
             */
            new UsernameNotFoundException("用户名不存在");
        }
        /**
         * 走到这一步就说明我们的前端在认证时传入的用户名是有效的,那么关于用户对应的密码就交由我们的SpringSecurity去处理/校验
         * */
        List<SimpleGrantedAuthority> authorities=new ArrayList<>();
        /**
         * 如果我们的传入的用户名是正确的,那么先将对应的用户信息进行返回,然后再由框架去为我们校验该用户对应的密码是否与我们前端输入的密码一致(也就是密码校验正确)，认证成功后我们的用户信息实体类才会返回，否则就会进入到认证失败的流程
         * 密码是SpringSecurity框架自己就会帮我们判断的,我们只需要判断用户传入的用户名是否是有效的
         * 将对应用户拥有的所有权限也一并返回(去存到我们的安全上下文中)
         */
        SecurityUser securityUser = new SecurityUser(sysUserService.getSysUser(username), authorities);
        /**
         * 通过对应的用户名找到对应的用户信息,再由用户id找到其对应所拥有的权限，返回一个字符串集合
         */
        if (sysUserService.getSysUser(username)==null){
            return securityUser;
        }
        List<String> menuList = sysMenuService.getMenuList(sysUserService.getSysUser(username).getUserId());
        for (int i = 0; i < menuList.size(); i++) {
            /**
             * SimpleGrantedAuthority里面放的其实就是字符串
             */
            authorities.add(new SimpleGrantedAuthority(menuList.get(i)));
        }
        return securityUser;
    }
}
