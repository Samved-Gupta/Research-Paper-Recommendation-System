#!/bin/sh

# The path is now updated to look inside the "js" folder
sed -i "s|%%API_BASE_URL%%|${API_BASE_URL}|g" /usr/share/nginx/html/js/app.js

# Start the NGINX server
nginx -g 'daemon off;'