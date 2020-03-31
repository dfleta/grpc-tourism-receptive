package org.elsmancs.grpc;

interface GuestDispatcher {

    void dispatch(String cardOwner, String cardNumber) throws Exception;

}