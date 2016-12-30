package net.yeputons.spbau.fall2016.netchat

import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.event.*
import javax.swing.*
import javax.swing.border.Border

class ChatPanel(val controller: ChatController) : JPanel() {
    val chat = JTextArea()
    val otherStatus = JLabel("${controller.otherName} is not typing")
    val myName = JLabel("${controller.myName}:")
    val message = JTextField()
    val submit = JButton("Submit")

    val chatControllerListener = object : ChatControllerListener {
        override fun onOtherNameChanged() {
            updateOtherStatus()
        }

        override fun onOtherIsTypingChanged() {
            updateOtherStatus()
        }

        override fun onNewMessage(message: ChatMessage) {
            chat.append("$message\n")
        }

        override fun onMyNameChanged() {
            myName.text = controller.myName + ":"
        }
    }

    fun updateOtherStatus() {
        if (controller.otherIsTyping == null) {
            otherStatus.text = "${controller.otherName} is not typing"
        } else {
            otherStatus.text = "${controller.otherName} is typing since ${controller.otherIsTyping}"
        }
    }

    fun sendMessage() {
        if (message.text != "") {
            controller.sendMessage(message.text)
            message.text = ""
        }
    }

    init {
        chat.isEditable = false
        chat.isFocusable = false
        controller.addChatControllerListener(chatControllerListener)

        message.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent?) {
                if (e!!.keyCode == KeyEvent.VK_ENTER) {
                    submit.doClick()
                } else if (message.text != "") {
                    controller.startTyping()
                }
            }
        })

        submit.addActionListener { sendMessage() }

        val line2 = Box.createHorizontalBox()
        line2.add(myName)
        line2.add(Box.createHorizontalGlue())
        line2.add(message)
        line2.add(Box.createHorizontalGlue())
        line2.add(submit)

        val bottom = Box.createVerticalBox()
        bottom.add(otherStatus)
        bottom.add(line2)

        layout = BorderLayout()
        add(chat, BorderLayout.CENTER)
        add(bottom, BorderLayout.SOUTH)

        setPreferredSize(Dimension(640, 480))
    }
}
