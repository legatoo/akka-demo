package com.akka.yifan.akkademo.msg;

public class DeadMsg {
    private MsgCode code = MsgCode.dead;

    @Override
    public String toString() {
        return "msg ---> " + code.name();
    }
}
