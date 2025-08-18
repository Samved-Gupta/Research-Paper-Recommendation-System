import pandas as pd

original_csv_path = 'papers_data.csv' 
new_csv_path = 'papers_data_small.csv'

# Change this number to 25000
num_rows_to_keep = 25000

print(f"Reading the original file: {original_csv_path}...")
try:
    df = pd.read_csv(original_csv_path, nrows=num_rows_to_keep)
    df.to_csv(new_csv_path, index=False)

    print(f"Successfully created a smaller file with {len(df)} rows at: {new_csv_path}")

except FileNotFoundError:
    print(f"ERROR: The original file '{original_csv_path}' was not found.")