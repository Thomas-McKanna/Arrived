package com.mckanna.arrived.util;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.mckanna.arrived.ArrivedApplication;
import com.mckanna.arrived.data.AppDatabase;
import com.mckanna.arrived.data.TripDao;
import com.mckanna.arrived.data.Repository;
import com.mckanna.arrived.view_models.ArrivedViewModel;
import com.mckanna.arrived.view_models.ArrivedViewModelFactory;

public class InjectorUtils {
    public static Repository getRepository(Context context) {
        TripDao dao = AppDatabase.getInstance(context).getTripDao();
        return new Repository(dao);
    }

    public static ArrivedViewModel getViewModel(AppCompatActivity activity) {
        Repository repository = getRepository(activity);
        ArrivedViewModelFactory factory = new ArrivedViewModelFactory(repository);
        ViewModelProvider provider = new ViewModelProvider(activity, factory);
        return provider.get(ArrivedViewModel.class);
    }

    public static Permissions getPermissions(AppCompatActivity activity) {
        return ((ArrivedApplication) activity.getApplication()).permissions;
    }
}
