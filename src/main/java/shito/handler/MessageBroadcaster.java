package shito.handler;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import shito.api.data.ShitoMessage;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MessageBroadcaster {
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onPushEvent(ShitoMessage message) {
        message.getTemplate().getMessageRouting().forEach(e -> {
          //  var addition = new HashMap<String,Object>();
            //addition.put()
            e.broadcast(recvr -> recvr.sendMessage(message.render(Collections.emptyMap())));
        });
    }
}
