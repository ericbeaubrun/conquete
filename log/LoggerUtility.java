package log;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * Utility class used to generate Log4j logger.
 */
public class LoggerUtility {
    private static final String HTML_LOG_CONFIG = "src/log/log4j-html.properties";
    private static final String LOG_FILE_TYPE = ".html";
    private static String logFilename = null;

    public static synchronized Logger getLogger(Class<?> logClass) {
        if (logFilename == null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String timestamp = dateFormat.format(new Date());
            logFilename = "src/log/logs_" + timestamp + LOG_FILE_TYPE;
            System.setProperty("logFilename", logFilename);
            PropertyConfigurator.configure(HTML_LOG_CONFIG);
        }
        return Logger.getLogger(logClass);
    }
}

