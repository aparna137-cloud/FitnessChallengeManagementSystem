# Proguard rules for Fitness Challenge Management System
-keepattributes *Annotation*
-keepclassmembers class * {
    @org.checkerframework.checker.nullness.qual.* <fields>;
}

# Keep MPAndroidChart
-keep class com.github.mikephil.charting.** { *; }

# Keep Firebase Models
-keep class com.example.fitnesschallenge.models.** { *; }
