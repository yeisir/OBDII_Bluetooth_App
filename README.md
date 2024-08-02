# OBDII_Bluetooth_App

An Android application that connects to an OBDII device via Bluetooth and sends GPS location data to a server using UDP.

## Features

- Connect to an OBDII device via Bluetooth.
- Obtain GPS location data.
- Send location data and RPM to a server via UDP.
- Toggle between sending only coordinates or coordinates with RPM data.

## Requirements

- Android Studio
- Android device with Bluetooth and GPS
- UDP server to receive the data

## Permissions

The application requires the following permissions:

- `ACCESS_COARSE_LOCATION`
- `ACCESS_FINE_LOCATION`
- `BLUETOOTH_CONNECT`
- `INTERNET`

## Setup

1. Clone this repository:

   ```sh
   git clone https://github.com/yeisir/OBDII_Bluetooth_App.git
2. Open the project in Android Studio.
3. Ensure your Android device is set up to allow app installations from Android Studio.
4. Connect your Android device to your computer.
5. Run the application from Android Studio.

## Usage

1. Open the app on your Android device.
2. Ensure Bluetooth is enabled on your device.
3. The app will automatically connect to the OBDII device using the MAC address provided in the code (DEVICE_ADDRESS).
4. Press the "Send" button to start sending location data or location data with RPM to the server.

## Note

I hope you find this project useful! If you have any questions or feedback, feel free to open an issue or reach out. :)

