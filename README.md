# Bluetooth Data Plotter
This Android application facilitates communication with external devices via Bluetooth, receiving and visualising data from connected devices.

## Features:
**MainActivity**: The main activity responsible for displaying options to connect devices.  
**BLEUtils**: A utility class for managing Bluetooth Low Energy (BLE) connections, scanning for nearby BLE devices, and handling data communication.  
**BLEPairing**: An activity for discovering nearby Bluetooth devices, selecting a device for pairing, establishing a connection for data transfer and displaying parsed data after a successful connection.  
**BLEListAdapter**: A RecyclerView adapter for displaying a list of discovered Bluetooth devices.  
**BLEViewHolder**: ViewHolder class for representing individual items in the Bluetooth device list.  
**Visualisation(Developing)**: An activity for visualising data with MPAndroidChart library.  
