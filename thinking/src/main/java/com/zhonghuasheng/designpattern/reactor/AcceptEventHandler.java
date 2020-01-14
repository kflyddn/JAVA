package com.zhonghuasheng.designpattern.reactor;

/**
 * Accept事件处理器实现类。
 */
public class AcceptEventHandler extends EventHandler {

    private Selector selector;

    public AcceptEventHandler(Selector selector) {
        this.selector = selector;
    }

    @Override
    public void handle(Event event) {
        if (EventType.ACCEPT == event.getType()) {
            Event readEvent = new Event();
            readEvent.setSource(event.getSource());
            readEvent.setType(EventType.READ);
            selector.addEvent(readEvent);
        }
    }
}
