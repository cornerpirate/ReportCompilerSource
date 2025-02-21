# ReportCompiler

## Overview

ReportCompiler is a Java GUI application to import vulnerabilities from tools such as Nessus, Burp Scanner, etc, create findings and manage the risks.

## Compilation

Use ant to build the jar file and the documentation:

```sh
mvn package
```

## Usage

```sh
java -Xms128M -Xmx512M -jar target/ReportCompiler.jar
```

Tune the minimum and maximum Java heap parameters at will.

## GTK Look & Feel

On GNU/Linux systems, the following error may appear:

```
Exception in thread "main" java.lang.Error: Cannot load com.sun.java.swing.plaf.gtk.GTKLookAndFeel
    at javax.swing.UIManager.initializeDefaultLAF(UIManager.java:1351)
    at javax.swing.UIManager.initialize(UIManager.java:1459)
    at javax.swing.UIManager.maybeInitialize(UIManager.java:1426)
    at javax.swing.UIManager.getInstalledLookAndFeels(UIManager.java:419)
    at GUI.MainWindow.main(SourceFile:1756)
```

To fix it, just specify a different look and feel as argument to Java:

```sh
java -Xms128M -Xmx512M -jar target/ReportCompiler.jar -Dswing.defaultlaf=com.jtattoo.plaf.aero.AeroLookAndFeel
```

## Additional Java options

Additional arguments can be passed to  Java in order to improve the ReportCompiler experience:

- Use default system fonts: `-Dawt.useSystemAAFontSettings=on`
- Initial Java heap of 128MB: `-Xms 128M`
- Max Java heap of 512MB: `-Xmx512M`
