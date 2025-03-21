package com.example.listascolaborativas.model

object LifecycleLogger {
    private val logs = mutableListOf<String>()

    fun addLog(fragmentName: String, method: String) {
        logs.add("$fragmentName - $method")
    }

    fun getLogs(): List<String> {
        return logs
    }

    fun clearLogs() {
        logs.clear()
    }

}

