# 1. Use an official, lightweight Nginx base image
FROM nginx:1.25-alpine-slim

# 2. Copy all your HTML, CSS, and JS files into the default Nginx web root directory
COPY . /usr/share/nginx/html

# 3. Expose port 80, which is the default HTTP port that Nginx listens on
EXPOSE 80