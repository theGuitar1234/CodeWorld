package az.codeworld.springboot.utilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;

@Profile("dev")
public class WriteLog {

    public static <T> void main(String log, Class<T> type) {

        String logMessage = buildLogMessage(log, type);

        Logger lg = LoggerFactory.getLogger(type);
        lg.info(logMessage);

        System.out.println("\n\n\n\n\n\n\n\n" + logMessage + "\n\n\n\n\n\n\n\n");

        File file = new File("D:\\CodeWorld\\log.txt");
        try {
            Files.write(
                Path.of(file.getAbsolutePath()), 
                logMessage.getBytes(), 
                StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static <T> String buildLogMessage(String logMessage, Class<T> type) {
        return "[" + LocalDateTime.now() + "] " + "You made it to : " + type.getCanonicalName() + " ...with a log message of : " + logMessage + '\n'; 
    }
}
