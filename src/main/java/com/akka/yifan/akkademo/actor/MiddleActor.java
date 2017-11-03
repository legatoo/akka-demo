package com.akka.yifan.akkademo.actor;

import akka.actor.ActorRef;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.japi.Function;
import com.akka.yifan.akkademo.msg.DeadMsg;
import com.akka.yifan.akkademo.msg.PingMsg;
import com.akka.yifan.akkademo.msg.PongMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Option;
import scala.PartialFunction;
import scala.concurrent.duration.Duration;
import scala.runtime.BoxedUnit;

import java.util.ArrayList;
import java.util.List;

public class MiddleActor extends UntypedActor{
    private static final Logger Log = LoggerFactory.getLogger(MiddleActor.class);

    private final String configuration;
    private final ActorRef rootActor;
    private final List<ActorRef> leaves;

    @Override public SupervisorStrategy supervisorStrategy() {
        return new OneForOneStrategy(1, Duration.create("3 sec"),
            new Function<Throwable, SupervisorStrategy.Directive>() {
                @Override public SupervisorStrategy.Directive apply(Throwable throwable) throws Exception {
                    SupervisorStrategy.Directive choose = OneForOneStrategy.escalate();
                    Log.error("middle strategy {} is triggered by {}", choose, throwable.getMessage());
                    return choose;
                }
            });
    }

    public MiddleActor(String configuration, ActorRef rootActor) {
        Log.info("middle in constructor, configuration {}", configuration);

        this.configuration = configuration;
        this.rootActor = rootActor;
        this.leaves = new ArrayList<>();
        for (int i = 1; i < 3; i++){
            ActorRef leaf = getContext()
                    .actorOf(Props.create(LeafActor.class, "leaf configuration", i, getSelf()), "leaf-" + i);
            leaves.add(leaf);
            getContext().watch(leaf);
        }

    }

    @Override
    public void aroundReceive(PartialFunction<Object, BoxedUnit> receive, Object msg) {
        super.aroundReceive(receive, msg);
        Log.info("middle around receive, configuration {}, receive {}, message {}", configuration, receive, msg);
    }

    @Override
    public void aroundPreStart() {
        super.aroundPreStart();
        Log.info("middle around pre start, configuration {}", configuration);
    }

    @Override
    public void aroundPostStop() {
        super.aroundPostStop();
        Log.warn("middle around post stop, configuration {}", configuration);
    }

    @Override
    public void aroundPreRestart(Throwable reason, Option<Object> message) {
        super.aroundPreRestart(reason, message);
        Log.warn("middle around pre re-start, configuration {}, reason {}, message {}", configuration, reason, message);
    }

    @Override
    public void aroundPostRestart(Throwable reason) {
        super.aroundPostRestart(reason);
        Log.warn("middle around post re-start, configuration {}, reason {}", configuration, reason);
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        Log.info("middle pre start, configuration {}", configuration);
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
        Log.warn("middle post stop, configuration {}", configuration);
    }

    @Override
    public void preRestart(Throwable reason, Option<Object> message) throws Exception {
        super.preRestart(reason, message);
        Log.warn("middle pre re-start, configuration {}, reason {} message {}", configuration, reason, message);
    }

    @Override
    public void postRestart(Throwable reason) throws Exception {
        super.postRestart(reason);
        Log.warn("middle post re-start, configuration {}, reason, {}, notify root", configuration, reason);
        rootActor.tell(new PongMsg(), getSelf());
    }

    @Override
    public void onReceive(Object o) throws Throwable {
        if(o instanceof PingMsg) {
            Log.info("middle got ping msg, broadcast to leaves");
            for (ActorRef each : leaves){
                each.tell(o, getSelf());
            }
            return;
        }

        if(o instanceof PongMsg){
            Log.info("middle got pong msg, ack pong to root");
            rootActor.tell(o, getSelf());
            return;
        }

        if(o instanceof DeadMsg) {
            Log.info("middle got dead msg, broadcast to leaves");
            for (ActorRef each : leaves){
                each.tell(o, getSelf());
            }
            return;
        }

        if (o instanceof Terminated){
            Log.error("middle got terminate msg = {} from {}", o, getSender());
            return;
        }

        unhandled(o);
    }
}
