# Use a lightweight, official NGINX image as the base
FROM nginx:alpine

# Copy all your HTML, CSS, and JS files into the default web server directory
COPY . /usr/share/nginx/html

# Copy the special startup script into the container
COPY entrypoint.sh /entrypoint.sh

# Make that script executable so the container can run it
RUN chmod +x /entrypoint.sh

# Tell the container to run this script when it starts.
# The script will inject the backend URL and then start the NGINX server.
CMD ["/entrypoint.sh"]