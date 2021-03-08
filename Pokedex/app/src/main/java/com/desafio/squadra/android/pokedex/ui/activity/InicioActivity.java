package com.desafio.squadra.android.pokedex.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.desafio.squadra.android.pokedex.R;
import com.desafio.squadra.android.pokedex.databinding.ActivityInicioBinding;
import com.desafio.squadra.android.pokedex.room.entity.PokemonEntity;
import com.desafio.squadra.android.pokedex.service.web.response.Pokemon;
import com.desafio.squadra.android.pokedex.service.web.PokedexAPIService;
import com.desafio.squadra.android.pokedex.viewmodel.PokemonsViewModel;
import com.desafio.squadra.android.pokedex.viewmodel.PokemonsViewModelFactory;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InicioActivity extends AppCompatActivity {

    private static final String POKEDEX_API_BASE_URL = "https://pokeapi.glitch.me/v1/";

    private static final int COUNT_POKEMONS_GEN_1 = 151;
    private static final int COUNT_POKEMONS_GEN_2 = 100;

    private ActivityInicioBinding binding;
    private PokemonsViewModel pokemonsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInicioBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Gerar animação do Pikachu correndo de forma assíncrona (método 'into')
        Glide.with(this).asGif().load(R.drawable.pikachu_running).into(binding.ivPikachuRunning);

        binding.btnEntrar.setOnClickListener(v -> entrar());
    }

    @SuppressLint("SetTextI18n")
    private void entrar() {
        pokemonsViewModel =
                new ViewModelProvider(getViewModelStore(), new PokemonsViewModelFactory(this.getApplication(), 0)).get(PokemonsViewModel.class);
        List<PokemonEntity> listaTodosPokemons = pokemonsViewModel.buscarTodosLista();

        // Verifica se precisa ou não (caso as inserções de pokémons já estejam no banco de dados)
        // fazer as requisições HTTP para busca de pokémons
        if (listaTodosPokemons != null && listaTodosPokemons.size() < COUNT_POKEMONS_GEN_1 + COUNT_POKEMONS_GEN_2) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(POKEDEX_API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            PokedexAPIService pokedexAPIService = retrofit.create(PokedexAPIService.class);

            for(int i = 1; i <= COUNT_POKEMONS_GEN_1 + COUNT_POKEMONS_GEN_2; i ++) {
                // Caso for necessário buscar algum pokémon que não exista na base de dados ainda
                if (pokemonsViewModel.buscar(i) == null) {
                    pokedexAPIService.buscarPokemon(i).enqueue(new Callback<List<Pokemon>>() {
                        @Override
                        public void onResponse(@NotNull Call<List<Pokemon>> call, @NotNull Response<List<Pokemon>> response) {
                            if (response.isSuccessful()) {
                                List<Pokemon> pokemonBuscado = response.body();

                                assert pokemonBuscado != null;
                                Pokemon novoPokemon = pokemonBuscado.get(0);

                                PokemonEntity inserirNovoPokemon = new PokemonEntity(novoPokemon);
                                pokemonsViewModel.inserir(inserirNovoPokemon);
                            } else {
                                Log.d("FALHA API", "Falha na requisição " + POKEDEX_API_BASE_URL + "  => " + response.code() + ": " + response.message());
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<List<Pokemon>> call, @NotNull Throwable t) {
                            Log.d("FALHA SERVIDOR", "Falha no servidor " + POKEDEX_API_BASE_URL + "  => " + t.getMessage());
                        }
                    });
                }
            }
        }

        Intent intent = new Intent(getApplicationContext(), ListasActivity.class);
        startActivity(intent);
    }
}