package net.yeputons.spbau.fall2016.netchat

import io.grpc.stub.StreamObserver
import net.ldvsoft.spbau.messenger.protocol.P2PMessenger
import org.slf4j.LoggerFactory
import java.util.*

class ChatController() : ProtobufMessageHandler() {
    companion object {
        val DEFAULT_NAME = "user"
        val LOG = LoggerFactory.getLogger(ChatController::class.java)
    }

    private val listeners = mutableSetOf<ChatControllerListener>()

    var myName: String = DEFAULT_NAME
        get() = field
        set(newMyName) {
            field = newMyName
            listeners.forEach { it.onMyNameChanged() }
        }

    var otherName: String = DEFAULT_NAME
        get() = field
        private set(newOtherName) {
            field = newOtherName
            listeners.forEach { it.onOtherNameChanged() }
        }

    var otherIsTyping: Boolean = false
        get() = field
        private set(newOtherIsTyping) {
            field = newOtherIsTyping
            listeners.forEach { it.onOtherIsTypingChanged() }
        }

    var writer: StreamObserver<P2PMessenger.Message>? = null

    fun addChatControllerListener(listener: ChatControllerListener) {
        listeners += listener
    }

    fun removeChatControllerListener(listener: ChatControllerListener) {
        listeners -= listener
    }

    fun startTyping() {
        LOG.debug("I started typing")
        val startedTypingMessage =
                P2PMessenger.StartedTyping.newBuilder()
                        .setDate(ProtobufHelper.dateToInt(Date()))
                        .build()
        writer!!.onNext(
                P2PMessenger.Message.newBuilder()
                        .setStartedTyping(startedTypingMessage)
                        .build()
        )
    }

    fun sendMessage(text: String) {
        val msg = ChatMessage(myName, text, Calendar.getInstance().time)
        LOG.debug("Sending message: $msg")
        val textMessage =
                P2PMessenger.TextMessage.newBuilder()
                        .setText(msg.text)
                        .setDate(ProtobufHelper.dateToInt(msg.date))
                        .build()
        writer!!.onNext(
                P2PMessenger.Message.newBuilder()
                        .setTextMessage(textMessage)
                        .build()
        )
        listeners.forEach { it.onNewMessage(msg) }
    }

    override fun handle(peerInfo: P2PMessenger.PeerInfo) {
        LOG.debug("Other changed name to ${peerInfo.name}")
        otherName = peerInfo.name
    }

    override fun handle(startedTyping: P2PMessenger.StartedTyping) {
        LOG.debug("Other started typing")
        otherIsTyping = true
    }

    override fun handle(textMessage: P2PMessenger.TextMessage) {
        val msg = ChatMessage(otherName, textMessage.text, ProtobufHelper.intToDate(textMessage.date))
        LOG.debug("Incoming text message: $msg")
        otherIsTyping = false
        listeners.forEach { it.onNewMessage(msg) }
    }

    override fun onCompleted() {
        LOG.info("onCompleted()")
        writer!!.onCompleted()
    }
}

data class ChatMessage(val author: String, val text: String, val date: Date)

interface ChatControllerListener {
    fun onMyNameChanged()
    fun onOtherNameChanged()
    fun onOtherIsTypingChanged()
    fun onNewMessage(message: ChatMessage)
}