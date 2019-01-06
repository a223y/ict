package org.iota.ict.ixi;

import org.iota.ict.Ict;
import org.iota.ict.IctTestTemplate;
import org.iota.ict.model.Transaction;
import org.iota.ict.model.TransactionBuilder;
import org.iota.ict.network.event.GossipEvent;
import org.iota.ict.network.event.GossipListener;
import org.iota.ict.utils.Trytes;
import org.junit.*;

import java.util.Set;

public class IxiTest extends IctTestTemplate {

    @Test
    public void testIxi() {

        // given
        Ict ict = createIct();
        TestIxiModule module = new TestIxiModule(new IctProxy(ict));
        new Thread(module).run();
        safeSleep(100);

        // then
        Assert.assertNotNull("IXI module did not receive event.", module.gossipEvent);
        Assert.assertEquals("Event received by IXI module contains wrong transaction", module.transaction, module.gossipEvent.getTransaction());
        Assert.assertNotNull("Ict did not store transaction submitted by IXI module.", ict.getTangle().findTransactionByHash(module.transaction.hash));
        Assert.assertTrue("IXI module can't query transaction from tangle.", module.findTransactionsByAddress(module.transaction.address).contains(module.transaction));
    }

    private static void safeSleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
        }
    }
}

class TestIxiModule extends IxiModule {

    Transaction transaction = createTransaction();
    GossipEvent gossipEvent = null;

    public TestIxiModule(Ixi ixi) {
        super(ixi);
        ixi.addGossipListener(new GossipListener() {
            @Override
            public void onGossipEvent(GossipEvent event) {
                gossipEvent = event;
            }
        });
    }

    @Override
    public void run() {

        // when
        ixi.submit(transaction);
    }

    Set<Transaction> findTransactionsByAddress(String address) {
        return ixi.findTransactionsByAddress(address);
    }

    private static Transaction createTransaction() {
        TransactionBuilder builder = new TransactionBuilder();
        builder.address = Trytes.randomSequenceOfLength(Transaction.Field.ADDRESS.tryteLength);
        return builder.build();
    }
}
