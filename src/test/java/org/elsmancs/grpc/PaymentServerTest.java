/*
 * Copyright 2016 The gRPC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.elsmancs.grpc;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.elsmancs.grpc.PaymentServer.PaymentService;

import io.grpc.ManagedChannel;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Unit tests for {@link PaymentServer}. For demonstrating how to write gRPC
 * unit test only. Not intended to provide a high code coverage or to test every
 * major usecase.
 *
 * directExecutor() makes it easier to have deterministic tests. However, if
 * your implementation uses another thread and uses streaming it is better to
 * use the default executor, to avoid hitting bug #3084.
 *
 * <p>
 * For more unit test examples see
 * {@link io.grpc.examples.routeguide.RouteGuideClientTest} and
 * {@link io.grpc.examples.routeguide.RouteGuideServerTest}.
 */

@RunWith(JUnit4.class)
public class PaymentServerTest {
    /**
     * This rule manages automatic graceful shutdown for the registered servers and
     * channels at the end of test.
     */
    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    private PaymentServer server;
    private ManagedChannel inProcessChannel;

    @Before
    public void setUp() throws Exception {
        // Generate a unique in-process server name.
        String serverName = InProcessServerBuilder.generateName();

        // Use directExecutor for both InProcessServerBuilder and
        // InProcessChannelBuilder can reduce the
        // usage timeouts and latches in test. But we still add timeout and latches
        // where they would be
        // needed if no directExecutor were used, just for demo purpose.

        // Create a server, add service, start, and register for automatic graceful
        // shutdown.
        int port = 50061;
        server = new PaymentServer(InProcessServerBuilder.forName(serverName).directExecutor(),
                                    port);

        server.start();

        // Create a client channel and register for automatic graceful shutdown.
        inProcessChannel = grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build());
    }

    @After
    public void tearDown() throws Exception {
        server.stop();
    }

    /**
     * To test the server, make calls with a real stub using the in-process channel,
     * and verify behaviors or state changes from the client side.
     */
    @Test
    public void paymentService_replyMessage() throws Exception {

        PaymentGrpc.PaymentBlockingStub blockingStub = PaymentGrpc.newBlockingStub(inProcessChannel);

        /**
         * Procesa el cargo porque existe credito suficiente
         */
        Processed reply = blockingStub
                .pay(CreditCard.newBuilder().setOwner("Rick").setNumber("1111").setCharge(500).build());

        assertTrue(reply.getIsProcessed());
        // assertEquals("Hello test name", reply.getMessage());

        /**
         * No procesa el cargo porque no existe credito suficiente
         */
        reply = blockingStub.pay(CreditCard.newBuilder().setOwner("Rick").setNumber("1111").setCharge(3000).build());

        assertFalse(reply.getIsProcessed());
    }
}
