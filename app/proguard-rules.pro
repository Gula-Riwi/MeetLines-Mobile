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
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile

# ==================== Retrofit & OkHttp ====================
# Retrofit does reflection on generic parameters. InnerClasses is required to use Signature.
-keepattributes Signature, InnerClasses, EnclosingMethod

# Retrofit does reflection on method and parameter annotations.
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Keep Retrofit interfaces
-keep,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**

# With R8 full mode, it sees no subtypes of Retrofit interfaces since they are created with a Proxy
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response

# With R8 full mode generic signature information for return types and parameters is removed
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# OkHttp platform used only on JVM and when Conscrypt and other security providers are available.
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

# ==================== Gson ====================
# Gson uses generic type information stored in a class file when working with fields.
-keepattributes Signature

# Gson specific classes
-dontwarn sun.misc.**

# Keep DTOs for Gson serialization/deserialization
-keep class com.meetline.app.data.model.** { *; }

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# ==================== Hilt / Dagger ====================
-keepnames @dagger.hilt.android.lifecycle.HiltViewModel class * extends androidx.lifecycle.ViewModel

# ==================== Domain Models ====================
# Keep domain models that might be used with reflection
-keep class com.meetline.app.domain.model.** { *; }

# ==================== Kotlin ====================
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**

# ==================== Coroutines ====================
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# ==================== AndroidX Security / Tink ====================
-keep class androidx.security.crypto.** { *; }
-dontwarn androidx.security.crypto.**

# Google Tink (used by EncryptedSharedPreferences)
-keep class com.google.crypto.tink.** { *; }
-dontwarn com.google.crypto.tink.**

# Google ErrorProne annotations (referenced by Tink)
-dontwarn com.google.errorprone.annotations.**

# ==================== Jetpack Compose ====================
# Keep all composable functions
-keepclassmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}

# Keep companion objects of composable functions
-keepclassmembers class * {
    @androidx.compose.runtime.Composable <methods>;
    companion object;
}

# Keep classes used by Compose previews
-keepclasseswithmembers class * {
    @androidx.compose.ui.tooling.preview.Preview <methods>;
}
