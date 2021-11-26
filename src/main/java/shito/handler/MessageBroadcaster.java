package shito.handler;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import shito.api.data.ShitoMessage;

import java.util.Collections;

public class MessageBroadcaster {
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onPushEvent(ShitoMessage message) {
        var msg = message.render(Collections.emptyMap());
        message.getTemplate().getMessageRouting().forEach(e -> {
            e.broadcast(recvr -> recvr.sendMessage(msg));
        });
    }
}
