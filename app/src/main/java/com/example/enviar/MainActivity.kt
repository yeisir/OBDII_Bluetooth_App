package com.example.enviar

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : ComponentActivity() {

    private lateinit var eBtn: Button
    private lateinit var locationManager: LocationManager
    private lateinit var handler: Handler
    private lateinit var locationListener: LocationListener
    private var sendingCoordinates = false // Variable para controlar el envío repetido
    private lateinit var sW: Switch
    private var sWv = 1

    private val PORT_UDP1 = 9999 // Puerto UDP Coordenadas
    private val PORT_UDP2 = 8888 // Puerto UDP Coordenadas + DV

    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothDevice: BluetoothDevice? = null
    private var bluetoothSocket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null

    private val DEVICE_ADDRESS = "01:BF:6B:02:0A:DE" // Dirección MAC del dispositivo Bluetooth al que deseas conectar
    private val PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")



    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        eBtn = findViewById(R.id.Enviar)
        sW = findViewById(R.id.switch1)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        handler = Handler()

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Este dispositivo no soporta Bluetooth", Toast.LENGTH_SHORT).show()
            finish()
            return
        }


        sW.setOnCheckedChangeListener { _, isChecked ->
            sWv = if (isChecked) 2 else 1
        }

        eBtn.setOnClickListener {
            if (sWv == 1) {
                if (!sendingCoordinates) {
                    //val ips = listOf("3.140.234.171", "3.18.154.149", "3.136.126.82") // Agrega aquí las direcciones IP adicionales
                    val ips = listOf("3.140.234.171")
                    // Iterar sobre todas las direcciones IP y enviar los datos a cada una
                    ips.forEach { ipAddress ->
                        sendLocationData1(ipAddress, PORT_UDP1)
                    }
                    // Iniciar el envío repetido de coordenadas cada 5 segundos
                    sendingCoordinates = true
                    handler.postDelayed(coordinateSender1, 3000)

                }
            }else {
                if (!sendingCoordinates) {
                    //val ips = listOf("3.140.234.171", "3.18.154.149", "3.136.126.82") // Agrega aquí las direcciones IP adicionales
                    val ips = listOf("3.140.234.171")
                    // Iterar sobre todas las direcciones IP y enviar los datos a cada una
                    ips.forEach { ipAddress ->
                        sendLocationData2(ipAddress, PORT_UDP2)
                    }
                    // Iniciar el envío repetido de coordenadas cada 5 segundos
                    sendingCoordinates = true
                    handler.postDelayed(coordinateSender2, 3000)

                }

            }
        }
        connectBluetooth()
    }


    private val coordinateSender1 = object : Runnable {
        override fun run() {
            //val ips = listOf("3.140.234.171","3.18.154.149","3.136.126.82") // Agrega aquí las direcciones IP adicionales
            val ips = listOf("3.140.234.171")
            // Iterar sobre todas las direcciones IP y enviar los datos a cada una
            ips.forEach { ipAddress ->
                sendLocationData1(ipAddress, PORT_UDP1)
            }
            // Programar el siguiente envío de coordenadas después de 3 segundos
            handler.postDelayed(this, 3000)
        }
    }

    private val coordinateSender2 = object : Runnable {
        override fun run() {
            //val ips = listOf("3.140.234.171","3.18.154.149","3.136.126.82") // Agrega aquí las direcciones IP adicionales
            val ips = listOf("3.140.234.171")
            // Iterar sobre todas las direcciones IP y enviar los datos a cada una
            ips.forEach { ipAddress ->
                sendLocationData2(ipAddress, PORT_UDP2)
            }
            // Programar el siguiente envío de coordenadas después de 3 segundos
            handler.postDelayed(this, 3000)
        }
    }

    @SuppressLint("MissingPermission")
    private fun connectBluetooth() {
        if (bluetoothAdapter?.isEnabled == false) {
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBluetoothIntent, 1)
        } else {
            bluetoothDevice = bluetoothAdapter?.getRemoteDevice(DEVICE_ADDRESS)
            try {
                bluetoothSocket = bluetoothDevice?.createRfcommSocketToServiceRecord(PORT_UUID)
                bluetoothSocket?.connect()
                outputStream = bluetoothSocket?.outputStream
                Toast.makeText(this, "Conexión Bluetooth establecida", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this, "Error al conectar al dispositivo Bluetooth", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun obtenerRPMDesdeBluetooth(): String {
        var resRpm = "" // Almacenará la respuesta completa del dispositivo Bluetooth

        try {
            // Comando OBD-II para obtener las RPM
            val comando = "010C\r" // Reemplazar con el comando OBD-II apropiado para tu dispositivo
            val outputStream = bluetoothSocket?.outputStream
            val inputStream = bluetoothSocket?.inputStream

            // Enviar el comando al dispositivo
            outputStream?.write(comando.toByteArray())

            // Leer la respuesta del dispositivo
            val buffer = ByteArray(1024)
            val bytes = inputStream?.read(buffer)

            if (bytes != null && bytes > 0) {
                val respuesta = String(buffer, 0, bytes)
                resRpm = respuesta.trim() // Eliminar espacios en blanco
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        return resRpm
    }


    private fun sendLocationData1(ipAddress: String, portUDP: Int) {
        if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                locationListener = object : LocationListener {
                    @RequiresApi(Build.VERSION_CODES.O)
                    @SuppressLint("SimpleDateFormat")
                    override fun onLocationChanged(location: Location) {
                        val latitude = location.latitude
                        val longitude = location.longitude
                        val altitude = location.altitude
                        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        val timestamp = sdf.format(Date())

                        val message = "\nLatitud: $latitude\nLongitud: $longitude\nAltitud: $altitude\nTimestamp: $timestamp"

                        Thread {
                            val udpSocket = DatagramSocket()
                            val sendData = message.toByteArray()
                            val packet = DatagramPacket(
                                sendData,
                                sendData.size,
                                InetAddress.getByName(ipAddress),
                                portUDP
                            )
                            udpSocket.send(packet)
                            udpSocket.close()
                        }.start()

                        Toast.makeText(
                            this@MainActivity,
                            "Coordenadas Enviadas a $ipAddress",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                // Solicitar actualización de la ubicación
                locationManager.requestSingleUpdate(
                    LocationManager.GPS_PROVIDER,
                    locationListener,
                    null
                )
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSION_REQUEST_CODE
                )
            }
    }


    private fun sendLocationData2(ipAddress: String, portUDP: Int) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            bluetoothSocket != null && bluetoothSocket?.isConnected == true) {
            locationListener = object : LocationListener {
                @RequiresApi(Build.VERSION_CODES.O)
                @SuppressLint("SimpleDateFormat")
                override fun onLocationChanged(location: Location) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    val altitude = location.altitude
                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    val timestamp = sdf.format(Date())
                    val rpm = obtenerRPMDesdeBluetooth()

                    val message = "\nLatitud: $latitude\nLongitud: $longitude\nAltitud: $altitude\nTimestamp: $timestamp\nRPM: $rpm"

                    Thread {
                        val udpSocket = DatagramSocket()
                        val sendData = message.toByteArray()
                        val packet = DatagramPacket(sendData, sendData.size, InetAddress.getByName(ipAddress), portUDP)
                        udpSocket.send(packet)
                        udpSocket.close()
                    }.start()

                    Toast.makeText(this@MainActivity, "Coordenadas Enviadas a $ipAddress", Toast.LENGTH_LONG).show()
                }
            }
            // Solicitar actualización de la ubicación
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null)
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_CODE)
        }
    }




    companion object {
        private const val PERMISSION_REQUEST_CODE = 100 // Puedes usar cualquier código único aquí
    }
}