package com.xrf.backend.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    private Integer code;//响应码，200 代表成功; 400 代表失败
    private String message;  //响应信息 描述字符串
    private Object data; //返回的数据

    //增删改 成功响应
    public static Result success(){
        return new Result(200,"success",null);
    }

    // 带自定义提示语的成功响应
    public static Result successMessage(String message){
        return new Result(200, message, null);
    }

    //查询 成功响应
    public static Result success(Object data){
        return new Result(200,"success",data);
    }

    //失败响应
    public static Result error(String message){
        return new Result(400,message,null);
    }

    public static Result error(int code, String message) {
        return new Result(code, message, null);
    }
}

