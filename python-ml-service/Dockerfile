# 1. Use an official Python runtime as a parent image
FROM python:3.11-slim

# 2. Set the working directory in the container
WORKDIR /app

# 3. Copy the requirements file
COPY python-ml-service/requirements.txt .

# 4. Install the Python dependencies
RUN pip install --no-cache-dir -r requirements.txt

# 5. Copy the pre-processed model and database files
COPY python-ml-service/tfidf_vectorizer.joblib .
COPY python-ml-service/tfidf_matrix.joblib .
COPY python-ml-service/papers.db .

# 6. Copy the application's source code
COPY python-ml-service/main.py .

# 7. Command to run the application
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]