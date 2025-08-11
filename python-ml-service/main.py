from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import linear_kernel
import numpy as np
from pydantic import BaseModel
from typing import List
import math

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
    print("INFO:     Loading paper data from CSV...")
    papers_df = pd.read_csv('papers_data.csv')
    papers_df['text_content'] = papers_df['text_content'].fillna('')
    # Ensure the DataFrame index matches the paper_index column if it exists ---
    if 'paper_index' in papers_df.columns:
        papers_df = papers_df.set_index('paper_index', drop=False)
    print("INFO:     Paper data loaded successfully.")

    print("INFO:     Building TF-IDF model from scratch...")
    vectorizer = TfidfVectorizer(max_features=20000, stop_words='english')
    tfidf_matrix = vectorizer.fit_transform(papers_df['text_content'])
    
    print("INFO:     TF-IDF model built successfully.")
    print("INFO:     Model assets are ready.")

except FileNotFoundError as e:
    print(f"ERROR:    Could not load papers_data.csv. Make sure the file is in the same folder.")
    exit()
except Exception as e:
    print(f"ERROR:    An error occurred during model building: {e}")
    exit()

@app.get("/search/")
def search_papers(query: str, page: int = 0, size: int = 10):
    matches_df = papers_df[papers_df['text_content'].str.contains(query, case=False, regex=False)]
    total_items = len(matches_df)

    if total_items == 0:
        return {
            "content": [], "totalPages": 0, "number": page, "size": size, "first": True, "last": True
        }
        
    total_pages = math.ceil(total_items / size)
    start_index = page * size
    end_index = start_index + size
    paginated_df = matches_df.iloc[start_index:end_index]
    paginated_df = paginated_df.rename(columns={'paper_id': 'id', 'paper_index': 'index'})

    content_list = paginated_df.reset_index().to_dict('records')
    
    return {
        "content": content_list, "totalPages": total_pages, "number": page, "size": size, "first": page == 0, "last": page >= total_pages - 1
    }

@app.get("/recommend/")
def get_recommendations(paper_index: int):
    try:
        idx = paper_index
        cosine_scores = linear_kernel(tfidf_matrix[idx], tfidf_matrix).flatten()
        similar_indices = cosine_scores.argsort()[-2:-12:-1]
        recommendations = papers_df.iloc[similar_indices]
        recommendations = recommendations.rename(columns={'paper_id': 'id', 'paper_index': 'index'})

        return recommendations.reset_index().to_dict('records')
    except IndexError:
        raise HTTPException(status_code=404, detail="Paper index out of bounds.")

@app.post("/recommend/personalized/")
def get_personalized_recommendations(user_history: UserHistory):
    if not user_history.paper_indices:
        raise HTTPException(status_code=400, detail="User history cannot be empty.")
    try:
        history_vectors = tfidf_matrix[user_history.paper_indices]
        user_profile_vector = np.mean(history_vectors.toarray(), axis=0)
        cosine_scores = linear_kernel(user_profile_vector.reshape(1, -1), tfidf_matrix).flatten()
        similar_indices = cosine_scores.argsort()[-10:][::-1]
        recommended_indices = [idx for idx in similar_indices if idx not in user_history.paper_indices]
        recommendations = papers_df.iloc[recommended_indices]
        recommendations = recommendations.rename(columns={'paper_id': 'id', 'paper_index': 'index'})
        
        return recommendations.reset_index().to_dict('records')
    except IndexError:
        raise HTTPException(status_code=404, detail="One or more paper indices are out of bounds.")
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"An error occurred: {e}")