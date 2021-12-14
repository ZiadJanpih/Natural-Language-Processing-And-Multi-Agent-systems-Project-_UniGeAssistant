package com.ziad.unigeassistant.Handlers;

import org.greenrobot.eventbus.EventBus;

public class BusHolder {

    private static EventBus eventBus;

    public static EventBus getInstnace() {
        if (eventBus == null) {
            eventBus = new EventBus();
        }
        return eventBus;
    }

    private BusHolder() {
    }
}