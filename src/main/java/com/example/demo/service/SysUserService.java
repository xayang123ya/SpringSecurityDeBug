package com.example.demo.service;

import com.example.demo.entity.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 *
 */
public interface SysUserService extends IService<SysUser> {
    /**
     * 返回一个实体类,其中属性内容对应着我们数据库中对应用户的字段信息;通过传入的用户名获取对应用户的详细信息
     * @param username  返回一个实体类,其中属性内容对应着我们数据库中对应用户的字段信息
     * @return  通过传入的用户名获取对应用户的详细信息
     */
    SysUser getSysUser(String username);
}
