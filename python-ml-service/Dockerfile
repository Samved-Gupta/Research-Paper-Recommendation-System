# 1. Start with a lean official Python base image
FROM python:3.11-slim

# 2. Set the working directory inside the container
WORKDIR /app

# 3. Copy the requirements file first to leverage Docker's layer caching
COPY requirements.txt .

# 4. Install the Python dependencies
# Install large libraries individually to conserve memory
RUN pip install --no-cache-dir numpy
RUN pip install --no-cache-dir pandas
RUN pip install --no-cache-dir scipy
RUN pip install --no-cache-dir scikit-learn

# Now, install the rest of the packages from the requirements file
RUN pip install --no-cache-dir -r requirements.txt

# 5. Copy the rest of the application code, including the CSV data file
COPY . .

# 6. Expose the port the app runs on
EXPOSE 8000

# 7. The command to run when the container starts
#    --host 0.0.0.0 is crucial to make it accessible from outside the container
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]