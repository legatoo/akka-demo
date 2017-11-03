package com.akka.yifan.akkademo.cluster;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.akka.yifan.akkademo.actor.RootActor;
import com.akka.yifan.akkademo.msg.ClusterDownMsg;
import com.akka.yifan.akkademo.msg.DeadMsg;
import com.akka.yifan.akkademo.msg.PingMsg;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class DemoCluster implements InitializingBean{
    private String configuration = "system configuration";
    private ActorSystem system;
    private ActorRef rootActor;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.system = ActorSystem.create("demo-akka-system");
        this.rootActor = this.system
                .actorOf(Props.create(RootActor.class, "root configuration"), "root-actor");
    }

    public void sendPing(){
        rootActor.tell(new PingMsg(), ActorRef.noSender());
    }

    public void sendDead(){
        rootActor.tell(new DeadMsg(), ActorRef.noSender());
    }

    public void clusterDown() {rootActor.tell(new ClusterDownMsg(), ActorRef.noSender());}
}
