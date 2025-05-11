#!/bin/bash
# Master script to migrate from Jakarta EE to Java EE/Javax

echo "Starting migration from Jakarta EE to Java EE/Javax..."

# Step 1: Update Java imports
echo "Step 1: Updating Java imports..."
bash update_java_imports.sh

# Step 2: Update JSP taglibs
echo "Step 2: Updating JSP taglib URIs..."
bash update_jsps.sh

# Step 3: Update JSTL dependencies
echo "Step 3: Updating JSTL dependencies..."
bash fix-jstl-deps.sh

# Step 4: Update JSP dependencies
echo "Step 4: Updating JSP dependencies..."
bash fix-jsp-deps.sh

# Step 5: Clean and rebuild the project
echo "Step 5: Cleaning and rebuilding the project..."
ant clean
ant build
ant war

echo "Migration complete! Please test your application to ensure everything works correctly."
