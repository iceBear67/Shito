package shito.session;

import cc.sfclub.events.MessageEvent;
import cc.sfclub.events.message.direct.PrivateMessage;
import cc.sfclub.user.User;
import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;

@RequiredArgsConstructor
public class SessionCreate implements ChatSession{
    private final User user;
    private final Consumer<String> dataConsumer;
    @Override
    public boolean test(MessageEvent event) {
        if(!(event instanceof PrivateMessage)){
            return false;
        }
        if(!(((PrivateMessage) event).getSender() == user)){
            return false;
        }
        dataConsumer.accept(event.getMessage());
        return true;
    }
}
