package net.yeputons.spbau.fall2016.netchat

import javax.swing.JFrame

fun main(args: Array<String>) {
    val config = ChatClientConfiguration("Netchat user", "localhost", 12345)
    val controller = ChatControllerFactory.connect(config)
    val panel = ChatPanel(controller)

    val frame = JFrame("Netchat")
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.add(panel)
    frame.pack()
    frame.isVisible = true
}