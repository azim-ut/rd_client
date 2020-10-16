package app;

import org.springframework.stereotype.Component;

@Component
public class BaseApp {
    protected int port;
    protected String code;
    protected String mode;

    public void init(String[] args) {
        System.out.println("MODE: " + args[0] + " CODE: " + args[1] + " PORT: " + args[2]);
        this.mode = args[0];
        this.code = args[1];
        this.port = Integer.parseInt(args[2]);
    }
}