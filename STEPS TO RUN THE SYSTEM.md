# Municipality Motor Vehicle System - Execution Guide

This guide explains how to set up and run the project on a new computer.

## 1. Prerequisites
Ensure the following software is installed:
*   **Java JDK 21** (e.g., Eclipse Adoptium)
*   **Apache Maven**
*   **Node.js & npm** (for the frontend)
*   **PostgreSQL 16+**
*   **WildFly 39.0.0.Final**

## 2. PostgreSQL Setup
1.  Open your PostgreSQL (e.g., pgAdmin or psql).
2.  Ensure local connection is available.
3.  The project currently expects PostgreSQL to be on **Port 5434** (as per your current setup).
    *   *Note: If the new PC uses the default 5432, you will need to update `persistence.xml` in the backend.*

## 3. WildFly Setup
1.  Download and unzip **WildFly 39.0.0.Final**.
2.  Open `run-system.bat` in a text editor.
3.  Update these two lines to match the new PC:
    ```batch
    SET "JAVA_HOME=C:\Path\To\Your\JDK"
    SET "WF_HOME=C:\Path\To\Your\WildFly"
    ```

## 4. Running the Project
Follow these steps in order:

### **A. Start Backend & WildFly**
1.  Navigate to the main project folder.
2.  Run **`run-system.bat`**.
3.  This script will automatically:
    *   Rebuild the backend (Maven).
    *   Deploy the `.war` file to WildFly.
    *   Start the WildFly server.
4.  Wait until you see: `WildFly ... started in ...ms`.

### **B. Start Frontend**
1.  Open a **new** terminal window.
2.  Navigate to the `frontend` folder.
3.  Run:
    ```powershell
    npm install
    npm start
    ```

---

## 5. Accessing the System
Open your browser and go to:
*   **URL**: `http://localhost:4200`
*   **Database Seeding**: The first time the backend starts, it will automatically fill the database with Districts, Zones, and Vehicle Types.
*   **Security Notification**: The backend uses an HTTPS port (8444) for internal communication. If you see CORS errors, ensure WildFly is configured to allow requests from `localhost:4200`.

### **Test Credentials**
| Role | Username | Password |
| :--- | :--- | :--- |
| **Admin** | `Essa` | `1234` |
| **Citizen** | `umair` | `1234` |
| **Police** | `John` | `1234` |

---
**Authors**: Essa Janneh & Umair Syed Anwar
