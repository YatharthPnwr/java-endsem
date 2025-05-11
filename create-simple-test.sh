#!/bin/bash

# Create a simple test WAR file to isolate the JSP issue

# Create directories
mkdir -p simple-test/WEB-INF

# Create a simple index.jsp
cat > simple-test/index.jsp << 'EOF'
<!DOCTYPE html>
<html>
<head>
    <title>Simple Test</title>
</head>
<body>
    <h1>Simple Test Page</h1>
    <p>This is a simple test page to check if JSP processing is working properly.</p>
    <p>Current time: <%= new java.util.Date() %></p>
</body>
</html>
EOF

# Create a minimal web.xml
cat > simple-test/WEB-INF/web.xml << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="https://jakarta.ee/xml/ns/jakartaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee 
         https://jakarta.ee/xml/ns/jakartaee/web-app_6_0.xsd"
         version="6.0">
    <display-name>Simple Test</display-name>
    <welcome-file-list>
        <welcome-file>index.jsp</welcome-file>
    </welcome-file-list>
</web-app>
EOF

# Create a correct glassfish-web.xml
cat > simple-test/WEB-INF/glassfish-web.xml << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE glassfish-web-app PUBLIC "-//GlassFish.org//DTD GlassFish Application Server 3.1 Servlet 3.0//EN" "http://glassfish.org/dtds/glassfish-web-app_3_0-1.dtd">
<glassfish-web-app>
    <context-root>/simple-test</context-root>
    <class-loader delegate="true"/>
</glassfish-web-app>
EOF

# Create the WAR file
cd simple-test
jar -cvf ../simple-test.war .
cd ..

echo "Created simple-test.war"
