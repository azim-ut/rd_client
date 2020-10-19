package app;

import app.bean.ConnectionContext;
import app.constants.ServerMode;
import app.process.Monitoring;
import app.process.cast.CastImageThread;
import app.process.host.HostFetcherThread;
import app.process.screen.ScreenThread;
import app.screen.ScreenQueue;
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
public class CastScreenApp extends BaseApp {


    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public ScreenQueue prototypeBean() {
        return new ScreenQueue();
    }

    public void start(String[] args) {
        init(args);
        ConnectionContext ctx = new ConnectionContext(ServerMode.SAVE, this.code);

        HostFetcherThread hostUpdateThread = new HostFetcherThread(ctx);
        CastImageThread senderThread = new CastImageThread(ctx);
        ScreenThread screenThread = new ScreenThread(ctx);

        Thread monitoringThread = new Thread(new Monitoring(ctx), "Monitor");


        hostUpdateThread.start();
        senderThread.start();
        screenThread.start();

        monitoringThread.start();
    }
}
