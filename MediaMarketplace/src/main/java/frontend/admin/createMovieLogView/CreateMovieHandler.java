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

/**
 * Custom log handler for managing and displaying log records in the JavaFX application.
 * <p>This handler processes log records and updates an observable list with the log entries,
 * ensuring that updates to the UI are made on the JavaFX application thread.</p>
 */
public class CreateMovieHandler extends Handler {

    /**
     * Maximum number of log records to append in a single update.
     * <p>This value is used to limit the number of log records processed at once to prevent
     * excessive UI updates.</p>
     */
    private static final int MAX_APPEND = 100;

    /**
     * Queue for storing log records before they are processed.
     * <p>This queue holds log records that need to be appended to the observable list.</p>
     */
    private final Queue<LogRecord> recordQueue = new ArrayDeque<>();

    /**
     * Flag indicating whether the JavaFX thread should be notified for processing records.
     */
    private boolean notify = true;

    /**
     * Observable list where log records are appended.
     * <p>This list is used to display log entries in the UI.</p>
     */
    private final ObservableList<LoggerRecord> createMovieLoggerList;

    /**
     * Flag indicating whether the handler is open and can process log records.
     */
    private volatile boolean open = true;

    /**
     * Constructs a {@link CreateMovieHandler} with the specified observable list.
     * 
     * @param createMovieLoggerList The {@link ObservableList} to which log records will be appended.
     */
    public CreateMovieHandler(ObservableList<LoggerRecord> createMovieLoggerList) {
        this.createMovieLoggerList = createMovieLoggerList;
        setFormatter(new SimpleFormatter());
    }

    /**
     * Publishes a log record to the handler.
     * <p>This method either processes the record immediately if on the JavaFX thread or
     * queues it for processing later if on a different thread.</p>
     * 
     * @param record The {@link LogRecord} to publish.
     */
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

    /**
     * Notifies the JavaFX thread to process the queue of log records.
     * <p>This method is invoked to ensure that the UI updates are executed on the JavaFX thread.</p>
     */
    private void notifyFxThread() {
        try {
            Platform.runLater(this::processQueue);
        } catch (Exception ex) {
            reportError(null, ex, ErrorManager.GENERIC_FAILURE);
        }
    }

    /**
     * Processes the queue of log records and appends them to the observable list.
     * <p>This method retrieves a batch of records from the queue and appends them to the list.</p>
     */
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

    /**
     * Appends a single log record to the observable list.
     * <p>This method is executed on the JavaFX thread to ensure that UI updates are safe.</p>
     * 
     * @param record The {@link LogRecord} to append.
     */
    private synchronized void appendRecord(LogRecord record) {
        try {
            Platform.runLater(() -> {
                createMovieLoggerList.add(new LoggerRecord(record));
            });
        } catch (Exception ex) {
            reportError(null, ex, ErrorManager.GENERIC_FAILURE);
        }
    }

    /**
     * Flushes the handler.
     * <p>This implementation does not need to do anything as there is no buffering in this handler.</p>
     */
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

    /**
     * Closes the handler.
     * <p>This method sets the open flag to false, preventing further processing of log records.</p>
     */
    @Override
    public void close() {
        open = false;
    }
}