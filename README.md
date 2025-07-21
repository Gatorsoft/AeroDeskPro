# ✈️ AeroDesk Pro – Advanced Airport Management System

> A modular Java Swing desktop application for managing and simulating airport operations in real-time using MySQL, multithreading, and API integration.

![Java](https://img.shields.io/badge/Java-11+-blue)
![Platform](https://img.shields.io/badge/Platform-Java_SE_Desktop-green)
![Status](https://img.shields.io/badge/Status-Academic_Project-orange)
![MySQL](https://img.shields.io/badge/Database-MySQL-orange)
![License](https://img.shields.io/badge/License-Academic-lightgrey)

---

## 🎯 Hackathon Context

**AeroDesk Pro** was developed as part of a **24-hour academic hackathon** — _Hackathon 2024_, held on **July 19–20**.

The challenge was to design and implement a working prototype that addressed real-world airport management problems using desktop technologies.

In just 24 hours, the team built a Java-based application that:

- Simulates essential airport operations (flights, passengers, gates, baggage)
- Implements real-time data refresh with Java multithreading
- Integrates external APIs (e.g., weather or flight data)
- Uses MySQL for persistent storage
- Presents a functional Swing-based GUI

> 🧠 Built under pressure, designed with purpose — **AeroDesk Pro** is a complete academic prototype created during a rapid innovation sprint.

---

## 📘 Overview

**AeroDesk Pro** is a comprehensive Java SE desktop application that simulates essential airport operations through an intuitive GUI and a structured backend. It enables airport staff to efficiently handle passengers, flights, baggage, gate assignments, and real-time updates in a realistic airport environment.

The system demonstrates advanced software engineering principles and showcases:

- Object-oriented Java programming with design patterns
- Professional Java Swing GUI development
- Robust JDBC integration with MySQL database
- Multithreaded background processing for real-time operations
- HTTP API usage for external services (weather, flight information)
- Comprehensive file handling and exception management
- Scalable architecture with separation of concerns

---

## 🚀 Key Features

### Core Functionality
- 🛫 **Flight Scheduling and Real-Time Status Simulation** - Dynamic flight management with live updates
- 🧍 **Passenger Check-In Management** - Streamlined passenger processing and boarding pass generation
- 🛄 **Baggage Tracking and Handling** - Complete baggage lifecycle management from check-in to collection
- 🛬 **Gate Assignment and Availability Monitoring** - Intelligent gate allocation and real-time status tracking

### Advanced Features
- 🌐 **External API Integration** - Weather updates and flight information from third-party services
- 🔁 **Live UI Refresh with Multithreading** - Non-blocking background operations for smooth user experience
- 🔐 **Role-Based Access Control** - Secure authentication and authorization system
- ⚠️ **Custom Exception Handling** - Robust error management and user-friendly error reporting
- 📁 **Logging, Configuration, and Data Persistence** - Comprehensive logging system and configurable settings

---

## 🛠️ Installation and Setup

This is a Java SE project designed to run directly from your IDE with no additional packaging required.

### Prerequisites

Before running the application, ensure you have the following installed:

- **Java JDK 11** or higher
- **NetBeans 20** (recommended) or any compatible Java IDE
- **MySQL Server** (5.7 or higher)
- **JDBC Driver**: `mysql-connector-java-8.0.x.jar`
- **AbsoluteLayout library** (if using NetBeans GUI Designer)

### Setup Instructions

1. **Clone the Repository**
   ```bash
   git clone https://github.com/Gatorsoft/aerodeskpro.git
   cd aerodeskpro
   ```

2. **Open in NetBeans**
   - Launch NetBeans 20
   - Open the project from the cloned directory
   - Allow NetBeans to resolve dependencies

3. **Database Setup**
   - Start your MySQL server
   - Create a new database named `aerodesk_pro`
   - Import the provided SQL schema (if available)
   - Update database credentials in:
     ```
     com/gatorsoft/aerodeskpro/database/db.properties
     ```

4. **Library Configuration**
   Add the following JAR files to your project classpath:
   - `mysql-connector-java-8.0.x.jar`
   - `AbsoluteLayout.jar` (if using NetBeans GUI Designer)

5. **Run the Application**
   Execute the main class:
   ```
   com.gatorsoft.aerodeskpro.gui.Main.java
   ```

---

## 🗃️ Project Architecture

```
aerodeskpro/
│
├── src/
│   └── com/gatorsoft/aerodeskpro/
│       ├── api/               # External API integrations (weather, flight data)
│       ├── database/          # Database configuration and connection management
│       ├── dao/               # Data Access Objects (JDBC operations)
│       ├── entity/            # Entity classes, enums, and role definitions
│       ├── exceptions/        # Custom exception classes
│       ├── gui/               # GUI components, forms, and dialogs
│       ├── models/            # Data model classes and DTOs
│       ├── services/          # Business logic and service layer
│       ├── threads/           # Background threads and multithreading utilities
│       └── utils/             # Utility and helper classes
│
├── resources/
│   ├── img/                   # UI assets (icons, logos, images)
│   ├── config/                # Configuration files
│   └── sql/                   # Database scripts and migrations
│
├── logs/                      # Application log files
├── build.xml                  # Apache Ant build script
├── manifest.mf                # JAR manifest file
└── README.md                  # This documentation
```

---

## ⚙️ Technology Stack

| Component | Technology | Version | Purpose |
|-----------|------------|---------|---------|
| **Language** | Java SE | 11+ | Core application development |
| **UI Framework** | Swing | Built-in | Desktop GUI components |
| **GUI Designer** | NetBeans GUI Builder | 20 | Visual form design |
| **Database** | MySQL | 5.7+ | Data persistence |
| **DB Access** | JDBC | Latest | Database connectivity |
| **Threading** | Java Multithreading | Built-in | Background processing |
| **API Integration** | HTTPURLConnection | Built-in | External service calls |
| **Layout Manager** | AbsoluteLayout | Optional | Precise component positioning |
| **Logging** | Custom File Logger | Custom | Application monitoring |
| **Exception Handling** | Java + Custom | Mixed | Error management |

---

## 🏗️ Key Design Patterns

- **MVC (Model-View-Controller)** - Separation of concerns between GUI, business logic, and data
- **DAO (Data Access Object)** - Abstraction layer for database operations
- **Singleton** - Database connection management and configuration
- **Observer** - Real-time UI updates and event handling
- **Factory** - Object creation for different entity types

---

## 📊 Database Schema

The application uses a MySQL database with the following core tables:

- **flights** - Flight information and schedules
- **passengers** - Passenger details and booking information
- **baggage** - Baggage tracking and status
- **gates** - Gate assignments and availability
- **users** - System users and authentication
- **logs** - System activity and audit trail

---

## 🚦 Getting Started

### First Run
1. Launch the application using the main class
2. Log in with default admin credentials (if configured)
3. Initialize sample data through the admin panel
4. Explore different modules and features

### Basic Operations
1. **Flight Management**: Create, schedule, and monitor flights
2. **Passenger Check-in**: Process passenger check-ins and generate boarding passes
3. **Gate Assignment**: Assign gates to flights and monitor availability
4. **Baggage Handling**: Track baggage from check-in to collection

---

## 👥 Development Team

| Name | Student ID | Role | Contact |
|------|------------|------|---------|
| Charaka Hashan | ------- | Project Lead & Backend Developer & Database Management & API Integration |  |
| Rashi Himaya | -------  | GUI Developer  | |
| Osada Gaya Suraweera | -------  | GUI Developer  |  |
| Himasha Shehan| -------  | GUI Developer  |  |
| Mohommad Aakil | -------  | API Integration & Testing |  |
| Shakya Kariyawasam | -------  | Documentation & Quality Assurance |  |
| Mohommad Aakil | -------  | Documentation & Quality Assurance |  |
| Pathum Kaushallaya | -------  | GUI Developer & Quality Assurance & Database Designer | |

---

## 🌐 Repository Information

- **GitHub Repository**: [https://github.com/Gatorsoft/aerodeskpro](https://github.com/Gatorsoft/aerodeskpro)
- **Issues**: Report bugs and feature requests on GitHub Issues
- **Wiki**: Detailed documentation available on GitHub Wiki

---

## 📝 License


This project is licensed under the  
**[Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)](https://creativecommons.org/licenses/by-nc/4.0/)**.

You are free to:

Share — copy and redistribute the material in any medium or format

Adapt — remix, transform, and build upon the material

Under the following terms:

Attribution — You must give appropriate credit, provide a link to the license, and indicate if changes were made.

NonCommercial — You may not use the material for commercial purposes.

<!-- This project is licensed under the  
**[Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)](https://creativecommons.org/licenses/by-nc/4.0/)**.
-->
It is intended strictly for **educational and academic use**.  
Commercial use, redistribution for profit, or production deployment is **not permitted**.

**Academic Use Only** - This software is provided for educational purposes and should not be used in production environments without proper security auditing and testing.

---

## 🔧 Troubleshooting

### Common Issues

1. **Database Connection Failed**
   - Verify MySQL server is running
   - Check database credentials in `db.properties`
   - Ensure database schema is properly imported

2. **Missing Dependencies**
   - Verify all JAR files are in the classpath
   - Check NetBeans library configuration

3. **GUI Components Not Loading**
   - Ensure AbsoluteLayout library is available
   - Verify NetBeans GUI Builder compatibility

### Performance Optimization

- Configure JVM heap size for better performance: `-Xmx512m -Xms256m`
- Enable connection pooling for database operations
- Monitor thread usage and optimize background processes

---

## 🙋 Support

For technical support, bug reports, or feature requests:

1. **GitHub Issues**: Open an issue on the repository
2. **Email Support**: Contact the development team members
3. **Documentation**: Refer to the project wiki and inline comments

**Response Time**: Academic project support is provided on a best-effort basis.

---

## 💡 Future Enhancements

### Planned Features
- 📊 **Export Capabilities** - PDF/Excel export for passenger and flight data
- 📈 **Admin Dashboard** - Analytics and comprehensive reporting system
- 🖱️ **Drag-and-Drop Interface** - Interactive gate assignment with visual feedback
- 🔄 **Airline Synchronization** - Real-time schedule updates from airline APIs
- 📱 **Mobile Companion App** - Passenger self-service mobile interface

### Technical Improvements
- **Database Optimization** - Query performance tuning and indexing
- **Security Enhancement** - Advanced authentication and encryption
- **API Expansion** - Integration with more external services
- **Testing Suite** - Comprehensive unit and integration testing
- **Containerization** - Docker support for easier deployment

---

## 📚 Learning Outcomes

This project demonstrates proficiency in:

- Advanced Java programming concepts and best practices
- Desktop application development with Swing
- Database design and JDBC integration
- Multithreaded application architecture
- API integration and external service consumption
- Software engineering principles and design patterns
- Version control and collaborative development

---

## 🎯 Academic Context

**Course**: BSC[Hons] Software Engineering  
**Semester**: 2nd level  
**Institution**: Java Institute for Advance Technology Sri Lanka  
**Instructor**: Mr.Tharaka Sankalpa, Mr.Achira Dissanayake, Mr.Tashila Dilshan

This project serves as a capstone demonstration of software engineering principles learned throughout the course, showcasing practical application of theoretical concepts in a real-world scenario.

---

<div align="center">

## 🌟 Thank you for using AeroDesk Pro! ✈️

*Elevating airport management through innovative software solutions*

---

**⭐ Don't forget to star this repository if you found it helpful!**

</div>
