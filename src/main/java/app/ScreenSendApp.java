package app;

import app.bean.ConnectionContext;
import app.constants.ServerMode;
import app.screen.ScreenQueue;
import app.service.ScreenSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Hello world!
 */
@Slf4j
@Component
public class ScreenSendApp {

    private ScreenSaveService screenService;

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public ScreenQueue prototypeBean() {
        return new ScreenQueue();
    }

    public void start(String[] args) {
        ConnectionContext ctx = new ConnectionContext(ServerMode.SAVE);
        screenService = new ScreenSaveService(ctx);


        Thread monitoringThread = new Thread(() -> {
            if (ctx.enableToConnect() && !ctx.isConnected()) {
                screenService.start();
            }

            if (!ctx.enableToConnect()) {
                screenService.stop();
//                senderImageThread.interrupt();
            }
        }, "Monitor");

        monitoringThread.start();
    }
}
