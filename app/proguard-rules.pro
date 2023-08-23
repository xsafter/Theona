# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keepattributes LineNumberTable,SourceFile
-renamesourcefileattribute SourceFile


-dontwarn java.beans.ConstructorProperties
-dontwarn java.beans.Transient
-dontwarn org.bouncycastle.jsse.BCSSLParameters
-dontwarn org.bouncycastle.jsse.BCSSLSocket
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
-dontwarn org.conscrypt.Conscrypt$Version
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.ConscryptHostnameVerifier
-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE

-dontwarn javax.naming.NamingEnumeration
-dontwarn javax.naming.NamingException
-dontwarn javax.naming.directory.Attribute
-dontwarn javax.naming.directory.Attributes
-dontwarn javax.naming.directory.DirContext
-dontwarn javax.naming.directory.InitialDirContext
-dontwarn javax.naming.directory.SearchControls
-dontwarn javax.naming.directory.SearchResult

-dontwarn com.mapbox.maps.plugin.animation.CameraAnimationsPluginImpl
-dontwarn com.mapbox.maps.plugin.animation.CameraAnimationsUtils
-dontwarn com.mapbox.maps.plugin.animation.animator.CameraAnimator
-dontwarn com.mapbox.maps.plugin.attribution.AttributionViewPlugin

# Needs because proto uses reflection that obfuscation is ruining
-keep class org.xmtp.proto.message.** { *; }
# Needs because encryption algorythm selection is probably based on reflection
-keep class org.xmtp.android.library.messages.** { *; }

#-keep class org.bouncycastle.jsse.** { *; }
-keep class org.bouncycastle.** { *; }

-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}
-keep,allowobfuscation @interface com.google.gson.annotations.SerializedName
