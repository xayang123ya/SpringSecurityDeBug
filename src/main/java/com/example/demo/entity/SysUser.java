package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 用户信息表
 * @author Administrator
 * @TableName sys_user
 */
@TableName(value ="sys_user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysUser implements Serializable {
    /**
     * 用户ID
     */
    @TableId(type = IdType.AUTO)
    private String userId;


    private String username;

    /**
     * 密码(默认的为123456)
     */
    private String password;


    private String sex;


    private String address;


    private String enabled;

    private String accountNoExpired;


    private String credentialsNoExpired;


    private String accountNoLocked;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}