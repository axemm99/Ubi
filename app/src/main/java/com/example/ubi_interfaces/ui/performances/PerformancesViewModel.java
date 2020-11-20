package com.example.ubi_interfaces.ui.performances;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PerformancesViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public PerformancesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}