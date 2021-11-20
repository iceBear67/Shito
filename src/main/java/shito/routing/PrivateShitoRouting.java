package shito.routing;

import cc.sfclub.core.Core;
import cc.sfclub.transform.Contact;
import cc.sfclub.transform.Receiver;
import shito.api.data.ShitoRoute;

import java.util.function.Consumer;

public class PrivateShitoRouting implements ShitoRoute {
    private final String platId;
    private final long uid;
    private transient Contact contact;

    public PrivateShitoRouting(String platId, long uid) {
        this.platId = platId;
        this.uid = uid;
        contact=Core.get().bot(platId).orElseThrow().getContact(uid).orElseThrow();
    }

    @Override
    public void broadcast(Consumer<? super Receiver> groupConsumer) {
        if (contact != null) {
            groupConsumer.accept(contact);
        }
    }

    @Override
    public String toString() {
        if(contact==null)return "Invalidated Contact";
        return "User "+contact.getNickname()+" ("+contact.getID()+")";
    }
}
