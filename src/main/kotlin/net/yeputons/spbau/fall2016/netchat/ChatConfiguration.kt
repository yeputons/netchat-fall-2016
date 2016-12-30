package net.yeputons.spbau.fall2016.netchat

interface ChatConfiguration {}

data class ChatServerConfiguration(val port: Int) : ChatConfiguration

data class ChatClientConfiguration(val host: String, val port: Int) : ChatConfiguration
