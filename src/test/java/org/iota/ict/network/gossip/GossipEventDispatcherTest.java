package org.iota.ict.network.gossip;

import org.iota.ict.Ict;
import org.iota.ict.IctTestTemplate;
import org.iota.ict.model.TransactionBuilder;
import org.junit.Assert;
import org.junit.Test;

public class GossipEventDispatcherTest extends IctTestTemplate {

    private boolean eventReceived;

    @Test
    public void testListenersCanNotBlock() {
        Ict ict = createIct();

        eventReceived = false;
        long start = System.currentTimeMillis();

        ict.addGossipListener(new GossipListener() {
            @Override
            public void onGossipEvent(GossipEvent e) {
                eventReceived = true;
                // try to block for a few second
                saveSleep(5000);
            }
        });

        ict.submit(new TransactionBuilder().build());
        saveSleep(100);
        Assert.assertTrue("gossip not delivered to gossip listener", eventReceived);

        long duration = System.currentTimeMillis() - start;
        Assert.assertTrue("gossip listener blocked main thread", duration < 2000);
    }
}