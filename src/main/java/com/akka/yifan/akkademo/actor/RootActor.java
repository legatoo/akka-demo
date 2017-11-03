package com.akka.yifan.akkademo.actor;

import akka.actor.ActorRef;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.japi.Function;
import com.akka.yifan.akkademo.msg.ClusterDownMsg;
import com.akka.yifan.akkademo.msg.DeadMsg;
import com.akka.yifan.akkademo.msg.PingMsg;
import com.akka.yifan.akkademo.msg.PongMsg;
import com.akka.yifan.akkademo.msg.ReloadMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Option;
import scala.PartialFunction;
import scala.concurrent.duration.Duration;
import scala.runtime.BoxedUnit;

import java.util.concurrent.TimeUnit;

public class RootActor extends UntypedActor {
    private static final Logger Log = LoggerFactory.getLogger(RootActor.class);

    private final String configuration;
    private ActorRef middleActorRef;

    @Override public SupervisorStrategy supervisorStrategy() {
        return new OneForOneStrategy(1, Duration.create("3 sec"),
            new Function<Throwable, SupervisorStrategy.Directive>() {
                @Override public SupervisorStrategy.Directive apply(Throwable throwable) throws Exception {
                    SupervisorStrategy.Directive choose = OneForOneStrategy.restart();
                    Log.error("root strategy {} is triggered by {}", choose, throwable.getMessage());
                    return choose;
                }
            });
    }

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
        Log.warn("root around post stop, configuration {}", configuration);
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
        getContext().system().scheduler()
            .scheduleOnce(
                Duration.create(5, TimeUnit.SECONDS),
                getSelf(),
                new ReloadMsg(),
                getContext().system().dispatcher(),
                getSelf());
        Log.warn("root post re-start, sent a scheduler message to reload at {}", System.currentTimeMillis());
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

        if (o instanceof ClusterDownMsg) {
            Log.error("root actor is going to down");
            throw new RuntimeException("root-dead-exception");
        }

        if(o instanceof ReloadMsg){
            Log.error("root recovery from dead, going to rebuild index at {}", System.currentTimeMillis());
            return;
        }

        if(o instanceof Terminated){
            Log.error("root got terminate msg = {}", o);
            return;
        }

        unhandled(o);
    }
}
