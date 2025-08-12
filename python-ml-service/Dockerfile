# 1. Use an official Python runtime as a parent image
FROM python:3.11-slim

# 2. Set the working directory in the container
WORKDIR /app

# 3. Copy the requirements file *from the correct subfolder*
COPY python-ml-service/requirements.txt .

# 4. Install the Python dependencies (using the memory-saving technique)
RUN pip install --no-cache-dir numpy
RUN pip install --no-cache-dir pandas
RUN pip install --no-cache-dir scipy
RUN pip install --no-cache-dir scikit-learn
RUN pip install --no-cache-dir -r requirements.txt

# 5. Copy the ML service's source code *from the correct subfolder*
COPY python-ml-service/ .

# 6. Command to run the application
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]