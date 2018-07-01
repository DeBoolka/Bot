package dikanev.nikita.bot;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dikanev.nikita.bot.model.callback.commands.VkCommand;
import dikanev.nikita.bot.model.storage.*;
import dikanev.nikita.bot.model.storage.clients.CoreClientStorage;
import dikanev.nikita.bot.model.storage.clients.VkClientStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;

public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    static private boolean shutdownFlag = false;

    public static void  main(String... args) throws Exception{
        LOG.debug("Application enabled");

        daemonize();

        registerShutdownHook();

        init();

        LOG.info("Start application");
//        start();

        LOG.info("Stop application");
    }

    private static void daemonize() throws Exception {
        System.in.close();
        System.out.close();
    }

    private static void init() throws Exception{

        try {
            Properties properties = loadConfiguration();

            //Подгрузка информации о сервере
            DataStorage.getInstance().init(properties);

            //Подключение к DB
            DBStorage.getInstance().init(properties);

            //Старт сервера прослушевоющего vk
            ServerStorage.getInstance().start(properties);

            //Старт клиента общающегося с ядром
            CoreClientStorage.init(properties);

            //Старт работы в background
            JobStorage.getInstance().start();
        } catch (Exception e) {
            LOG.error("Fatal error: ", e);
            throw e;
        }
    }

    private static Properties loadConfiguration() {
        Properties properties = new Properties();
        try (InputStream is = Application.class.getResourceAsStream("/config.properties")) {
            properties.load(is);
        } catch (IOException e) {
            LOG.error("Can't load properties file", e);
            throw new IllegalStateException(e);
        }

        return properties;
    }

    static public void setShutdownFlag() {
        System.exit(0);
    }

    private static void registerShutdownHook()
    {
        Runtime.getRuntime().addShutdownHook(
                new Thread(Application::setShutdownFlag)
        );
    }

    private static void start() throws Exception{
        LocalDateTime ldt = LocalDateTime.now();
        while (!shutdownFlag) {
            if (LocalDateTime.now().minusMinutes(1).isAfter(ldt)) {
                shutdownFlag = true;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignore) {
            }
        }
    }

}
