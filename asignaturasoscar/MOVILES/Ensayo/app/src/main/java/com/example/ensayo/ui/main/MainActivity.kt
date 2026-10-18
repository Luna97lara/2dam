package com.example.ensayo.ui.main

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ensayo.R
import com.example.ensayo.databinding.ActivityMainBinding
import com.example.ensayo.di.AppModule

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val viewModel: MainViewModel by viewModels {
        MainViewModel.MainViewModelFactory(
            AppModule.dameDelegadoUseCase,
            AppModule.dameSubdelegadoUseCase
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        observarEstado()

        setUpEventos()

    }

    private fun setUpEventos(){
        with(binding) {
            bdelegado.setOnClickListener {
                viewModel.handleDameDelegado()
            }
            bsubdelegado.setOnClickListener {
                viewModel.handleDameSubdelegado()
            }
        }
    }

    private fun observarEstado() {
        viewModel.state.observe(this, {state ->
            state?.delegado.let{
                binding.textodelegado.text=it
            }
            state?.subdelegado.let {
                binding.textosubdelegado.text=it
            }
        } )
    }

}