package com.sergey_kost.diploma.diplomaproject;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RecognitionListener {

    TextView speechReturnedText;
    ProgressBar progressBar;
    FloatingActionButton fabBtn;
    SpeechRecognizer speechRecognizer = null;
    Intent speechRecognizerIntent;
    final int REQUEST_PERMISSION = 1;
    private String command;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        speechReturnedText = findViewById(R.id.speechTextView);
        fabBtn = findViewById(R.id.floatingActionButton);
        progressBar = findViewById(R.id.progressBar1);
    }

    @Override
    public void onResume() {
        super.onResume();

        checkPermissions();

        progressBar.setVisibility(View.INVISIBLE);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(this);
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        //Adding extra RecognizerIntent
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "ru-RU");
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        //Input recording time 3 seconds
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 3000);
        speechRecognizerIntent.putExtra("android.speech.extra.DICTATION_MODE", true);

        fabBtn.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            speechRecognizer.startListening(speechRecognizerIntent);
            fabBtn.setEnabled(false);
            Snackbar snackbar = Snackbar.make(v, "Очистить текст", Snackbar.LENGTH_INDEFINITE)
                    .setAction("ОЧИСТИТЬ", view -> {
                        speechReturnedText.setText("");
                        Snackbar snackbar1 = Snackbar.make(view, "Текст был очищен", Snackbar.LENGTH_SHORT);
                        snackbar1.show();
                    });
            snackbar.setActionTextColor(getColor(R.color.colorPrimaryDark));
            snackbar.show();
        });
    }

    private void checkPermissions() {
        String[] PERMISSIONS = {Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_CONTACTS,
                Manifest.permission.CALL_PHONE, Manifest.permission.SEND_SMS, Manifest.permission.CHANGE_WIFI_STATE};
        if (!Permissions.hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSION);
        }

        NotificationManager n = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (n != null && !n.isNotificationPolicyAccessGranted()) {
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            startActivity(intent);
        }

        if (!Settings.System.canWrite(this)) {
            // If do not have write settings permission then open the Can modify system settings panel.
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + this.getPackageName()));
            startActivity(intent);
        }
    }

    public static String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Ошибка записи звука";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Ошибка на стороне клиента";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Нет разрешения";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Ошибка сети";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Тайм-аут сети";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "Нет совпадений";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService занят";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "Ошибка с сервера";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "Нет голосового ввода";
                break;
            default:
                message = "Не распознал. Повторите, пожалуйста, ещё раз.";
                break;
        }
        return message;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBeginningOfSpeech() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
    }

    @Override
    public void onEndOfSpeech() {
        progressBar.setVisibility(View.INVISIBLE);
        fabBtn.setEnabled(true);
    }

    @Override
    public void onError(int errorCode) {
        String errorMessage = getErrorText(errorCode);
        progressBar.setVisibility(View.INVISIBLE);
        speechReturnedText.setText(errorMessage);
        fabBtn.setEnabled(true);
    }

    @Override
    public void onPartialResults(Bundle arg0) {
        ArrayList<String> matches = arg0.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = "";
        text = matches.get(0);
        command = text.toLowerCase();
        text = text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
        speechReturnedText.setText(text);
    }

    @Override
    public void onReadyForSpeech(Bundle arg0) {
    }

    @Override
    public void onResults(Bundle results) {
        new CommandsExecutor(this, command);
    }

    @Override
    public void onEvent(int arg0, Bundle arg1) {
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        progressBar.setProgress((int) rmsdB);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
        return true;
    }
}