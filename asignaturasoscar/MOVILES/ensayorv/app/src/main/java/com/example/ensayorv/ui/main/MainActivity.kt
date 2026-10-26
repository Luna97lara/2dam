package com.example.ensayorv.ui.main

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ensayorv.R
import com.example.ensayorv.databinding.ActivityMainBinding
import com.example.ensayorv.di.AppModule
import com.example.ensayorv.domain.model.Gatito

class MainActivity : AppCompatActivity() {

    private val binding : ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater)}

    private val viewModel : MainViewModel by viewModels { MainViewModelFactory(
        AppModule.dameFavoritoUseCase,
        AppModule.dameListaUseCase,
        AppModule.addGatitoUseCase,
        AppModule.delGatitoUseCase
    ) }

    private lateinit var adapter : GatitoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        configRecycler()

        observarEstado()

        setupEventos()

    }

        private fun configRecycler() {

            binding.listado.layoutManager = LinearLayoutManager(this)

            adapter = GatitoAdapter( { gatito ->
                viewModel.handleDelete(gatito)
            })

            binding.listado.adapter = adapter

        }

    private fun setupEventos() {

        with(binding) {

            button.setOnClickListener {
                viewModel.handleDameFavorito()
                viewModel.handleDameLista()
            }

            buttonAdd.setOnClickListener {
                var g = Gatito("Roger", null, 0)

                viewModel.handleAdd(g)

            }

        }
    }

    private fun observarEstado() {

        viewModel.state.observe(this, { state ->

            state?.favorito.let {

                binding.textView.text = it

            }

            state?.gatitos.let {

                adapter.submitList(it)

            }

            state?.error?.let {

                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                viewModel.handleLimpiarError()

            }

        })


    }

}
