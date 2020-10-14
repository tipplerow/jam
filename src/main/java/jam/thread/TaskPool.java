
package jam.thread;

import java.util.Collection;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import jam.lang.JamException;

/**
 * Executes a group of tasks in parallel and waits for them to finish.
 */
public final class TaskPool {
    private final long timeout;
    private final TimeUnit unit;
    private final ForkJoinPool pool;
    private final Collection<? extends Runnable> tasks;

    private TaskPool(Collection<? extends Runnable> tasks, long timeout, TimeUnit unit) {
        this.pool = new ForkJoinPool();
        this.unit = unit;
        this.tasks = tasks;
        this.timeout = timeout;
    }

    /**
     * Executes a group of tasks in parallel and waits essentially
     * forever for them to finish.
     *
     * @param tasks the runnable tasks to execute.
     */
    public static void run(Collection<? extends Runnable> tasks) {
        run(tasks, 365L, TimeUnit.DAYS);
    }

    /**
     * Executes a group of tasks in parallel and waits a finite time
     * for them to finish.
     *
     * @param tasks the runnable tasks to execute.
     *
     * @param timeout the length of time to wait for all tasks to
     * finish.
     *
     * @param unit the units in which the timeout are expressed.
     *
     * @return {@code true} iff all tasks completed in the allotted
     * time.
     *
     * @throws RuntimeException if any errors occur.
     */
    public static boolean run(Collection<? extends Runnable> tasks, long timeout, TimeUnit unit) {
        TaskPool taskPool = new TaskPool(tasks, timeout, unit);
        return taskPool.run();
    }

    private boolean run() {
        for (Runnable task : tasks)
            pool.execute(task);

        pool.shutdown();

        try {
            return pool.awaitTermination(timeout, unit);
        }
        catch (InterruptedException ex) {
            throw JamException.runtime(ex);
        }
    }
}
