FROM python:3.11-slim
WORKDIR /app
COPY python-ml-service/requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt
COPY python-ml-service/tfidf_vectorizer.joblib .
COPY python-ml-service/tfidf_matrix.joblib .
COPY python-ml-service/papers.db .
COPY python-ml-service/main.py .
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]