# Keep demo app classes
-keep class org.calypsonet.keyple.demo.control.models.Location { *; }
-keep class org.joda.time.** { *; }

# Keep Keyple library classes
-keep class org.calypsonet.keyple.plugin.coppernic.ParagonReader { *; }

# Keep Coppernic SDK classes
-keep public class fr.coppernic.sdk.** { *; }