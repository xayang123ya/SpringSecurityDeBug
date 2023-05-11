package com.example.demo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Administrator
 * @USER: YangBaoLiang
 * 给前端返回数据（响应码、响应消息、响应数据）
 */
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class HttpResult {
    /**
     * 响应编码
     */
    private  Integer code;
    /**
     * 响应消息
     */
    private  String msg;
    /**
     * 响应数据
     */
    private  Object data;
}
