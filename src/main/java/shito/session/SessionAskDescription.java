package shito.session;

import cc.sfclub.user.User;

import java.util.function.Consumer;

public class SessionAskDescription extends SessionCreate{
    public SessionAskDescription(User user, Consumer<String> dataConsumer) {
        super(user, dataConsumer);
    }
}
