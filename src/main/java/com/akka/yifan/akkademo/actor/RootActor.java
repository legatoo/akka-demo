package com.akka.yifan.akkademo.actor;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import com.akka.yifan.akkademo.msg.DeadMsg;
import com.akka.yifan.akkademo.msg.MsgCode;
import com.akka.yifan.akkademo.msg.PingMsg;
import com.akka.yifan.akkademo.msg.PongMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Option;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public class RootActor extends UntypedActor {
    private static final Logger Log = LoggerFactory.getLogger(RootActor.class);

    private final String configuration;
    private ActorRef middleActorRef;


    public RootActor(String configuration) {
        Log.info("root in constructor, configuration {}", configuration);

        this.configuration = configuration;
        this.middleActorRef = getContext()
                .actorOf(Props.create(MiddleActor.class, "middle configuration", getSelf()), "middle-actor");
        getContext().watch(this.middleActorRef);
    }

    @Override
    public void aroundReceive(PartialFunction<Object, BoxedUnit> receive, Object msg) {
        super.aroundReceive(receive, msg);
        Log.info("root around receive, configuration {}, receive {}, message {}", configuration, receive, msg);
    }

    @Override
    public void aroundPreStart() {
        super.aroundPreStart();
        Log.info("root around pre start, configuration {}", configuration);
    }

    @Override
    public void aroundPostStop() {
        super.aroundPostStop();
        Log.warn("root around post start, configuration {}", configuration);
    }

    @Override
    public void aroundPreRestart(Throwable reason, Option<Object> message) {
        super.aroundPreRestart(reason, message);
        Log.warn("root around pre re-start, configuration {}, reason {}, message {}", configuration, reason, message);
    }

    @Override
    public void aroundPostRestart(Throwable reason) {
        super.aroundPostRestart(reason);
        Log.warn("root around post re-start, configuration {}, reason {}", configuration, reason);
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        Log.info("root pre start, configuration {}", configuration);
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
        Log.warn("root post stop, configuration {}", configuration);
    }

    @Override
    public void preRestart(Throwable reason, Option<Object> message) throws Exception {
        super.preRestart(reason, message);
        Log.warn("root pre re-start, configuration {}, reason {} message {}", configuration, reason, message);
    }

    @Override
    public void postRestart(Throwable reason) throws Exception {
        super.postRestart(reason);
        Log.warn("root post re-start, configuration {} reason {}", configuration, reason);
    }

    @Override
    public void onReceive(Object o) throws Throwable {
        if(o instanceof PingMsg) {
            Log.info("root got ping msg, tell middle");
            middleActorRef.tell(o, getSelf());
            return;
        }

        if(o instanceof PongMsg){
            Log.info("root got pong msg, calling callback");
            return;
        }

        if(o instanceof DeadMsg){
            middleActorRef.tell(o, getSelf());
            return;
        }

        if(o instanceof Terminated){
            Log.error("root got terminate msg = {}", o);
            return;
        }

        unhandled(o);
    }
}