package me.zero.alpine.event;

public class Cancellable {
    private boolean cancelled;

    public void cancel() {
        cancelled = true;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public boolean isCancelled() {
        return cancelled;
    }
}
