package com.donaldwu.lunchpickerandroid.helper

import android.util.Log
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.*

class Helper {
    companion object {
        fun getIPAddress(useIPv4: Boolean): String {
            var ip = ""

            try {
                val interfaces: List<NetworkInterface> =
                    Collections.list(NetworkInterface.getNetworkInterfaces())
                for (intf in interfaces) {
                    val addrs: List<InetAddress> = Collections.list(intf.inetAddresses)
                    for (addr in addrs) {
                        if (!addr.isLoopbackAddress) {
                            val sAddr: String = addr.hostAddress

                            val isIPv4 = sAddr.indexOf(':') < 0
                            if (useIPv4) {
                                if (isIPv4)
                                    ip = sAddr
                            } else {
                                if (!isIPv4) {
                                    val delim = sAddr.indexOf('%')
                                    if (delim < 0)
                                        ip = sAddr.toUpperCase()
                                    else
                                        ip = sAddr.substring(0, delim).toUpperCase()
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.i("logger", "error = ${e.message}")
            }

            return ip
        }
    }
}