package net.yeputons.spbau.fall2016.netchat

import javax.swing.JFrame
import javax.swing.JOptionPane

fun main(args: Array<String>) {
    val config = ChatConfigurationDialog.showConfigurationDialog()
    if (config == null) {
        System.exit(1)
        return
    }

    val controller = ChatControllerFactory.connect(config)
    val panel = ChatPanel(controller)

    val frame = JFrame("Netchat")
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.add(panel)
    frame.pack()
    frame.isVisible = true
}