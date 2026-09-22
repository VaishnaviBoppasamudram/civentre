# Civentre – Smart Public Issue Reporting & Resolution Platform

Civentre is a Java Full-Stack web application designed to connect citizens with public issue management teams. Citizens can report problems such as potholes, broken streetlights, garbage accumulation, water leakage, traffic signal failures, and fallen trees. Administrators can verify and assign issues, while field officers can manage assigned issues and update their progress.

## Problem Statement

Public issues are often reported through informal channels, making it difficult for citizens to track progress and for authorities to manage complaints efficiently.

Civentre provides a centralized platform where issues can be reported, assigned, tracked, updated, and resolved through a structured workflow.

## Key Features

### Citizen Module
- User registration and secure login
- Report public issues with title and description
- Upload issue images
- Capture location using latitude and longitude
- View submitted issues
- Track issue status and history
- Receive notifications
- Support existing public issues
- Confirm resolution of resolved issues

### Admin Module
- Secure administrator login
- View all reported issues
- Verify or reject reported issues
- Assign departments and field officers
- Set and manage issue priorities
- Monitor issue progress
- View analytics and issue statistics
- Monitor department and officer workload

### Field Officer Module
- Secure officer login
- View assigned issues
- View issue details and location
- Update issue status
- Add progress information
- Upload resolution photographs
- Mark assigned issues as resolved

## Issue Workflow

```text
Reported
   ↓
Verified
   ↓
Assigned
   ↓
In Progress
   ↓
Resolved
   ↓
Closed