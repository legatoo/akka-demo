package com.akka.yifan.akkademo.msg;

/**
 * Author: Yang Yifan
 * MisId: yangyifan03
 * Project: akka-demo
 * Date: 11/3/17
 * Time: 11:14 AM
 */
public class ReloadMsg {
    private MsgCode code = MsgCode.reload;

    @Override
    public String toString() {
        return "msg ---> " + code.name();
    }
}
