package net.yeputons.spbau.fall2016.netchat

interface ChatConfiguration {}

data class ChatServerConfiguration(val name: String, val port: Int) : ChatConfiguration

data class ChatClientConfiguration(val name: String, val host: String, val port: Int) : ChatConfiguration
