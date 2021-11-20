package shito.session;

import cc.sfclub.events.MessageEvent;

import java.util.function.Predicate;

public interface ChatSession extends Predicate<MessageEvent> {
}
