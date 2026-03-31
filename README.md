

# DeliverEase – Delivery Management Web Application

DeliverEase is a role-based web application built as part of a university software development project. It was designed to improve coordination between logistics managers and delivery personnel by centralizing delivery assignment and tracking.

The system demonstrates the ability to design and implement a real-world workflow solution using modern web technologies.



## Project Summary

DeliverEase is a role-based web application built to improve efficiency in delivery operations by connecting **logistics managers** and **delivery personnel** within a single platform.

The system enables managers to assign deliveries while allowing delivery personnel to track and update delivery progress in real time.



## Problem Solved

Manual delivery coordination often leads to:

* Miscommunication between managers and riders
* Lack of visibility into delivery progress
* Inefficient task allocation

DeliverEase solves this by providing a **centralized system for assignment, tracking, and status updates**.



##  Tech Stack

* **Backend:** Node.js
* **Frontend:** Handlebars (HBS templating engine)
* **Architecture:** MVC (Model-View-Controller)
* **Database:** JSON(local)*



##  Key Features

* **User Authentication**
  Secure login system with role-based access (Manager vs Delivery Personnel)

* **Delivery Assignment**
  Logistics managers can assign deliveries to specific personnel

* **Task Dashboard**
  Delivery personnel can view assigned deliveries

* **Delivery Status Updates**
  Personnel can update delivery progress (e.g., Pending → In Progress → Completed)


## Technical Highlights

* Designed a **role-based access control system** to separate manager and delivery workflows
* Implemented **dynamic server-side rendering** using Handlebars for efficient UI updates
* Built RESTful routes in Node.js to handle delivery assignment and status updates
* Structured the application using **MVC architecture** for scalability and maintainability
* Ensured the system is **extensible for future features** such as notifications, tracking, and analytics



## Architecture Overview

```
Client (Handlebars UI)
        ↓
Node.js Server (Controllers & Routes)
        ↓
Business Logic Layer
        ↓
JSON (Delivery + User Data)

```


## Core Functionality

### Logistics Manager

* Logs into the system
* Assigns deliveries to personnel
* Monitor delivery progress

### Delivery Personnel

* Logs into the system
* Views assigned deliveries
* Updates delivery status



## Impact

* Improves coordination between logistics teams
* Enhances visibility of delivery progress
* Reduces operational inefficiencies
* Demonstrates practical implementation of **role-based systems and workflow management**


## Future Improvements

* Real-time tracking (GPS integration)
* Push/email notifications for new assignments
* Analytics dashboard for performance tracking
* Mobile-friendly or native mobile version

---

##  Authors

**Lisa Adisa Magada**

**Shawn Kathuri**




## Why This Project Matters

DeliverEase demonstrates:

* Backend development with Node.js
* Server-side rendering with Handlebars
* Role-based system design
* Clean architecture and scalable thinking
* Ability to translate real-world workflows into software solutions


