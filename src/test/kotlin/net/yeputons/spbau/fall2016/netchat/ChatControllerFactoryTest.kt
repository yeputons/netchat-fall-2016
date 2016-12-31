package net.yeputons.spbau.fall2016.netchat

import com.nhaarman.mockito_kotlin.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.slf4j.LoggerFactory
import java.net.ServerSocket
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CountDownLatch

class ChatControllerFactoryTest {
    companion object {
        val LOG = LoggerFactory.getLogger(ChatControllerFactoryTest::class.java)
    }

    var port = -1

    @Before fun setUp() {
        ServerSocket(0).use {
            port = it.localPort
        }
        LOG.info("Will use port $port")
    }

    @Test fun testTwoWayMessage() {
        val clientFuture = CompletableFuture<ChatController>()
        Thread(object : Runnable {
            override fun run() {
                LOG.debug("Creating client")
                clientFuture.complete(ChatControllerFactory.connect(ChatClientConfiguration("user-client", "localhost", port)))
                LOG.debug("Client created")
            }
        }).start()
        LOG.debug("Creating server")
        val server = ChatControllerFactory.connect(ChatServerConfiguration("user-server", port))
        LOG.debug("Server created")
        val client = clientFuture.get()

        val serverListener = mock<ChatControllerListener>()
        val clientListener = mock<ChatControllerListener>()
        server.addChatControllerListener(serverListener)
        client.addChatControllerListener(clientListener)

        val latch1 = CountDownLatch(1)
        whenever(serverListener.onNewMessage(any()))
                .then { msg -> assertEquals("Hello1", msg.getArgument<ChatMessage>(0).text) }
        whenever(clientListener.onNewMessage(any()))
                .then { msg -> assertEquals("Hello1", msg.getArgument<ChatMessage>(0).text); latch1.countDown() }
        server.sendMessage("Hello1")
        verify(serverListener, times(1)).onNewMessage(any())
        latch1.await()
        verify(clientListener, times(1)).onNewMessage(any())

        val latch2 = CountDownLatch(1)
        whenever(clientListener.onNewMessage(any()))
                .then { msg -> assertEquals("Hello2", msg.getArgument<ChatMessage>(0).text) }
        whenever(serverListener.onNewMessage(any()))
                .then { msg -> assertEquals("Hello2", msg.getArgument<ChatMessage>(0).text); latch2.countDown() }
        client.sendMessage("Hello2")
        verify(clientListener, times(2)).onNewMessage(any())
        latch2.await()
        verify(serverListener, times(2)).onNewMessage(any())
    }
}