package simple.client;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;
import simple.common.NotificationType;

/**
 * Log4J appender which logs to the game console.
 * 
 * @author Matthias Totz
 */
public class GameConsoleAppender extends AppenderSkeleton {

    @Override
    protected void append(final LoggingEvent loggingEvent) {
        final StringBuilder buf = new StringBuilder();
        buf.append(getLayout().format(loggingEvent));
        final ThrowableInformation ti = loggingEvent.getThrowableInformation();

        if (ti != null) {
            final String[] cause = ti.getThrowableStrRep();

            for (final String line : cause) {
                buf.append(line).append('\n');
            }
        }
        if (SimpleUI.get() != null) {
            SimpleUI.get().addEventLine(new HeaderLessEventLine(buf.toString(), NotificationType.CLIENT));
        }
    }

    @Override
    public void close() {
        // implementation of abstract method
        // yet nothing do to
    }

    @Override
    public boolean requiresLayout() {
        return true;
    }
}
