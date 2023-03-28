go to "File" -> "Project Structure"
click libraries -> "+" add -> from Maven -> org.loadui:testFx:3.1.2 -> "apply"
repeat for:
1) org.hamcrest:hamcrest-junit:2.0.0.0
2) junit:junit:4.13.2
3) org.testfx:testfx-core:4.0.16-alpha
4) org.testfx:testfx-junit:4.0.15-alpha

add configurations
create new junit configuration
change name and class to UITest
change -ea to --add-exports javafx.graphics/com.sun.javafx.application=ALL-UNNAMED