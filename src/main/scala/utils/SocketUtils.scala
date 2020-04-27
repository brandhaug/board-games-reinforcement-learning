package utils

import java.io.{BufferedInputStream, BufferedReader, FileInputStream}
import java.security.KeyStore
import java.security.cert.{Certificate, CertificateFactory}

import javax.net.ssl.{SSLContext, SSLSocketFactory, TrustManagerFactory}

object SocketUtils {
  def nextLine(in: BufferedReader): String = {
    val chars = new Array[Char](1024)
    in.read(chars)
    chars.mkString("").trim
  }

  def getFactory: SSLSocketFactory = {
    val certificateFactory     = CertificateFactory.getInstance("X.509")
    val certificateInputStream = new FileInputStream("src/main/scala/server.crt")

    val certificateBufferedInputStream = new BufferedInputStream(certificateInputStream)
    var certificate: Certificate       = null
    try {
      certificate = certificateFactory.generateCertificate(certificateBufferedInputStream)
    } finally {
      certificateBufferedInputStream.close()
    }

    val keyStoreType = KeyStore.getDefaultType
    val keyStore     = KeyStore.getInstance(keyStoreType)
    keyStore.load(null, null)
    keyStore.setCertificateEntry("ca", certificate)

    val trustManagerFactoryAlgorithm = TrustManagerFactory.getDefaultAlgorithm
    val trustManagerFactory          = TrustManagerFactory.getInstance(trustManagerFactoryAlgorithm)
    trustManagerFactory.init(keyStore)

    val context = SSLContext.getInstance("TLS")
    context.init(null, trustManagerFactory.getTrustManagers, null)
    context.getSocketFactory
  }
}
