# 💳 BankEase Project

👨‍💻 Developed by **Varadkar Siddhesh**

BankEase is a simple **banking management system** written in Java.  
It allows users to create accounts, log in, and perform basic banking operations securely with a MySQL database.

---

## 🚀 Features
- 🔑 User authentication (sign up & login)
- 💵 Deposit & withdrawal operations
- 📊 Balance checking
- 🗂️ Data stored in **MySQL database**
- 🛠️ Clean DAO pattern for database operations

---

## 🏗️ Project Structure
- BankEase-project/
- ├── src/
- │ ├── Main.java # Entry point of the program
- │ ├── BankApp.java # Core banking logic
- │ ├── DBConnection.java # Database connection handler
- │ └── UserDAO.java # Data Access Object for users
- │
- ├── README.md # Project documentation
- └── .gitignore # Git ignore file

---

## ⚙️ Requirements
- Java 8 or above ☕
- MySQL installed 🐬
- MySQL JDBC Driver

---

## 📝 Setup Instructions

1. **Clone the repository**
   ```bash
   git clone https://github.com/varadkarsiddhesh69-sys/BankEase-project.git
   cd BankEase-project
CREATE DATABASE bankdb;
USE bankdb;

CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE,
    password VARCHAR(100),
    balance DOUBLE
);
⚠️ Make sure to update the database name (bankdb) in your DBConnection.java.
