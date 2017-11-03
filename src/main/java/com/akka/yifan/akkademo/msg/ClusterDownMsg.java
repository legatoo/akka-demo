package com.akka.yifan.akkademo.msg;

/**
 * Author: Yang Yifan
 * MisId: yangyifan03
 * Project: akka-demo
 * Date: 11/3/17
 * Time: 11:10 AM
 */
public class ClusterDownMsg {
    private MsgCode code = MsgCode.clusterDown;

    @Override
    public String toString() {
        return "msg ---> " + code.name();
    }
}
