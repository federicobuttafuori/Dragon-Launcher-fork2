package org.elnix.dragonlauncher.models

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.shizuku.OutputLine
import org.elnix.dragonlauncher.shizuku.ShellCommandExecutor
import org.elnix.dragonlauncher.shizuku.ShizukuPermissionHandler

class ShizukuViewModel(
    private val shellCommandExecutor: ShellCommandExecutor,
    private val shizukuPermissionHandler: ShizukuPermissionHandler,
    private val coroutineScope: CoroutineScope

) {

    private val _output = MutableStateFlow<OutputLine?>(null)
    val outputValue = _output.asStateFlow()

    fun clearOutput() {
        _output.value = null
    }

//    fun hasShizukuPermission(): Boolean {
//        return shizukuPermissionHandler.hasPermission()
//    }

    fun shizukuPermissionState(): StateFlow<Boolean> {
        return shizukuPermissionHandler.permissionGranted
    }

//    fun refreshShizukuPermission() {
//        return shizukuPermissionHandler.refreshPermissionState()
//    }

    fun requestShizukuPermission() {
        return shizukuPermissionHandler.requestPermission()
    }

    fun executeShizukuCommand(command: String) {
        coroutineScope.launch {
            shellCommandExecutor.runShizuku(command)
                .collect { outputLine ->
                    _output.value = outputLine
                }
        }
    }

//    fun stopCommand() {
//        return shellCommandExecutor.stop()
//    }
}