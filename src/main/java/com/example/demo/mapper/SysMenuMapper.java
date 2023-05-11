package com.example.demo.mapper;

import com.example.demo.entity.SysMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Administrator
 * @Entity com.example.demo.entity.SysMenu
 */
public interface SysMenuMapper extends BaseMapper<SysMenu> {
    /**
     * 根据用户的id通过多表连接去mysql中获取该用户有着什么样的权限
     * @param userId       传入用户id我们好去查该用户有啥权限
     * @return          返回权限
     */
    List<String> getMenuList(@Param("userId") String userId);
}




