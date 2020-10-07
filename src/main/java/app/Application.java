package app;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import picocli.CommandLine;


public class Application {

    @CommandLine.Option(names = {"-m", "--mode"}, defaultValue = "view", description = "'cast': cast screen, 'view': view script")
    private static String mode = "cast";

    public static void main(String[] args) throws InterruptedException {
        if (mode.equals("cast")) {
            ApplicationContext context = new AnnotationConfigApplicationContext(CastScreenApp.class);
            CastScreenApp serverApp = context.getBean(CastScreenApp.class);
            serverApp.start(args);
        } else {
            System.out.println("View");
        }
    }
}
