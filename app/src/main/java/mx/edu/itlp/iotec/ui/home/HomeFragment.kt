package mx.edu.itlp.iotec.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import mx.edu.itlp.iotec.R

// <section-a>
import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.util.Log
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import mx.edu.itlp.iotec.ui.MainActivity

import java.io.IOException
import java.io.InputStream
import java.util.*
import java.util.concurrent.TimeUnit


// <section-a>

// <section-b>
const val REQUEST_ENABLE_BT = 1;
// <section-b>

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
// private const val ARG_PARAM1 = "param1"
// private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    // private var param1: String? = null
    // private var param2: String? = null

    // <section-c>
    //BluetoothAdapter
    lateinit var mBtAdapter: BluetoothAdapter
    var mAddressDevices: ArrayAdapter<String>? = null
    var mNameDevices: ArrayAdapter<String>? = null
    lateinit var lineChart: LineChart
    var values: List<String>? = null

    companion object {

        var m_myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        private var m_bluetoothSocket: BluetoothSocket? = null

        var m_isConnected: Boolean = false
        lateinit var m_address: String

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(/*param1: String, param2: String*/) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    // putString(ARG_PARAM1, param1)
                    // putString(ARG_PARAM2, param2)
                }
            }
    }
    // <section-c>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // param1 = it.getString(ARG_PARAM1)
            // param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val m = (activity as MainActivity)
        val v = inflater.inflate(R.layout.fragment_home, container, false)

        // <section-d>
        mAddressDevices = ArrayAdapter(m, android.R.layout.simple_list_item_1)
        mNameDevices = ArrayAdapter(m, android.R.layout.simple_list_item_1)

        val idBtnEncender = v.findViewById<Button>(R.id.btnEncender)
        // val idBtnOffBT = findViewById<Button>(R.id.idBtnOffBT)
        val idBtnConectar = v.findViewById<Button>(R.id.btnConectar)
        val idBtnEnviar = v.findViewById<Button>(R.id.btnEnviar)
        val idBtnBuscar = v.findViewById<Button>(R.id.btnBuscar)

        // val idBtnLuz_1on = findViewById<Button>(R.id.idBtnLuz_1on)
        // val idBtnLuz_1off = findViewById<Button>(R.id.idBtnLuz_1off)
        // val idBtnLuz_2on = findViewById<Button>(R.id.idBtnLuz_2on)
        // val idBtnLuz_2off = findViewById<Button>(R.id.idBtnLuz_2off)

        // val idBtnDispBT = v.findViewById<Button>(R.id.idBtnDispBT)
        val idSpnDispos = v.findViewById<Spinner>(R.id.spnDispositivos)
        val idEtxTerminal = v.findViewById<EditText>(R.id.etTerminal)

        lineChart = v.findViewById<LineChart>(R.id.chtDatos)

        /*val descripcion: Description = Description()
        descripcion.text = "datos"
        descripcion.setPosition(150f, 15f)
        idChtDat*/

        var datos = mutableListOf<Entry>()
        datos.add(Entry(0f,0f))
        //datos.add(Entry(10f,10f))

        var dset:LineDataSet = LineDataSet(datos,"datos")
        dset.setColor(Color.BLUE)

        var ld:LineData = LineData(dset)
        lineChart.data = ld;

        val someActivityResultLauncher = registerForActivityResult(
            StartActivityForResult()
        ) { result ->
            if (result.resultCode == REQUEST_ENABLE_BT) {
                Log.i("MainActivity", "ACTIVIDAD REGISTRADA")
            }
        }

        //Inicializacion del bluetooth adapter
        mBtAdapter = (m.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter

        //Checar si esta encendido o apagado
        if (mBtAdapter == null) {
            Toast.makeText(m, "Bluetooth no está disponible en este dipositivo", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(m, "Bluetooth está disponible en este dispositivo", Toast.LENGTH_LONG).show()
        }

        //Boton Encender bluetooth
        /*idBtnEncender.setOnClickListener {
            if (mBtAdapter.isEnabled) {
                //Si ya está activado
                Toast.makeText(m, "Bluetooth ya se encuentra activado", Toast.LENGTH_LONG).show()
                mBtAdapter.disable()
            } else {
                //Encender Bluetooth
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                if (ActivityCompat.checkSelfPermission(
                        m,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Log.i("MainActivity", "ActivityCompat#requestPermissions")
                }
                someActivityResultLauncher.launch(enableBtIntent)
            }
        }*/

        //Boton apagar bluetooth
        idBtnEncender.setOnClickListener {
            if (!mBtAdapter.isEnabled) {
                //Si ya está desactivado
                //Encender Bluetooth
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                if (ActivityCompat.checkSelfPermission(
                        m,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Log.i("MainActivity", "ActivityCompat#requestPermissions")
                }
                someActivityResultLauncher.launch(enableBtIntent)
                //Toast.makeText(m, "Bluetooth ya se encuentra desactivado", Toast.LENGTH_LONG).show()
                Toast.makeText(m, "Se ha activado el bluetooth", Toast.LENGTH_LONG).show()
            } else {
                //Encender Bluetooth
                mBtAdapter.disable()
                Toast.makeText(m, "Se ha desactivado el bluetooth", Toast.LENGTH_LONG).show()

                //ACTUALIZO LOS DISPOSITIVOS
                idSpnDispos.setAdapter(mNameDevices)
            }
        }

        //Boton dispositivos emparejados

        idBtnBuscar.setOnClickListener {
            if (mBtAdapter.isEnabled) {

                val pairedDevices: Set<BluetoothDevice>? = mBtAdapter?.bondedDevices
                mAddressDevices!!.clear()
                mNameDevices!!.clear()

                pairedDevices?.forEach { device ->
                    val deviceName = device.name
                    val deviceHardwareAddress = device.address // MAC address
                    mAddressDevices!!.add(deviceHardwareAddress)
                    //........... EN ESTE PUNTO GUARDO LOS NOMBRE A MOSTRARSE EN EL COMBO BOX
                    mNameDevices!!.add(deviceName)
                }

                /*datos.add(Entry(datos.count()+0f,(10*datos.count())+0f))
                //datos.add(Entry(10f,10f))

                var dset:LineDataSet = LineDataSet(datos,"datos")
                dset.setColor(Color.BLUE)

                var ld:LineData = LineData(dset)
                lineChart.data = ld;
                lineChart.notifyDataSetChanged()

                Toast.makeText(m, datos.count().toString(), Toast.LENGTH_LONG).show()*/

                //ACTUALIZO LOS DISPOSITIVOS
                idSpnDispos.setAdapter(mNameDevices)
            } else {
                val noDevices = "Ningun dispositivo pudo ser emparejado"
                mAddressDevices!!.add(noDevices)
                mNameDevices!!.add(noDevices)
                Toast.makeText(m, "Primero vincule un dispositivo bluetooth", Toast.LENGTH_LONG).show()
            }
        }


        idBtnConectar.setOnClickListener {
            try {
                if (m_bluetoothSocket == null || !m_isConnected) {

                    val IntValSpin = idSpnDispos.selectedItemPosition
                    m_address = mAddressDevices!!.getItem(IntValSpin).toString()
                    Toast.makeText(m,m_address,Toast.LENGTH_LONG).show()
                    // Cancel discovery because it otherwise slows down the connection.
                    mBtAdapter?.cancelDiscovery()
                    val device: BluetoothDevice = mBtAdapter.getRemoteDevice(m_address)
                    m_bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(m_myUUID)
                    m_bluetoothSocket!!.connect()
                }

                Toast.makeText(m,"CONEXION EXITOSA",Toast.LENGTH_LONG).show()
                Log.i("MainActivity", "CONEXION EXITOSA")

            } catch (e: IOException) {
                //connectSuccess = false
                e.printStackTrace()
                Toast.makeText(m,"ERROR DE CONEXION",Toast.LENGTH_LONG).show()
                Log.i("MainActivity", "ERROR DE CONEXION")
            }
        }

        /*
        idBtnLuz_1on.setOnClickListener {
            sendCommand("A")
        }

        idBtnLuz_1off.setOnClickListener {
            sendCommand("B")
        }

        idBtnLuz_2on.setOnClickListener {
            sendCommand("C")
        }

        idBtnLuz_2off.setOnClickListener {
            sendCommand("D")
        }
        */

        idBtnEnviar.setOnClickListener {

            if(idEtxTerminal.text.toString().isEmpty()){
                Toast.makeText(m, "El nombre no puede estar vacío", Toast.LENGTH_SHORT)
            }else{
                sendCommand(idEtxTerminal.text.toString());
                println("Dato enviado")
                var mensaje_out: String = idEtxTerminal.text.toString()
                //var mensaje_in: String = ""+sendCommandWithResponse(mensaje_out)
                println("Esperando datos")
                val sockInputStream: InputStream = m_bluetoothSocket!!.getInputStream() // Replace 'sock' with your socket object
                val thread = Thread {
                    val result = readRawDataWithTimeout(sockInputStream)

                    // Handle the result as needed
                    println("Received data: $result")
                }
                thread.start()
                //val result = readRawDataWithTimeout(m_bluetoothSocket!!.getInputStream())
                //Toast.makeText(m, result.toString(), Toast.LENGTH_SHORT)
                //var datos = mutableListOf<Entry>()
                //-datos.add(Entry(datos.count()+0f,mensaje_in.toInt()+0f))
                //datos.add(Entry(10f,10f))

                //-var dset:LineDataSet = LineDataSet(datos,"datos")
                //-dset.setColor(Color.BLUE)

                //-var ld:LineData = LineData(dset)
                //-lineChart.data = ld;
            }
        }
        // <section-d>

        return v;
    }

    // <section-e>
    private fun sendCommand(input: String) {
        if (m_bluetoothSocket != null) {
            try{
                m_bluetoothSocket!!.outputStream.write(input.toByteArray())

            } catch(e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun sendCommandWithResponse(input: String): String? {
        if (m_bluetoothSocket != null) {
            try {
               // val outputStream = m_bluetoothSocket!!.outputStream
                val inputStream = m_bluetoothSocket!!.inputStream

                // Envía el comando
                //outputStream.write(input.toByteArray())

                // Espera y lee la respuesta
                val bufferSize = 1024 // Tamaño del búfer de lectura
                val buffer = ByteArray(bufferSize)
                val bytesRead = inputStream.read(buffer)

                if (bytesRead > 0) {
                    // Si se ha recibido alguna respuesta, conviértela a una cadena y devuelve
                    return String(buffer, 0, bytesRead)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        return null // En caso de error o falta de respuesta
    }

    fun readRawData(inputStream: InputStream): String {
        var b: Byte
        val res = StringBuilder()

        while (true) {
            b = inputStream.read().toByte()
            if (b == (-1).toByte()) {
                break
            }
            val c = b.toChar()
            if (c == '>') {
                break
            }
            res.append(c)
        }

        return res.toString()
    }


    //import java.util.concurrent.TimeUnit

    fun readRawDataWithTimeout(inputStream: InputStream): String {
        val res = StringBuilder()
        val startTime = System.currentTimeMillis()
        val timeoutMillis = TimeUnit.SECONDS.toMillis(5) // 5 segundos

        while (true) {
            val currentTime = System.currentTimeMillis()
            println((currentTime - startTime).toString()+" transcurridos")
            if (currentTime - startTime >= timeoutMillis) {
                println("Hilo detenido")
                break
            }

            val b = inputStream.read().toByte()
            if (b == (-1).toByte()) {
                println("No se recibio un valor")
                break
            }

            val c = b.toChar()
            if (c == '>') {
                println("Se recibio el valor")
                break
            }

            res.append(c)
        }

        return res.toString()
    }



    // <section-e>
}