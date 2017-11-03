package com.akka.yifan.akkademo;

import com.akka.yifan.akkademo.cluster.DemoCluster;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/akka")
public class DemoController {
    @Autowired DemoCluster cluster;

    @RequestMapping("/ping") @ResponseBody
    public String ping() {
        cluster.sendPing();
        return  "ping msg sent";
    }

    @RequestMapping("/dead") @ResponseBody
    public String dead() {
        cluster.sendDead();
        return  "dead msg sent";
    }

    @RequestMapping("/clusterdown") @ResponseBody
    public String clusterDown() {
        cluster.clusterDown();
        return  "dead msg sent";
    }
}
