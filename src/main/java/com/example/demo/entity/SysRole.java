package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 角色信息表-----此表在当前案例中无足轻重,所以里面的数据也没必要去仔细考究
 * @author Administrator
 * @TableName sys_role
 */
@TableName(value ="sys_role")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysRole implements Serializable {
    /**
     * 角色ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 角色名称
     */
    private String username;

    /**
     * 角色权限字符串
     */
    private String remark;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}