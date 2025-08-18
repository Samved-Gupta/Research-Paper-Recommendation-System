# load_data_to_db.py (FINAL VERSION)

import pandas as pd
from sqlalchemy import create_engine, text
import os
from dotenv import load_dotenv

# Load environment variables from .env file
load_dotenv()

connection_string = os.getenv("MYSQL_URL")

if not connection_string:
    print("Error: MYSQL_URL not found. Make sure it is set in your .env file.")
    exit()

try:
    papers_df = pd.read_csv('papers_data.csv')
    papers_df.rename(columns={'id': 'paper_id'}, inplace=True)
    papers_df.reset_index(inplace=True)
    papers_df.rename(columns={'index': 'paper_index'}, inplace=True)

    print(f"Successfully read and processed {len(papers_df)} rows from papers_data.csv")

    engine = create_engine(connection_string)
    print("Successfully created database engine for Railway.")

    with engine.connect() as connection:
        # --- START OF CHANGE ---
        # Step 1: Temporarily disable foreign key checks to allow truncation
        print("Disabling foreign key checks...")
        connection.execute(text("SET FOREIGN_KEY_CHECKS = 0;"))

        # Step 2: Truncate the table to delete all existing rows without dropping the table
        print("Truncating the 'papers' table...")
        connection.execute(text("TRUNCATE TABLE papers;"))

        # Step 3: Re-enable foreign key checks
        print("Re-enabling foreign key checks...")
        connection.execute(text("SET FOREIGN_KEY_CHECKS = 1;"))

        # Commit the transaction for the truncate command
        connection.commit()
        # --- END OF CHANGE ---

    print("Loading data into the 'papers' table... (This may take a few minutes)")
    # Use if_exists='append' since we have already cleared the table
    papers_df.to_sql('papers', con=engine, if_exists='append', index=False, chunksize=1000)

    print("✅ Data loaded successfully into the 'papers' table on Railway!")

except FileNotFoundError:
    print("Error: 'papers_data.csv' not found. Please ensure it's in the same directory.")
except Exception as e:
    print(f"An error occurred: {e}")