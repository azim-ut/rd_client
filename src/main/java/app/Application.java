package app;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class Application {

    public static void main(String[] args) throws InterruptedException {
        if (args.length > 0) {
            ApplicationContext context;
            context = new AnnotationConfigApplicationContext(ScreenSendApp.class);
            context.getBean(ScreenSendApp.class).start(args);
        }
    }
}
