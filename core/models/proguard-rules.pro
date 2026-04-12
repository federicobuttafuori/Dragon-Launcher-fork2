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

# Do not mignify any Workspace-related stuff dur to boot crashes
-keep class org.elnix.dragonlauncher.common.serializables.WorkspaceState { *; }
-keep class org.elnix.dragonlauncher.common.serializables.Workspace { *; }
-keep class org.elnix.dragonlauncher.common.serializables.AppOverride { *; }

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep enum * { *; }


# Gson essentials
-keepattributes Signature
-keepattributes *Annotation*


# Keep adapters
-keep class org.elnix.dragonlauncher.common.serializables.SwipeActionAdapter { *; }
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Keep all @Serializable classes and their members
-keep @kotlinx.serialization.Serializable class * { *; }
-keepclassmembers @kotlinx.serialization.Serializable class * { *; }

# Keep serialization metadata
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault
-keep class kotlinx.serialization.** { *; }
-keep class kotlin.reflect.** { *; }
