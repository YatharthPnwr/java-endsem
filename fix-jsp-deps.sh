#!/bin/bash

# Create a backup directory
mkdir -p lib_backup

# Download correct JSP API JAR for Java EE 8 / Servlet 4.0
echo "Downloading correct JSP API library for Java EE 8..."
wget -nc https://repo1.maven.org/maven2/javax/servlet/jsp/javax.servlet.jsp-api/2.3.3/javax.servlet.jsp-api-2.3.3.jar -P lib/

# Add tag libraries WAR files needed for TLD files
wget -nc https://repo1.maven.org/maven2/javax/servlet/jstl/1.2/jstl-1.2.jar -P lib/

# Ensure the libraries are correctly organized
echo "Making sure only one version of each library is used..."
mv lib/jakarta.*.jar lib_backup/ 2>/dev/null || true
mv lib/jakarta.servlet.jsp.jstl-api-2.0.0.jar lib_backup/ 2>/dev/null || true

echo "JSP and JSTL libraries updated to Java EE/Javax versions successfully!"
