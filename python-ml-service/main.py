from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
import joblib
import numpy as np
from pydantic import BaseModel
from typing import List
import math
import sqlite3

app = FastAPI(title="Research Paper Recommender ML Service")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)

class UserHistory(BaseModel):
    paper_indices: List[int]

try:
    print("INFO:     Loading pre-processed model assets...")
    vectorizer = joblib.load('tfidf_vectorizer.joblib')
    tfidf_matrix = joblib.load('tfidf_matrix.joblib')
    db_path = 'papers.db'
    print("INFO:     Model assets loaded successfully.")
except Exception as e:
    print(f"ERROR:    Could not load model assets. Make sure you have run preprocess.py. Error: {e}")
    exit()

def get_db_connection():
    conn = sqlite3.connect(db_path)
    conn.row_factory = sqlite3.Row
    return conn

def execute_query(query, params=()):
    conn = get_db_connection()
    cursor = conn.cursor()
    cursor.execute(query, params)
    rows = cursor.fetchall()
    conn.close()
    return [dict(row) for row in rows]

@app.get("/search/")
def search_papers(query: str, page: int = 0, size: int = 10):
    offset = page * size
    search_query = f"%{query}%"
    
    count_query = "SELECT COUNT(*) FROM papers WHERE text_content LIKE ?"
    conn = get_db_connection()
    total_items = conn.cursor().execute(count_query, (search_query,)).fetchone()[0]
    conn.close()
    
    if total_items == 0:
        return {"content": [], "totalPages": 0, "number": page}

    total_pages = math.ceil(total_items / size)
    
    content_query = "SELECT paper_id as id, paper_index as 'index', text_content FROM papers WHERE text_content LIKE ? LIMIT ? OFFSET ?"
    content_list = execute_query(content_query, (search_query, size, offset))
    
    return {"content": content_list, "totalPages": total_pages, "number": page}

@app.get("/recommend/")
def get_recommendations(paper_index: int):
    try:
        cosine_scores = np.dot(tfidf_matrix[paper_index], tfidf_matrix.T).toarray().flatten()
        similar_indices = cosine_scores.argsort()[-2:-12:-1]
        
        placeholders = ', '.join('?' for _ in similar_indices)
        query = f"SELECT paper_id as id, paper_index as 'index', text_content FROM papers WHERE paper_index IN ({placeholders})"
        
        recommendations = execute_query(query, [int(i) for i in similar_indices])
        return recommendations
    except IndexError:
        raise HTTPException(status_code=404, detail="Paper index out of bounds.")

@app.post("/recommend/personalized/")
def get_personalized_recommendations(user_history: UserHistory):
    if not user_history.paper_indices:
        raise HTTPException(status_code=400, detail="User history cannot be empty.")
    try:
        history_vectors = tfidf_matrix[user_history.paper_indices]
        user_profile_vector = np.mean(history_vectors, axis=0)
        
        cosine_scores = np.dot(user_profile_vector, tfidf_matrix.T).toarray().flatten()
        
        similar_indices = cosine_scores.argsort()[-20:][::-1]
        recommended_indices = [idx for idx in similar_indices if idx not in user_history.paper_indices][:10]
        
        placeholders = ', '.join('?' for _ in recommended_indices)
        query = f"SELECT paper_id as id, paper_index as 'index', text_content FROM papers WHERE paper_index IN ({placeholders})"
        
        recommendations = execute_query(query, [int(i) for i in recommended_indices])
        return recommendations
    except IndexError:
        raise HTTPException(status_code=404, detail="One or more paper indices are out of bounds.")