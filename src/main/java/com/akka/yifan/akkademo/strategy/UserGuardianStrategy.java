package com.akka.yifan.akkademo.strategy;

import akka.actor.OneForOneStrategy;
import akka.actor.SupervisorStrategy;
import akka.actor.SupervisorStrategyConfigurator;
import akka.japi.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.duration.Duration;

/**
 * Author: Yang Yifan
 * MisId: yangyifan03
 * Project: akka-demo
 * Date: 11/3/17
 * Time: 4:20 PM
 */
public class UserGuardianStrategy implements SupervisorStrategyConfigurator {
    private static final Logger Log = LoggerFactory.getLogger(UserGuardianStrategy.class);

    @Override
    public SupervisorStrategy create() {
        return new OneForOneStrategy(1, Duration.create("3 sec"),
            new Function<Throwable, SupervisorStrategy.Directive>() {
                @Override public SupervisorStrategy.Directive apply(Throwable throwable) throws Exception {
                    SupervisorStrategy.Directive choose = OneForOneStrategy.stop();
                    Log.error("user guardian strategy {} is triggered by {}", choose, throwable.getMessage());
                    return choose;
                }
            });
    }
}
