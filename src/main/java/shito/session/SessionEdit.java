package shito.session;

import cc.sfclub.user.User;

import java.util.function.Consumer;

public class SessionEdit extends SessionCreate{
    public SessionEdit(User user, Consumer<String> dataConsumer) {
        super(user, dataConsumer);
    }
}
