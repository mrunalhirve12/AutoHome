# ECE 558 Project #3 -- Building an IoT Home Automation System
## <b>This assignment is worth 100 points.  Demos on Wed, 14-Nov and Thu 15-Nov.  Deliverables due to GitHub on Sat, 17-Nov by 10:00 PM.  </b>

### <i> We will be using GitHub classroom for this assignment.  You will do your development using the integrated support for Git and GitHub in Android Studio.  You should submit your assignment before the deadline by pushing your final Android Studio projects and other deliverables to your shared GitHub repository for the assignment.</i>

## After completing this assignment students should have:
- Learned to apply Android to control and monitor a device remotely using the internet
- Gained experience with Google Android Things and Firebase
- Gained experience with embedded systems hardware using an off-the-shelf single-board computer and commonly available components


### Introduction

Although ECE 558 is entitled Embedded Systems Programming all we have produced so far has been software running on an Android device. That’s about to change. Project #3 has both a hardware and a software component to it and provides a chance for you to gain experience building an application for the Internet of Things (IoT). For hardware we will be using a Raspberry PI 3 (RPI3), a few resistors, a tri-color (RGB) LED, a PIC microcontroller, an analog temperature sensor and DC hobbyist motor. You will use your Android device to run an app that shares information with the RPI3 across a WiFi or Bluetooth link. Both the control app running on the RPI3 and the user interface app running on the Android device will be programmed in Java using Android Studio and the Android framework.  

The deliverables for this project will be a demo for the T/A or instructor, a design report for the project, and your Android Studio projects.  You should bring your own hardware to the demo.  We will provide WiFi access.

We will be using the team project support in GitHub classroom for this assignment.  This means that your team will share a private repository on GitHub that can also be accessed by the instructor and T/A for the course. You will submit your work via a private repository using GitHub classroom.  Our plan is to review your work and provide feedback using GitHub classroom but doing team projects  will be a new experience for us so we'll see how it goes.

## Deliverables
Push your deliverables to your private GitHub repository for the assignment.  The repository should include:
- Your design report (.pdf preferred).
- A final version of your Android projects. We will use these to grade your effort.  “Neatness counts” for this project - we will grade the quality of your code.  You code should be well structured, indented consistently (Android Studio helps with that) and you should include comments describing what long sections of your code do.    Comments should be descriptive rather than explain the obvious (ex:  //set a to b when the actual code says a = b; does not provide any value-added).

## List of files:
- docs/project3.pdf - The write-up for the project.
- docs/project3_hardware.pdf - Description and details about the hardware used in tis project. Includes information on how to configure your RPI3 for Android Things and WiFi.
- docs/project2_software.pdf - Description and details about the software portion(s) of this project.  Includes some basic information on configuring Firebase
- hardware/datasheets - datasheets for some of the hardware components used in the project.
- hardware/iot_schematic.pdf - schematic for the hardware system
- hardware/iot_wiring_dgm.pdf - wiring diagram for the hardware system
- README.md - this file
