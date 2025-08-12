# Use a lightweight, official Nginx image
FROM nginx:stable-alpine

# Copy your frontend code from the 'frontend' subfolder in your repository
# into the directory that Nginx serves files from.
COPY frontend/ /usr/share/nginx/html

# Tell Docker that the container listens on port 80
EXPOSE 80

# The default command to start Nginx
CMD ["nginx", "-g", "daemon off;"]