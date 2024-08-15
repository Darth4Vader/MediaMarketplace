package frontend.admin.createMovieLogView;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.logging.ErrorManager;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

import javafx.application.Platform;
import javafx.collections.ObservableList;

public class CreateMovieHandler extends Handler {

    // Avoid doing too much work at once. Tune this value as needed.
    private static final int MAX_APPEND = 100;

    private final Queue<LogRecord> recordQueue = new ArrayDeque<>();
    private boolean notify = true;

    private final ObservableList<LoggerRecord> createMovieLoggerList;
    private volatile boolean open = true;

    public CreateMovieHandler(ObservableList<LoggerRecord> createMovieLoggerList) {
        this.createMovieLoggerList = createMovieLoggerList;
        setFormatter(new SimpleFormatter());
    }

    @Override
    public void publish(LogRecord record) {
        if (open && isLoggable(record)) {
            if (Platform.isFxApplicationThread()) {
                appendRecord(record);
            } else {
                synchronized (recordQueue) {
                    recordQueue.add(record);
                    if (notify) {
                        notify = false;
                        notifyFxThread();
                    }
                }
            }
        }
    }

    private void notifyFxThread() {
        try {
            Platform.runLater(this::processQueue);
        } catch (Exception ex) {
            reportError(null, ex, ErrorManager.GENERIC_FAILURE);
        }
    }

    private void processQueue() {
        List<LogRecord> records = new ArrayList<>();
        synchronized (recordQueue) {
            while (!recordQueue.isEmpty() && records.size() < MAX_APPEND) {
                records.add(recordQueue.remove());
            }

            if (recordQueue.isEmpty()) {
                notify = true;
            } else {
                notifyFxThread();
            }
        }
        records.forEach(this::appendRecord);
    }

    private synchronized void appendRecord(LogRecord record) {
        try {
        	Platform.runLater(() -> {
        		createMovieLoggerList.add(new LoggerRecord(record));
        	});
        } catch (Exception ex) {
            reportError(null, ex, ErrorManager.GENERIC_FAILURE);
        }
    }

    @Override
    public void flush() {
        /*
         * This implementation has no "buffer". If the 'recordQueue' is not
         * empty then the JavaFX Application Thread has already been notified
         * of work, meaning the queue will be emptied eventually. And if the
         * JavaFX framework has been shutdown, then there's no work to do
         * anyway.
         */
    }

    @Override
    public void close() {
        open = false;
    }
}
