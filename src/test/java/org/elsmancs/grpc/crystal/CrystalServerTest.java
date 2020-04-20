
/**
 * Server Crystal logic testing.
 * Integration with service Crystal Dispenser 
 * business logic and data repo.
 */

package org.elsmancs.grpc.crystal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.elsmancs.grpc.CreditCard;
import org.elsmancs.grpc.Crystal;
import org.elsmancs.grpc.CrystalExpenderGrpc;
import org.elsmancs.grpc.Processed;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import io.grpc.ManagedChannel;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;

@RunWith(JUnit4.class)
public class CrystalServerTest {

    /**
     * From grpc documentation examples:
     * "This rule manages automatic graceful shutdown for the registered servers and
     * channels at the end of test.
     */
    @Rule
    public final GrpcCleanupRule  grpcCleanup = new GrpcCleanupRule();

    /**
     * The class "GrpcServerRule" (from "grpc-java/testing") 
     * is a JUnit TestRule that creates a InProcessServer and a ManagedChannel.
     */

    public CrystalServer server = null;
    public ManagedChannel inProcessChannel = null; 

    @Before
    public void setup() throws Exception {

        // Generate a unique in-process server name.
        String serverName = InProcessServerBuilder.generateName();

        // Create a server, add service, start, and register for automatic graceful
        // shutdown.
        int port = 50061;
        server = new CrystalServer(InProcessServerBuilder.forName(serverName).directExecutor(),
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
     * Service Dispatch responses units of crystal available.
     */
    @Test
    public void crystal_service_dispatch_responseMessage_crystal_avaliable() {

        CrystalExpenderGrpc.CrystalExpenderBlockingStub blockingStub = CrystalExpenderGrpc.newBlockingStub(inProcessChannel);

        CreditCard request = CreditCard.newBuilder()
                                        .setOwner("Rick")
                                        .setNumber("111111111111")
                                        .build();

        Crystal response = blockingStub.dispatch(request);
        assertEquals(1, response.getUnidades(), 0);
    }

    /**
     * Service Confirm dispenses crystal.
     */
    @Test
    public void crystal_service_confirm_responseMessage_OK() {

        CrystalExpenderGrpc.CrystalExpenderBlockingStub blockingStub = CrystalExpenderGrpc.newBlockingStub(inProcessChannel);
        
        Crystal request = Crystal.newBuilder()
                            .setUnidades(1)
                            .build();

        Processed response = blockingStub.confirm(request);
        assertTrue(response.getIsProcessed());
    }

    /**
     * Service Confirm can't find enough units of crystal.
     */
    @Test
    public void crystal_service_confirm_crystal_responseMessage_NotOK() {

        CrystalExpenderGrpc.CrystalExpenderBlockingStub blockingStub = CrystalExpenderGrpc.newBlockingStub(inProcessChannel);
        
        Crystal request = Crystal.newBuilder()
                            .setUnidades(1000)
                            .build();

        Processed response = blockingStub.confirm(request);
        assertFalse(response.getIsProcessed());
    }
}