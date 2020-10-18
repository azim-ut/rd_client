package app;

import app.constants.ClientMode;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class Application {

    public static void main(String[] args) throws InterruptedException {
        if (args.length > 0) {
            ApplicationContext context;
            switch (ClientMode.valueOf(args[0])) {
                case CAST:
                    context = new AnnotationConfigApplicationContext(CastScreenApp.class);
                    context.getBean(CastScreenApp.class).start(args);
                    break;
                case GET:
                    context = new AnnotationConfigApplicationContext(ReceiveScreenApp.class);
                    context.getBean(ReceiveScreenApp.class).start(args);
                    break;
            }
        }
    }
}
