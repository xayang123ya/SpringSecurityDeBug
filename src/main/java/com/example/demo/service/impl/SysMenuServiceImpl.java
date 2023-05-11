package com.example.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.SysMenu;
import com.example.demo.service.SysMenuService;
import com.example.demo.mapper.SysMenuMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 *
 * @author Administrator
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu>
    implements SysMenuService{
    @Resource
private  SysMenuMapper sysMenuMapper;

    @Override
    public List<String> getMenuList(String userId) {
        return sysMenuMapper.getMenuList(userId);
    }
}




