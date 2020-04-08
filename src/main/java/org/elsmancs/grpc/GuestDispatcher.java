package org.elsmancs.grpc;

public interface GuestDispatcher {

    void dispatch(String cardOwner, String cardNumber) throws Exception;

}