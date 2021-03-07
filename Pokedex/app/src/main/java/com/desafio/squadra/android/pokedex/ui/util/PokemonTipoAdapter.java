package com.desafio.squadra.android.pokedex.ui.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.desafio.squadra.android.pokedex.R;

import java.util.List;

public class PokemonTipoAdapter extends ArrayAdapter<PokemonTipo> {
    private final LayoutInflater layoutInflater;

    public PokemonTipoAdapter(@NonNull Context context, int resource, @NonNull List<PokemonTipo> listaPokemonTipos) {
        super(context, resource, listaPokemonTipos);
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        @SuppressLint({"ViewHolder", "InflateParams"})
        View optionView = layoutInflater.inflate(R.layout.item_spinner_tipo, null, true);

        PokemonTipo pokemonTipo = getItem(position);

        ImageView ivTypeOption = optionView.findViewById(R.id.iv_type_option);
        ivTypeOption.setImageResource(pokemonTipo.getTypeIcon());

        TextView tvTypeOption = optionView.findViewById(R.id.tv_type_option);
        tvTypeOption.setText(pokemonTipo.getType());

        return optionView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null)
            convertView = layoutInflater.inflate(R.layout.item_spinner_tipo, parent, false);

        PokemonTipo pokemonTipo = getItem(position);

        ImageView ivTypeOption = convertView.findViewById(R.id.iv_type_option);
        ivTypeOption.setImageResource(pokemonTipo.getTypeIcon());

        TextView tvTypeOption = convertView.findViewById(R.id.tv_type_option);
        tvTypeOption.setText(pokemonTipo.getType());

        return convertView;

    }
}
