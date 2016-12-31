package net.yeputons.spbau.fall2016.netchat

import javax.swing.JOptionPane

public object ChatConfigurationDialog {
    fun showConfigurationDialog(): ChatConfiguration? {
        val choices = arrayOf("Server", "Client")

        val name = JOptionPane.showInputDialog(null, "Please enter your name:", "Netchat configuration", JOptionPane.QUESTION_MESSAGE) ?: return null

        val selected = JOptionPane.showInputDialog(null, "Please choose your role:", "Netchat configuration", JOptionPane.QUESTION_MESSAGE, null, choices, null) ?: return null
        if (selected == "Server") {
            return configureServer(name)
        } else if (selected == "Client") {
            return configureClient(name)
        } else {
            throw IllegalStateException("JOptionPane.showInputDialog returned something strange: $selected")
        }
    }

    private fun configureServer(name: String): ChatConfiguration? {
        val port = requestPort() ?: return null
        return ChatServerConfiguration(name = name, port = port)
    }

    private fun configureClient(name: String): ChatConfiguration? {
        val host = JOptionPane.showInputDialog(null, "Please enter host to connect to:", "Netchat configuration", JOptionPane.QUESTION_MESSAGE) ?: return null
        val port = requestPort() ?: return null
        return ChatClientConfiguration(name = name, host = host, port = port)
    }

    private fun requestPort(): Int? {
        val port = JOptionPane.showInputDialog(null, "Please enter port number:", "Netchat configuration", JOptionPane.QUESTION_MESSAGE) ?: return null
        try {
            return port.toInt()
        } catch (e: NumberFormatException) {
            JOptionPane.showMessageDialog(null, "Invalid port number: '${port}'", "Netchat configuration", JOptionPane.ERROR_MESSAGE)
            return null
        }
    }
}