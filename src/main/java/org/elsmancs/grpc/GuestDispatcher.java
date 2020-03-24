package org.elsmancs.grpc;

interface GuestDispatcher {

    void dispatch(Card card) throws Exception;

}