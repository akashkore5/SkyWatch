# PDF Management System
[![Try Now](https://img.shields.io/badge/Try%20Now-Click%20Here-blue)](https://pdfmanagmentsystem-production.up.railway.app/)

PDF Management & Collaboration System

## Features

### 1. Introduction
The PDF Management & Collaboration System is a web application designed to facilitate the seamless management and collaboration of PDFs. The system enables users to sign up, upload PDFs, share them with other users, and collaborate through comments. This PRD outlines the features, functionality, and specifications of the application.

### 2. User Signup and Authentication
- Users can create an account by providing essential information such as name, email address, and password.
- Authentication mechanisms have been implemented to ensure secure access to the application.

### 3. File Upload
- Authenticated users can upload a PDF file to the system.
- The PDF files are securely stored and accessible only to authorized users.
- The application validates the uploaded files to ensure they are in PDFA format.

### 4. Dashboard
- Users can search for PDF files based on file names.
- Clicking on a file takes them to a specific PDF file and displays all the comments.

### 5. File Sharing
- Users have the ability to share PDF files with others.
- Sharing is done by generating a unique link.
- An email notification is sent to the recipient when a PDF is shared, providing them with access to the shared file.

### 6. Invited User File Access and Commenting
- Invited users can access shared PDF files through their invite link.
- The system provides a user-friendly interface to view the PDF files.
- Invited users can add comments related to the PDF file on a sidebar.

### 7. Security and Data Privacy
- Access controls are in place to ensure that only authorized users can access PDF files and comments.
- User passwords are securely hashed and stored.

### 8. User Interface and Design
- The application has an intuitive and user-friendly interface.
- Responsive design is implemented to support various devices and screen sizes.
- The UI provides clear navigation, a PDF file preview, and easy-to-use commenting features.

### 9. Future Enhancements
- Identify potential future enhancements that can be considered beyond the scope of this PRD.

## Good Features

### 1. User Signup and Authentication
- User authentication supports features like password reset and account recovery.

### 2. File Sharing
- In addition to generating a unique link, an email notification is sent to the recipient when a PDF is shared.

### 3. Invited User File Access and Commenting
- Users can reply to existing comments.
- The system supports basic text formatting options (bold, italic, bullet points) for comments.

## Technical Details

PDF Management System created using Spring Boot Java and MySQL. The application is deployed on Railway App. We have implemented the following features:

- User signup and authentication with secure access.
- File upload functionality with PDFA format validation.
- Dashboard with search functionality and displaying comments.
- File sharing with a unique link and email notification to the recipient.
- Invited users can access shared files and add comments.
- Secure access controls and password hashing.
- User-friendly interface with responsive design.
- Potential future enhancements can be explored beyond the scope of this PRD.

## Try Now

Visit the following link to try out our application:

[PDF Management System](https://pdfmanagmentsystem-production.up.railway.app/)
