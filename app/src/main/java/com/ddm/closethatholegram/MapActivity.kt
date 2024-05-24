package com.ddm.closethatholegram

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapClickListener {
    private lateinit var mMap: GoogleMap
    private lateinit var btnConfirm: Button
    private var selectedLocation: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        btnConfirm = findViewById(R.id.btnConfirm)
        btnConfirm.setOnClickListener {
            // Confirmar localização selecionada e fechar a atividade
            if (selectedLocation != null) {
                val intent = Intent()
                intent.putExtra("latitude", selectedLocation?.latitude)
                intent.putExtra("longitude", selectedLocation?.longitude)
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Adicionar um marcador padrão ao centro do mapa
        val defaultLocation = LatLng(0.0, 0.0)
        mMap.addMarker(MarkerOptions().position(defaultLocation).title("Marker in Default Location"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(defaultLocation))

        // Configurar um listener de clique no mapa
        mMap.setOnMapClickListener { latLng ->
            // Adicionar um marcador onde o usuário clicou
            mMap.clear()
            mMap.addMarker(MarkerOptions().position(latLng).title("Selected Location"))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))

            // Exibir o botão de confirmação
            btnConfirm.visibility = View.VISIBLE

            // Armazenar a localização selecionada
            selectedLocation = latLng
        }
    }

    override fun onMapClick(latLng: LatLng) {
        // Não é necessário implementar nesta versão
    }

}

