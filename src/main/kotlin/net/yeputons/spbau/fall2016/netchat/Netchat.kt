package net.yeputons.spbau.fall2016.netchat

import java.awt.BorderLayout
import java.awt.Font
import javax.swing.JFrame
import javax.swing.JLabel

fun main(args: Array<String>) {
    val config = ChatConfigurationDialog.showConfigurationDialog()
    if (config == null) {
        System.exit(1)
        return
    }

    val waitingFrame = JFrame("Netchat waiting")
    val waitingLabel = JLabel("Waiting for connection...")
    waitingLabel.font = Font("Arial", Font.PLAIN, 30)
    waitingFrame.add(waitingLabel, BorderLayout.CENTER)
    waitingFrame.pack()
    waitingFrame.isResizable = false
    waitingFrame.isVisible = true

    val controller = ChatControllerFactory.connect(config)
    val panel = ChatPanel(controller)
    waitingFrame.isVisible = false

    val frame = JFrame("Netchat")
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.add(panel)
    frame.pack()
    frame.isVisible = true
}