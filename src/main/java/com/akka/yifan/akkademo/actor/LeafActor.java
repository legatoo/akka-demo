package com.akka.yifan.akkademo.actor;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import com.akka.yifan.akkademo.msg.DeadMsg;
import com.akka.yifan.akkademo.msg.PingMsg;
import com.akka.yifan.akkademo.msg.PongMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Option;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

import java.util.HashSet;
import java.util.Set;

public class LeafActor extends UntypedActor{
    private static final Logger Log = LoggerFactory.getLogger(LeafActor.class);
    private final Integer index;
    private final String configuration;
    private final ActorRef middleActor;
    private final Set<Integer> store;

    public LeafActor(String configuration, Integer index, ActorRef actorRef) {
        Log.info("leaf {} in constructor, configuration {}", index, configuration);
        this.index = index;
        this.configuration = configuration;
        this.middleActor = actorRef;
        store = new HashSet<>();
        for (int i = 0; i < 3; i++){
            store.add(index);
        }
    }

    @Override
    public void aroundReceive(PartialFunction<Object, BoxedUnit> receive, Object msg) {
        super.aroundReceive(receive, msg);
        Log.info("leaf {} around receive, configuration {}, receive {}, message {}", index, configuration, receive, msg);
    }

    @Override
    public void aroundPreStart() {
        super.aroundPreStart();
        Log.info("leaf {} around pre start, configuration {}", index, configuration);
    }

    @Override
    public void aroundPostStop() {
        super.aroundPostStop();
        Log.warn("leaf {} around post start, configuration {}", index, configuration);
    }

    @Override
    public void aroundPreRestart(Throwable reason, Option<Object> message) {
        super.aroundPreRestart(reason, message);
        Log.warn("leaf {} around pre re-start, configuration {}, reason {}, message {}", index, configuration, reason, message);
    }

    @Override
    public void aroundPostRestart(Throwable reason) {
        super.aroundPostRestart(reason);
        Log.warn("leaf {} around post re-start, configuration {}, reason {}", index, configuration, reason);
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        Log.info("leaf {} pre start, configuration {}", index, configuration);
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
        Log.warn("leaf {} post stop, configuration {}", index, configuration);
    }

    @Override
    public void preRestart(Throwable reason, Option<Object> message) throws Exception {
        super.preRestart(reason, message);
        Log.warn("leaf {} pre re-start, configuration {}, reason {} message {}", index, configuration, reason, message);
    }

    @Override
    public void postRestart(Throwable reason) throws Exception {
        super.postRestart(reason);
        Log.warn("leaf {} post re-start, configuration {} reason {}", index, configuration, reason);
    }

    @Override
    public void onReceive(Object o) throws Throwable {
        if(o instanceof PingMsg) {
            Log.info("leaf {} got ping msg, store content {}, ack pong", index, store);
            middleActor.tell(new PongMsg(), getSelf());
            return;
        }

        if(o instanceof DeadMsg) {
            Log.info("leaf {} got dead msg, throw exception", index);
            throw new RuntimeException("leaf throw");
        }

        unhandled(o);
    }
}
