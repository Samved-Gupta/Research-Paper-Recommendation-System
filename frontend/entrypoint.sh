#!/bin/sh

# Find the placeholder in app.js and replace it with the environment variable's value
sed -i "s|%%API_BASE_URL%%|${API_BASE_URL}|g" /usr/share/nginx/html/app.js

# Start the NGINX server
nginx -g 'daemon off;'