# Research Paper Recommendation System 🔬

A full-stack web application designed to help users discover relevant academic papers. This system provides content-based and behavior-based suggestions tailored to user interests and reading history.

## ✨ Features

- **Secure User Authentication**: Sign up and log in with Email/Password or social providers (Google & GitHub).
- **Full Password Management**: Includes secure "Forgot Password" email flow and "Change Password" functionality.
- **Guest Mode**: Allows anonymous users to search and explore papers without creating an account.
- **ML-Powered Search**: Search through a large dataset of papers using a Python-based machine learning service.
- **Personalized Recommendations**: Logged-in users receive paper recommendations on their dashboard based on their viewing history.
- **Similar Paper Discovery**: Find papers similar to a specific search result.
- **Bookmarking**: Users can save papers to a persistent "Saved Papers" list.
- **Viewing History**: Automatically tracks all papers a user views.
- **List Management**: Users can remove items from their Saved Papers and History lists.
- **Efficient Pagination**: All lists (Search, Saved, History) are paginated to handle large amounts of data smoothly.

## 🛠️ Tech Stack

- **Backend**:
  - Java 21
  - Spring Boot 3
  - Spring Security (for JWT & OAuth2)
  - Spring Data JPA (Hibernate)
- **Machine Learning Service**:
  - Python 3
  - FastAPI
  - Pandas
  - Scikit-learn (for TF-IDF and Cosine Similarity)
- **Frontend**:
  - HTML5
  - Tailwind CSS
  - Vanilla JavaScript (using Fetch API)
- **Database**:
  - MySQL


 
## 📂 Project Structure

The project is organized as a monorepo containing the three main services: 
├── 📁 backend/              # Java Spring Boot API
├── 📁 frontend/             # HTML, CSS, and JS files
├── 📁 python-ml-service/    # FastAPI ML Service
└── 📄 README.md

## 🚀 Getting Started

Follow these instructions to get a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

You will need the following software installed on your machine:
- Java JDK 21+
- Apache Maven
- Python 3.10+
- MySQL Server
- Git

### Setup & Installation

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/Samved-Gupta/Research-Paper-Recommendation-System.git
    cd Research-Paper-Recommendation-System
    ```

2.  **Database Setup:**
    - Make sure your MySQL server is running.
    - Connect to your MySQL instance and run the queries in the `SQL_Queries.txt` file. This will create the `recommender_db` database and all the necessary tables.

3.  **Backend Setup (`/backend`):**
    - Navigate to the `backend` directory.
    - You will need to create an `application.properties` file inside `src/main/resources/`.
    - Copy the following template and fill in your own credentials. **Do not commit your actual credentials to Git.**
      ```properties
      # --- Database Configuration ---
      spring.datasource.url=jdbc:mysql://localhost:3306/recommender_db
      spring.datasource.username=YOUR_MYSQL_USERNAME
      spring.datasource.password=YOUR_MYSQL_PASSWORD
      spring.jpa.hibernate.ddl-auto=update

      # --- Email Configuration (for 'Forgot Password') ---
      spring.mail.host=smtp.gmail.com
      spring.mail.port=587
      spring.mail.username=YOUR_GMAIL_ADDRESS@gmail.com
      spring.mail.password=YOUR_16_DIGIT_GMAIL_APP_PASSWORD
      spring.mail.properties.mail.smtp.auth=true
      spring.mail.properties.mail.smtp.starttls.enable=true

      # --- OAuth2 Credentials ---
      spring.security.oauth2.client.registration.google.client-id=YOUR_GOOGLE_CLIENT_ID
      spring.security.oauth2.client.registration.google.client-secret=YOUR_GOOGLE_CLIENT_SECRET
      spring.security.oauth2.client.registration.github.client-id=YOUR_GITHUB_CLIENT_ID
      spring.security.oauth2.client.registration.github.client-secret=YOUR_GITHUB_CLIENT_SECRET
      ```

4.  **ML Service Setup (`/python-ml-service`):**
    - Navigate to the `python-ml-service` directory.
    - Create a Python virtual environment:
      ```bash
      python -m venv venv
      source venv/bin/activate  # On Windows: venv\Scripts\activate
      ```
    - Install the required dependencies:
      ```bash
      pip install -r requirements.txt
      ```
    - Make sure the `papers_data.csv` file is present in this directory.

5.  **Frontend Setup (`/frontend`):**
    - The recommended way to serve the frontend is using the **Live Server** extension in Visual Studio Code.

### Running the Application

You must start the services in the correct order:

1.  **Start your MySQL Database.**
2.  **Start the Python ML Service:**
    - Open a terminal, navigate to `/python-ml-service`, and run:
      ```bash
      uvicorn main:app --reload
      ```
    - The service will be available at `http://122.0.0.1:8000`.
3.  **Start the Spring Boot Backend:**
    - Open the `/backend` project in your IDE (like IntelliJ) and run the `BackendApplication`.
    - The API will be available at `http://127.0.0.1:8080`.
4.  **Start the Frontend:**
    - Open the `/frontend` folder in VS Code.
    - Right-click on `login.html` and choose "Open with Live Server".
    - Your browser will open to an address like `http://127.0.0.1:5500`.
