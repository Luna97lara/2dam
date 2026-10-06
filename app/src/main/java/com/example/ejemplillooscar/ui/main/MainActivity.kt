package com.example.ejemplillooscar.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ejemplillooscar.R
import com.example.ejemplillooscar.databinding.ActivityMainBinding
import com.example.ejemplillooscar.di.AppModule

class MainActivity : AppCompatActivity() {

    private val viewBinding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(
        layoutInflater) }
    private val viewModel: MainViewModel by viewModels { MainViewModelFactory(AppModule.damePresidenteUseCase) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(viewBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupEventos()
        observarEstado()
    }

    private fun setupEventos(){
        viewBinding.button.setOnClickListener{
            viewModel.handleDamePresidente()
        }
    }

    private fun observarEstado(){
        viewModel.state.observe(this, {it?.presidente.let { viewBinding.textView.text = it }
        it?.error?.let { error -> Toast.makeText(this, error, Toast.LENGTH_SHORT).show()}})
    }

    private fun configRecycler() {
        val adapter= PoliticoAdapter()
        viewBinding.lista.adapter.toString()
    }


}