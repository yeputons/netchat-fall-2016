package net.yeputons.spbau.fall2016.netchat

import com.natpryce.hamkrest.*
import com.natpryce.hamkrest.assertion.*
import com.nhaarman.mockito_kotlin.*
import io.grpc.stub.StreamObserver
import net.ldvsoft.spbau.messenger.protocol.P2PMessenger
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.*

class ChatControllerTest {
    val mockWriter = mock<StreamObserver<P2PMessenger.Message>>()
    val controller = ChatController()

    @Before fun setUp() {
        controller.writer = mockWriter
    }

    @Test fun testChangeMyName() {
        val listener = mock<ChatControllerListener>()
        controller.addChatControllerListener(listener)

        whenever(listener.onMyNameChanged())
                .then { assertEquals(controller.myName, "hello1") }
        controller.myName = "hello1"
        verify(listener, times(1)).onMyNameChanged()
        assertEquals("hello1", controller.myName)

        whenever(listener.onMyNameChanged())
                .then { assertEquals(controller.myName, "hello2") }
        controller.myName = "hello2"
        verify(listener, times(2)).onMyNameChanged()
        assertEquals("hello2", controller.myName)

        verifyNoMoreInteractions(listener)
    }

    @Test fun testMultipleListenersAddRemove() {
        val listener1 = mock<ChatControllerListener>()
        val listener2 = mock<ChatControllerListener>()

        controller.addChatControllerListener(listener1)
        controller.myName = "hello1"
        verify(listener1, times(1)).onMyNameChanged()
        verify(listener2, times(0)).onMyNameChanged()

        controller.addChatControllerListener(listener2)
        controller.myName = "hello2"
        verify(listener1, times(2)).onMyNameChanged()
        verify(listener2, times(1)).onMyNameChanged()

        controller.removeChatControllerListener(listener1)
        controller.myName = "hello3"
        verify(listener1, times(2)).onMyNameChanged()
        verify(listener2, times(2)).onMyNameChanged()
    }

    @Test fun testHandlePeerInfo() {
        val listener = mock<ChatControllerListener>()
        controller.addChatControllerListener(listener)
        whenever(listener.onOtherNameChanged())
                .then { assertEquals(controller.otherName, "hello") }

        controller.handle(
                P2PMessenger.PeerInfo.newBuilder()
                        .setName("hello")
                        .build()
        )

        verify(listener, times(1)).onOtherNameChanged()
        verifyNoMoreInteractions(listener)
    }

    @Test fun testHandleStartedTyping() {
        assertFalse(controller.otherIsTyping)

        val listener = mock<ChatControllerListener>()
        controller.addChatControllerListener(listener)
        whenever(listener.onOtherIsTypingChanged())
                .then { assertTrue(controller.otherIsTyping) }

        controller.handle(
                P2PMessenger.StartedTyping.newBuilder()
                        .setDate(0)
                        .build()
        )
        verify(listener, times(1)).onOtherIsTypingChanged()

        controller.handle(
                P2PMessenger.StartedTyping.newBuilder()
                        .setDate(0)
                        .build()
        )

        verify(listener, times(2)).onOtherIsTypingChanged()

        verifyNoMoreInteractions(listener)
    }

    @Test fun testHandleNewMessageAndTyping() {
        val msg1 = P2PMessenger.TextMessage.newBuilder()
                .setText("Hello1")
                .setDate(1)
                .build()
        val msg2 = P2PMessenger.TextMessage.newBuilder()
                .setText("Hello2")
                .setDate(2)
                .build()
        val listener = mock<ChatControllerListener>()
        controller.handle(
                P2PMessenger.PeerInfo.newBuilder()
                        .setName("OTHER")
                        .build()
        )
        assertEquals("OTHER", controller.otherName)
        controller.addChatControllerListener(listener)

        controller.handle(P2PMessenger.StartedTyping.newBuilder().setDate(0).build())
        verify(listener, times(1)).onOtherIsTypingChanged()
        assertTrue(controller.otherIsTyping)

        whenever(listener.onOtherIsTypingChanged())
                .then { assertFalse(controller.otherIsTyping) }
        controller.handle(msg1)
        verify(listener, times(1)).onNewMessage(ChatMessage("OTHER", "Hello1", ProtobufHelper.intToDate(1)))
        assertFalse(controller.otherIsTyping)

        controller.handle(msg2)
        verify(listener, times(1)).onNewMessage(ChatMessage("OTHER", "Hello2", ProtobufHelper.intToDate(2)))
        assertFalse(controller.otherIsTyping)

        verify(listener, atLeast(1)).onOtherIsTypingChanged()
        verifyNoMoreInteractions(listener)
    }

    @Test fun testSendMessage() {
        val listener = mock<ChatControllerListener>()
        controller.myName = "ME"
        reset(mockWriter)
        controller.addChatControllerListener(listener)

        val startDate = Date()
        whenever(listener.onNewMessage(any()))
                .then { msg ->
                    val msg = msg.getArgument<ChatMessage>(0)
                    assertEquals("ME", msg.author)
                    assertEquals("Hello1", msg.text)
                    assertThat(msg.date, greaterThanOrEqualTo(startDate))
                }
        whenever(mockWriter.onNext(any()))
                .then { msg ->
                    val msg = msg.getArgument<P2PMessenger.Message>(0)
                    assertEquals(P2PMessenger.Message.BodyCase.TEXTMESSAGE, msg.bodyCase)
                    val textMsg = msg.textMessage
                    assertNotNull(textMsg)
                    assertEquals("Hello1", textMsg.text)
                    assertThat(textMsg.date, greaterThanOrEqualTo(ProtobufHelper.dateToInt(startDate)))
                }
        controller.sendMessage("Hello1")
        verify(listener, times(1)).onNewMessage(any())
        verify(mockWriter, times(1)).onNext(any())
        assertFalse(controller.otherIsTyping)

        verifyNoMoreInteractions(listener)
    }

    @Test fun testStartTyping() {
        val listener = mock<ChatControllerListener>()
        reset(mockWriter)
        controller.addChatControllerListener(listener)

        val startDate = Date()
        whenever(mockWriter.onNext(any()))
                .then { msg ->
                    val msg = msg.getArgument<P2PMessenger.Message>(0)
                    assertEquals(P2PMessenger.Message.BodyCase.STARTEDTYPING, msg.bodyCase)
                    val startedTypingMsg = msg.startedTyping
                    assertNotNull(startedTypingMsg)
                    assertThat(startedTypingMsg.date, greaterThanOrEqualTo(ProtobufHelper.dateToInt(startDate)))
                }
        controller.startTyping()
        verify(mockWriter, times(1)).onNext(any())
        assertFalse(controller.otherIsTyping)

        verifyNoMoreInteractions(listener)
    }

    @Test fun testPeerInfo() {
        val listener = mock<ChatControllerListener>()
        reset(mockWriter)
        controller.addChatControllerListener(listener)

        whenever(mockWriter.onNext(any()))
                .then { msg ->
                    val msg = msg.getArgument<P2PMessenger.Message>(0)
                    assertEquals(P2PMessenger.Message.BodyCase.PEERINFO, msg.bodyCase)
                    val peerInfoMsg = msg.peerInfo
                    assertNotNull(peerInfoMsg)
                    assertEquals("HELLO", peerInfoMsg.name)
                }
        controller.myName = "HELLO"
        verify(mockWriter, times(1)).onNext(any())

        verifyNoMoreInteractions(mockWriter)
    }
}
