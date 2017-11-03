package com.akka.yifan.akkademo.msg;

public class PongMsg {
    private MsgCode code = MsgCode.pong;

    @Override
    public String toString() {
        return "msg ---> " + code.name();
    }
}
