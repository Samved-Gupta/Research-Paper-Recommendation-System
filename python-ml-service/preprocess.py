import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
import joblib
import sqlite3
import os

print("Starting pre-processing...")

# --- 1. Load the CSV Data ---
csv_path = 'papers_data.csv'
if not os.path.exists(csv_path):
    print(f"ERROR: {csv_path} not found. Please place it in the same directory.")
    exit()

print("Reading CSV file...")
papers_df = pd.read_csv(csv_path)
papers_df['text_content'] = papers_df['text_content'].fillna('')

# Ensure paper_index is the DataFrame's index
if 'paper_index' in papers_df.columns:
    papers_df = papers_df.set_index('paper_index', drop=False)
print("CSV data loaded successfully.")


# --- 2. Build and Save the TF-IDF Model ---
print("Building TF-IDF model...")
vectorizer = TfidfVectorizer(max_features=20000, stop_words='english')
tfidf_matrix = vectorizer.fit_transform(papers_df['text_content'])
print("Model built successfully.")

# Save the vectorizer and the matrix to files
joblib.dump(vectorizer, 'tfidf_vectorizer.joblib')
joblib.dump(tfidf_matrix, 'tfidf_matrix.joblib')
print("TF-IDF vectorizer and matrix have been saved to files.")


# --- 3. Convert CSV to SQLite Database ---
db_path = 'papers.db'
print(f"Creating SQLite database at {db_path}...")
conn = sqlite3.connect(db_path)

# Save the pandas DataFrame to a table named 'papers' in the SQLite DB
papers_df.to_sql('papers', conn, if_exists='replace', index=True, index_label='paper_index_pk')
conn.close()
print("SQLite database created and data imported successfully.")
print("\nPre-processing complete!")