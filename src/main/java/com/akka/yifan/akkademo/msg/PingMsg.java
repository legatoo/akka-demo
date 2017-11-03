package com.akka.yifan.akkademo.msg;

public class PingMsg {
    private MsgCode code = MsgCode.ping;

    @Override
    public String toString() {
        return "msg ---> " + code.name();
    }
}
