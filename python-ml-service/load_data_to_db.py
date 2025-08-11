
import pandas as pd
from sqlalchemy import create_engine
db_user = "root"
db_password = "" # Your URL-encoded password
db_host = "localhost"
db_port = "3306"
db_name = "recommender_db"
# ------------------------------------

try:
    papers_df = pd.read_csv('papers_data.csv')
    papers_df.rename(columns={'id': 'paper_id'}, inplace=True)
    
    # --- NEW: Add the index as a new column ---
    papers_df.reset_index(inplace=True)
    papers_df.rename(columns={'index': 'paper_index'}, inplace=True)
    # -----------------------------------------

    print(f"Successfully read and processed {len(papers_df)} rows from papers_data.csv")

    connection_string = f"mysql+pymysql://{db_user}:{db_password}@{db_host}:{db_port}/{db_name}"
    engine = create_engine(connection_string)
    print("Successfully created database engine.")

    print("Loading data into the 'papers' table...")
    # Use 'append' since the SQL script creates the table.
    papers_df.to_sql('papers', con=engine, if_exists='append', index=False)
    print("✅ Data loaded successfully into the 'papers' table!")

except Exception as e:
    print(f"An error occurred: {e}")
