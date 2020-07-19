package com.mckanna.arrived.view_models;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.mckanna.arrived.data.Repository;

public class ArrivedViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private Repository repository;

    public ArrivedViewModelFactory(Repository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ArrivedViewModel(repository);
    }
}
