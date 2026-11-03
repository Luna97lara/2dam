package com.example.gatitosrv.ui.main

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gatitosrv.R
import com.example.gatitosrv.databinding.ActivityMainBinding
import com.example.gatitosrv.di.AppModule
import com.example.gatitosrv.domain.model.Gatito

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val viewModel: MainViewModel by viewModels { MainViewModelFactory(
        AppModule.dameFavoritoUseCase,
        AppModule.ListaGatitosUseCase,
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
    }


    private fun configRecycler() {
        binding.listado.layoutManager = LinearLayoutManager(this)

        adapter = GatitoAdapter({ gatito->
            viewModel.handleDelete(gatito)
        })
        binding.listado.adapter = adapter





    }


    private fun setupEventos() {
        with(binding) {
            button.setOnClickListener {
                // no se cambian interfaz fuera del observable
                //binding.textView.text = viewModel.damePresidente()

                viewModel.handleDameFavorito()
                viewModel.handleCargarLista()
            }


            buttonAdd.setOnClickListener {
                var p = Gatito(nombre = textBox.editText?.text.toString(), null, 0)

                viewModel.handleAdd(p)
            }
        }
    }

    private fun observarEstado() {
        viewModel.state.observe(this,{ state ->
            state?.gatitos.let{
                binding.textView.text = it
            }

            state?.gatitos.let {
                adapter.submitList(it)
            }

            state?.error?.let{ error ->
                Toast.makeText(this,error,Toast.LENGTH_SHORT).show()
                viewModel.handleLimpiarError()
            }
        })


    }
}
