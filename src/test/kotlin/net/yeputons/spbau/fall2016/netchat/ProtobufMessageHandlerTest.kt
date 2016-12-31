package net.yeputons.spbau.fall2016.netchat

import org.junit.Assert.*
import com.nhaarman.mockito_kotlin.*
import net.ldvsoft.spbau.messenger.protocol.P2PMessenger
import org.junit.Test
import org.mockito.Mockito

class ProtobufMessageHandlerTest {
    open class NullProtobufMessageHandler : ProtobufMessageHandler() {
        override fun handle(peerInfo: P2PMessenger.PeerInfo) {
        }

        override fun handle(startedTyping: P2PMessenger.StartedTyping) {
        }

        override fun handle(textMessage: P2PMessenger.TextMessage) {
        }

        override fun onCompleted() {
        }
    }

    @Test fun handleError() {
        val handler = spy(NullProtobufMessageHandler())

        handler.onError(null)

        verify(handler).onError(null)
        verifyNoMoreInteractions(handler)
    }

    @Test fun handleNullMessage() {
        val handler = spy(NullProtobufMessageHandler())

        handler.onNext(null)

        verify(handler).onNext(null)
        verifyNoMoreInteractions(handler)
    }

    @Test fun handlePeerInfo() {
        val handler = spy(NullProtobufMessageHandler())
        val inner =
                P2PMessenger.PeerInfo.newBuilder()
                        .setName("Hello")
                        .build()

        handler.onNext(P2PMessenger.Message.newBuilder().setPeerInfo(inner).build())

        verify(handler).onNext(Mockito.any())
        verify(handler, times(1)).handle(inner)
        verifyNoMoreInteractions(handler)
    }

    @Test fun handleStartedTyping() {
        val handler = spy(NullProtobufMessageHandler())
        val inner =
                P2PMessenger.StartedTyping.newBuilder()
                        .setDate(1234)
                        .build()

        handler.onNext(P2PMessenger.Message.newBuilder().setStartedTyping(inner).build())

        verify(handler).onNext(Mockito.any())
        verify(handler, times(1)).handle(inner)
        verifyNoMoreInteractions(handler)
    }

    @Test fun handleTextMessage() {
        val handler = spy(NullProtobufMessageHandler())
        val inner =
                P2PMessenger.TextMessage.newBuilder()
                        .setText("Hello")
                        .setDate(1234)
                        .build()

        handler.onNext(P2PMessenger.Message.newBuilder().setTextMessage(inner).build())

        verify(handler).onNext(Mockito.any())
        verify(handler, times(1)).handle(inner)
        verifyNoMoreInteractions(handler)
    }
}