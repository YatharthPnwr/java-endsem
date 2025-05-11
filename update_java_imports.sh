#!/bin/bash
# Script to replace Jakarta imports with Javax in Java files

echo "Replacing Jakarta imports with Javax imports in Java files..."

# Find all Java files and replace jakarta.servlet with javax.servlet
find src/main/java -name "*.java" -type f -exec sed -i 's/import jakarta.servlet/import javax.servlet/g' {} \;

echo "All Java imports updated from jakarta.* to javax.*"