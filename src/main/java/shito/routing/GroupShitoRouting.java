package shito.routing;

import cc.sfclub.core.Core;
import cc.sfclub.transform.ChatGroup;
import cc.sfclub.transform.Receiver;
import shito.api.data.ShitoRoute;

import java.util.function.Consumer;

public class GroupShitoRouting implements ShitoRoute {
    private final long groupId;
    private final String platId;
    private transient ChatGroup chatGroup ;

    public GroupShitoRouting(long groupId, String platId) {
        this.groupId = groupId;
        this.platId = platId;
        chatGroup=Core.get().bot(platId).orElseThrow().getGroup(groupId).orElseThrow();
    }

    @Override
    public void broadcast(Consumer<? super Receiver> groupConsumer) {
        if (chatGroup != null) groupConsumer.accept(chatGroup);
    }

    @Override
    public void init() {
        chatGroup=Core.get().bot(platId).orElseThrow().getGroup(groupId).orElseThrow();
    }

    @Override
    public String toString() {
        if(chatGroup==null)return "Invalidated Group";
        return "Group "+chatGroup.getName()+" ("+chatGroup.getID()+")";
    }
}
