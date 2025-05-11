#!/bin/bash

# Create a backup directory
mkdir -p lib_backup

# Move Jakarta JSTL JARs to backup
echo "Backing up Jakarta JSTL libraries..."
mv lib/jakarta.*.jar lib_backup/ 2>/dev/null || true

# Download correct JSTL libraries for Java EE 8 / Servlet 4.0
echo "Downloading correct JSTL libraries for Java EE 8..."
wget -nc https://repo1.maven.org/maven2/javax/servlet/jstl/1.2/jstl-1.2.jar -P lib/

echo "JSTL libraries updated to Javax version successfully!"
