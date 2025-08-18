import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
import joblib
import sqlite3
import os

print("Starting pre-processing...")

csv_path = 'papers_data.csv'
if not os.path.exists(csv_path):
    print(f"ERROR: {csv_path} not found. Please place it in the same directory.")
    exit()

print("Reading CSV file...")
papers_df = pd.read_csv(csv_path)
papers_df['text_content'] = papers_df['text_content'].fillna('')

if 'paper_index' in papers_df.columns:
    papers_df = papers_df.set_index('paper_index', drop=False)
print("CSV data loaded successfully.")

print("Building TF-IDF model...")
vectorizer = TfidfVectorizer(max_features=20000, stop_words='english')
tfidf_matrix = vectorizer.fit_transform(papers_df['text_content'])
print("Model built successfully.")

joblib.dump(vectorizer, 'tfidf_vectorizer.joblib')
joblib.dump(tfidf_matrix, 'tfidf_matrix.joblib')
print("TF-IDF vectorizer and matrix have been saved to files.")

db_path = 'papers.db'
print(f"Creating SQLite database at {db_path}...")
conn = sqlite3.connect(db_path)
papers_df.to_sql('papers', conn, if_exists='replace', index=True, index_label='paper_index_pk')
conn.close()
print("SQLite database created and data imported successfully.")
print("\nPre-processing complete!")