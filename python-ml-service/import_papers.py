import csv
import mysql.connector
import os

# --- Database Connection Details ---
# Replace with your actual details if they are different.
db_config = {
    'host': '127.0.0.1',
    'port': 3306, # This is the correct port
    'user': 'root',
    'password': 'Soul@000',
    'database': 'recommender_db'
}


# --- CSV File Details ---
csv_file_path = 'papers_data.csv' # Make sure this file is in the same directory

def import_csv_to_mysql():
    """
    Reads data from a CSV file and inserts it into the 'papers' table.
    """
    if not os.path.exists(csv_file_path):
        print(f"Error: The file '{csv_file_path}' was not found.")
        return

    try:
        # Establish a connection to the database
        conn = mysql.connector.connect(**db_config)
        cursor = conn.cursor()
        print("Successfully connected to the database.")

        # Open the CSV file
        with open(csv_file_path, mode='r', encoding='utf-8') as csvfile:
            csv_reader = csv.reader(csvfile)
            
            # Skip the header row
            header = next(csv_reader)
            print(f"Skipping header: {header}")

            # Prepare the SQL query for inserting data
            sql = "INSERT INTO papers (paper_id, text_content, paper_index) VALUES (%s, %s, %s)"

            # Iterate over each row in the csv file
            rows_imported = 0
            for row in csv_reader:
                try:
                    # Assuming CSV columns are in order: paper_id, text_content, paper_index
                    cursor.execute(sql, tuple(row))
                    rows_imported += 1
                except mysql.connector.Error as err:
                    print(f"Error inserting row: {row}. Error: {err}")

            # Commit the transaction
            conn.commit()
            print(f"Import complete. Successfully imported {rows_imported} rows.")

    except mysql.connector.Error as err:
        print(f"Database connection error: {err}")
    finally:
        # Close the connection
        if 'conn' in locals() and conn.is_connected():
            cursor.close()
            conn.close()
            print("Database connection closed.")

if __name__ == '__main__':
    import_csv_to_mysql()