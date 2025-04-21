#!/bin/bash
cd ~/Desktop/FinanceApp
javac --module-path ~/Desktop/javafx-sdk-17.0.15/lib --add-modules javafx.controls Main.java Expense.java Login.java
java --module-path ~/Desktop/javafx-sdk-17.0.15/lib --add-modules javafx.controls Main
