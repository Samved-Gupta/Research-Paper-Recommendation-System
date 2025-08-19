# 1. Use an official Python runtime as a parent image
FROM python:3.11-slim

# 2. Set the working directory in the container
WORKDIR /app

# 3. Copy the requirements file and install dependencies
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# 4. Copy the pre-processed model files and database
COPY tfidf_vectorizer.joblib .
COPY tfidf_matrix.joblib .
COPY papers.db .

# 5. Copy the application's source code
COPY main.py .

# 6. Expose the port the app runs on
EXPOSE 8000

# 7. Define the command to run the app
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]