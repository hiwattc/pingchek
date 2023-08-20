package com.staroot.pingcheck.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingCheckController {
    @Autowired
    PingCheckService pingCheckService;
    @GetMapping("/check")
    public String check(){
        pingCheckService.performPingCheckAndSendEmail();
        return "test";

    }
}
