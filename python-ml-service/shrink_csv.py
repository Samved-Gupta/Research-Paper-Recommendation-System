import pandas as pd

# The name of your large, original CSV file
original_csv_path = 'papers_data.csv' 

# The name for your new, smaller CSV file
new_csv_path = 'papers_data_small.csv'

# The number of rows you want in the new file
num_rows_to_keep = 50000

print(f"Reading the original file: {original_csv_path}...")
try:
    # Read only the first 50,000 rows from the original file
    df = pd.read_csv(original_csv_path, nrows=num_rows_to_keep)
    
    # Save the smaller DataFrame to a new file
    df.to_csv(new_csv_path, index=False)
    
    print(f"Successfully created a smaller file with {len(df)} rows at: {new_csv_path}")
    print("\nYou can now delete the original 'papers_data.csv' and rename 'papers_data_small.csv' to 'papers_data.csv'.")

except FileNotFoundError:
    print(f"ERROR: The original file '{original_csv_path}' was not found.")