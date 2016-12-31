package net.yeputons.spbau.fall2016.netchat

import io.grpc.ManagedChannelBuilder
import io.grpc.ServerBuilder
import io.grpc.stub.StreamObserver
import net.ldvsoft.spbau.messenger.protocol.MessengerGrpc
import net.ldvsoft.spbau.messenger.protocol.P2PMessenger
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture

object ChatControllerFactory {
    val LOG = LoggerFactory.getLogger(ChatControllerFactory::class.java)

    fun connect(configuration: ChatConfiguration): ChatController {
        LOG.info("connect($configuration)")
        when (configuration) {
            is ChatServerConfiguration -> return connectToServer(configuration)
            is ChatClientConfiguration -> return connectToClient(configuration)
            else -> throw IllegalArgumentException("Unknown subclass of ChatConfiguration")
        }
    }

    private fun connectToServer(configuration: ChatServerConfiguration): ChatController {
        val controllerFuture = CompletableFuture<ChatController>()
        val server = ServerBuilder.forPort(configuration.port)
                .addService(object : MessengerGrpc.MessengerImplBase() {
                    override fun chat(responseObserver: StreamObserver<P2PMessenger.Message>?): StreamObserver<P2PMessenger.Message> {
                        val controller = ChatController()
                        controller.myName = configuration.name
                        controller.writer = responseObserver
                        controllerFuture.complete(controller)
                        return controller
                    }
                })
                .build()
        LOG.info("Starting server on port ${configuration.port}")
        server.start()
        LOG.info("Waiting for client")
        val controller = controllerFuture.get()
        LOG.info("Client connected")
        return controller
    }

    private fun connectToClient(configuration: ChatClientConfiguration): ChatController {
        LOG.info("Connecting to server at ${configuration.host}:${configuration.port}")
        val channel = ManagedChannelBuilder.forAddress(configuration.host, configuration.port)
                .usePlaintext(true) // Disable TLS
                .build()
        val stub = MessengerGrpc.newStub(channel)
        LOG.info("Connected to server")
        val controller = ChatController()
        controller.myName = configuration.name
        controller.writer = stub.chat(controller)
        return controller
    }
}