package org.elsmancs.grpc;

interface GuestDispatcher {

    void dispatch(CreditCard card) throws Exception;

}