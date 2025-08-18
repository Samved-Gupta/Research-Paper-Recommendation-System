# main.py

from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
import joblib
import numpy as np
from pydantic import BaseModel
from typing import List
import os
from sqlalchemy import create_engine, text
from dotenv import load_dotenv

load_dotenv()

app = FastAPI(title="Research Paper Recommender ML Service")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)

class UserHistory(BaseModel):
    paper_indices: List[int]

DATABASE_URL = os.getenv("MYSQL_URL")
if not DATABASE_URL:
    raise RuntimeError("MYSQL_URL environment variable is not set!")

engine = create_engine(DATABASE_URL)

try:
    print("INFO:     Loading TF-IDF vectorizer...")
    vectorizer = joblib.load('tfidf_vectorizer.joblib')
    print("INFO:     Vectorizer loaded successfully.")
except Exception as e:
    print(f"ERROR:    Could not load tfidf_vectorizer.joblib. Error: {e}")
    exit()

def execute_query(query, params={}):
    with engine.connect() as connection:
        result = connection.execute(text(query), params)
        rows = result.fetchall()
        return [dict(row._mapping) for row in rows]

@app.get("/search/")
def search_papers(query: str, page: int = 0, size: int = 10):
    offset = page * size
    search_query = f"%{query}%"

    count_query = "SELECT COUNT(*) as count FROM papers WHERE text_content LIKE :search_query"
    total_items = execute_query(count_query, {"search_query": search_query})[0]['count']

    if total_items == 0:
        return {"content": [], "totalPages": 0, "number": page}

    total_pages = (total_items + size - 1) // size

    content_query = "SELECT paper_id as id, paper_index as 'index', text_content FROM papers WHERE text_content LIKE :search_query LIMIT :size OFFSET :offset"
    content_list = execute_query(content_query, {"search_query": search_query, "size": size, "offset": offset})

    return {"content": content_list, "totalPages": total_pages, "number": page}

@app.post("/recommend/personalized/")
def get_personalized_recommendations(user_history: UserHistory):
    if not user_history.paper_indices:
        raise HTTPException(status_code=400, detail="User history cannot be empty.")

    try:
        placeholders = ', '.join(f":idx_{i}" for i in range(len(user_history.paper_indices)))
        params = {f"idx_{i}": index for i, index in enumerate(user_history.paper_indices)}

        query = f"SELECT text_content FROM papers WHERE paper_index IN ({placeholders})"
        history_papers = execute_query(query, params)

        if not history_papers:
            raise HTTPException(status_code=404, detail="Paper indices not found.")

        history_texts = [p['text_content'] for p in history_papers]
        history_vectors = vectorizer.transform(history_texts)
        user_profile_vector = np.mean(history_vectors, axis=0)

        all_papers_query = "SELECT paper_index, text_content FROM papers"
        all_papers = execute_query(all_papers_query)
        all_indices = [p['paper_index'] for p in all_papers]
        all_texts = [p['text_content'] for p in all_papers]
        all_tfidf_matrix = vectorizer.transform(all_texts)

        cosine_scores = np.dot(user_profile_vector, all_tfidf_matrix.T).flatten()

        similar_indices_sorted = np.argsort(cosine_scores)[::-1]
        recommended_indices = []
        for idx in similar_indices_sorted:
            if all_indices[idx] not in user_history.paper_indices:
                recommended_indices.append(all_indices[idx])
            if len(recommended_indices) == 10:
                break

        rec_placeholders = ', '.join(f":idx_{i}" for i in range(len(recommended_indices)))
        rec_params = {f"idx_{i}": index for i, index in enumerate(recommended_indices)}

        query_recs = f"SELECT paper_id as id, paper_index as 'index', text_content FROM papers WHERE paper_index IN ({rec_placeholders})"
        recommendations = execute_query(query_recs, rec_params)

        return recommendations
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"An internal error occurred: {str(e)}")