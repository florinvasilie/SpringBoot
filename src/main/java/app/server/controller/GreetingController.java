package app.server.controller;

import app.server.model.Greeting;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by fvasilie on 9/19/2016.
 */
@RestController
public class GreetingController {
    private static final String template="Salut, %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping(value = "/greeting")
    public Greeting greeting (@RequestParam(value="name", defaultValue = "BOSS") String name){
        return new Greeting(counter.incrementAndGet(), String.format(template,name));
    }
}
