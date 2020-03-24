package org.elsmancs.grpc;

import io.grpc.ManagedChannel;

interface GuestDispatcher {

    void dispatch(Card card, ManagedChannel channel) throws Exception;

}