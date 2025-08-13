import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
import joblib
import sqlite3
print("Starting pre-processing...")
papers_df = pd.read_csv('papers_data.csv')
papers_df['text_content'] = papers_df['text_content'].fillna('')
if 'paper_index' in papers_df.columns:
    papers_df = papers_df.set_index('paper_index', drop=False)
print("Building and saving TF-IDF model...")
vectorizer = TfidfVectorizer(max_features=20000, stop_words='english')
tfidf_matrix = vectorizer.fit_transform(papers_df['text_content'])
joblib.dump(vectorizer, 'tfidf_vectorizer.joblib')
joblib.dump(tfidf_matrix, 'tfidf_matrix.joblib')
print("Creating SQLite database...")
conn = sqlite3.connect('papers.db')
papers_df.to_sql('papers', conn, if_exists='replace', index=True, index_label='paper_index_pk')
conn.close()
print("Pre-processing complete!")