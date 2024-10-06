package nl.enjarai.doabarrelroll.util;

public class DelayedRunnable {
    private final Runnable runnable;
    private final int delay;
    private int ticks = 0;

    public DelayedRunnable(int delay, Runnable runnable) {
        this.runnable = runnable;
        this.delay = delay;
    }

    public void tick() {
        if (++ticks >= delay) {
            runnable.run();
        }
    }

    public boolean isDone() {
        return ticks >= delay;
    }
}
