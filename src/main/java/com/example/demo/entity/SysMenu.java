package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 菜单权限表
 * @author Administrator
 * @TableName sys_menu
 */
@TableName(value ="sys_menu")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysMenu implements Serializable {
    /**
     * 菜单ID
     */
    @TableId(type = IdType.AUTO)
    private Long menuId;


    private String menuName;


    private Long parentId;

    private Integer orderNum;

    /**
     * 权限
     */
    private String url;


    private String target;

    private String menuType;


    private String visible;


    private String isRefresh;

    private String perms;


    private String icon;


    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;


    private String remark;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}