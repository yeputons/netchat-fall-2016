package net.yeputons.spbau.fall2016.netchat

import io.grpc.stub.StreamObserver
import net.ldvsoft.spbau.messenger.protocol.P2PMessenger
import org.slf4j.LoggerFactory

abstract class ProtobufMessageHandler : StreamObserver<P2PMessenger.Message> {
    companion object {
        val LOG = LoggerFactory.getLogger(ProtobufMessageHandler::class.java)
    }

    abstract fun handle(peerInfo: P2PMessenger.PeerInfo)

    abstract fun handle(startedTyping: P2PMessenger.StartedTyping)

    abstract fun handle(textMessage: P2PMessenger.TextMessage)

    override fun onNext(message: P2PMessenger.Message?) {
        if (message == null) {
            LOG.error("onNext(null) was called")
            return
        }
        when (message.bodyCase) {
            P2PMessenger.Message.BodyCase.PEERINFO -> {
                if (message.peerInfo == null) {
                    LOG.warn("Received invalid protobuf message: bodyCase is PeerInfo, but it's absent")
                    return
                }
                handle(message.peerInfo)
            }
            P2PMessenger.Message.BodyCase.STARTEDTYPING -> {
                if (message.startedTyping == null) {
                    LOG.warn("Received invalid protobuf message: bodyCase is StartedTyping, but it's absent")
                    return
                }
                handle(message.startedTyping)
            }
            P2PMessenger.Message.BodyCase.TEXTMESSAGE -> {
                if (message.textMessage == null) {
                    LOG.warn("Received invalid protobuf message: bodyCase is TextMessage, but it's absent")
                    return
                }
                handle(message.textMessage)
            }
            else ->
                LOG.warn("Received invalid protobuf message with body case = ${message.bodyCase}")
        }
    }

    override fun onError(error: Throwable?) {
        LOG.warn("onError()", error)
    }
}
