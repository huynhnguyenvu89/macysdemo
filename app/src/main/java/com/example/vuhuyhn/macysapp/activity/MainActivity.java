package com.example.vuhuyhn.macysapp.activity;

import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.vuhuyhn.macysapp.MLog;
import com.example.vuhuyhn.macysapp.MacysApplication;
import com.example.vuhuyhn.macysapp.R;
import com.example.vuhuyhn.macysapp.listener.MainActivityListener;
import com.example.vuhuyhn.macysapp.model.FileWrapperObject;
import com.example.vuhuyhn.macysapp.presenter.MainActivityPresenter;
import com.example.vuhuyhn.macysapp.util.MUtils;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

import static com.example.vuhuyhn.macysapp.task.ScanAllFilesTask.AVERAGE_SIZE_KEY;
import static com.example.vuhuyhn.macysapp.task.ScanAllFilesTask.FREQUEST_EXTENSIONS_KEY;
import static com.example.vuhuyhn.macysapp.task.ScanAllFilesTask.LARGEST_FILES_KEY;
import static com.example.vuhuyhn.macysapp.task.ScanAllFilesTask.TOTAL_FILES_KEY;
import static com.example.vuhuyhn.macysapp.util.MUtils.getStringFromFileListDemo;
import static com.example.vuhuyhn.macysapp.util.MUtils.getStringFromHashMapDemo;

public class MainActivity extends AppCompatActivity implements MainActivityListener {

    @BindView(R.id.info_layout)
    LinearLayout infoLayout;
    @BindViews({R.id.text_view_1, R.id.text_view_2, R.id.text_view_3, R.id.text_view_4})
    List<TextView> listTextViews;

    @BindView(R.id.progress_layout)
    LinearLayout progressLayout;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindViews({R.id.update_text_view, R.id.scan_controller})
    List<TextView> scanControllerTexts;
    @BindView(R.id.share_button)
    Button shareButton;

    private MainActivityPresenter presenter;
    private final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        presenter = new MainActivityPresenter(this, this);
        presenter.init(savedInstanceState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        presenter.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        presenter.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    @Override
    public void onMissingPermission(String... permissions) {
        infoLayout.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onExternalMemoryNotAccessible() {
        setInfoLayoutText("Error",
                "Cannot access external memory",
                null,
                null);
        infoLayout.setVisibility(View.VISIBLE);
        MUtils.slideUp(infoLayout);
    }

    @Override
    public void onStartScanning() {
        progressBar.setVisibility(View.VISIBLE);
        shareButton.setVisibility(View.INVISIBLE);
        scanControllerTexts.get(0).setVisibility(View.VISIBLE);
        scanControllerTexts.get(1).setText(MacysApplication.get().getString(R.string.stop_scanning));
        scanControllerTexts.get(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onStopScanning();
            }
        });
    }

    @Override
    public void onStopScanning() {
        progressBar.setVisibility(View.INVISIBLE);
        scanControllerTexts.get(0).setVisibility(View.INVISIBLE);
        scanControllerTexts.get(1).setText(MacysApplication.get().getString(R.string.start_scanning));
        scanControllerTexts.get(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.init(null);
            }
        });
    }

    @Override
    public void onScanUpdate(final int inProgress) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                scanControllerTexts.get(0).setText("Total files scanned: " + inProgress);
            }
        });
    }

    @Override
    public void onScanComplete(Bundle scanResultBundle) {
        progressLayout.setVisibility(View.INVISIBLE);
        shareButton.setVisibility(View.VISIBLE);

        int totalFilesScanned = scanResultBundle.getInt(TOTAL_FILES_KEY, 0);
        double averageFileSize = scanResultBundle.getDouble(AVERAGE_SIZE_KEY, 0.0);
        ArrayList<FileWrapperObject> largestFilesList =
                (scanResultBundle.getParcelableArrayList(LARGEST_FILES_KEY));
        HashMap<String, Integer> mostFrequentExtensions =
                (HashMap<String, Integer>) scanResultBundle
                        .getSerializable(FREQUEST_EXTENSIONS_KEY);

        setInfoLayoutText(
                "Complete scanning " + totalFilesScanned + " files",
                "Name and size of 10 largest files: " + getStringFromFileListDemo(largestFilesList),
                "Average file size: " + MUtils.convertByteToMegabyte(averageFileSize) + " MB",
                "The 5 most frequent file extensions: " + getStringFromHashMapDemo(mostFrequentExtensions));
        infoLayout.setVisibility(View.VISIBLE);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onShareData();
            }
        });
        MUtils.slideUp(infoLayout);
    }

    private void setInfoLayoutText(String title, String msg1, String msg2, String msg3) {
        if (title != null)
            listTextViews.get(0).setText(title);
        if (msg1 != null)
            listTextViews.get(1).setText(msg1);
        if (msg2 != null)
            listTextViews.get(2).setText(msg2);
        if (msg3 != null)
            listTextViews.get(3).setText(msg3);
    }
}
