package disdik.getak.id.ui.reports;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ReportViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ReportViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is reports fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}