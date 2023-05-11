package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.SysUser;
import com.example.demo.service.SysUserService;
import com.example.demo.mapper.SysUserMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 *
 * @author Administrator
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser>
    implements SysUserService{
    @Resource
    private SysUserMapper sysUserMapper;
    @Override
    public SysUser getSysUser(String username) {
        return sysUserMapper.selectOne(new QueryWrapper<SysUser>().eq("username",username));
    }
}




