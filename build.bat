@echo off
javac main.java
jar -cfe canvas.jar main main.java main.class src org
java -jar canvas.jar
rem java main