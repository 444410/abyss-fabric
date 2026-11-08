package me.ciruu.abyss.events;

import me.zero.alpine.event.Cancellable;

public final class EventChatSend extends Cancellable {
    public String message;

    public EventChatSend(String message) {
        this.message = message;
    }
}
