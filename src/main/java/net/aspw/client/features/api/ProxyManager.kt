package net.aspw.client.features.api

import io.netty.bootstrap.ChannelFactory
import io.netty.channel.socket.oio.OioSocketChannel
import java.net.InetSocketAddress
import java.net.Proxy
import java.net.Socket

object ProxyManager {
    var isEnable = false
    var proxy = "0.0.0.0:25565"
    var proxyType = Proxy.Type.SOCKS

    val proxyInstance: Proxy
        get() = proxy.split(":").let { Proxy(proxyType, InetSocketAddress(it.first(), it.last().toInt())) }

    class ProxyOioChannelFactory(proxy: Proxy) : ChannelFactory<OioSocketChannel> {
        private val proxy: Proxy

        init {
            this.proxy = proxy
        }

        override fun newChannel(): OioSocketChannel {
            return OioSocketChannel(Socket(this.proxy))
        }
    }
}