package shito.handler;

import cc.sfclub.events.MessageEvent;
import lombok.RequiredArgsConstructor;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import shito.session.ChatSession;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class SessionHandler {
    private final List<ChatSession> sessions; // reference from outer world.
    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onMessage(MessageEvent event){
        sessions.removeIf(e->e.test(event));
    }

}
