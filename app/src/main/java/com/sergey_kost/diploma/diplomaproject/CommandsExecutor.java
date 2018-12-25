package com.sergey_kost.diploma.diplomaproject;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.provider.AlarmClock;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

class CommandsExecutor {

    private Context context;
    private String[] commandValueArr;
    String string;

    CommandsExecutor(Context context, String str) {
        this.context = context;
        this.string = str;
        commandValueArr = str.split(" ");
        commandExecution();
    }

    private void commandExecution() {
        if (commandValueArr.length < 2) {
            Toast.makeText(context, "Неправильная команда, для просмотра команд откройте \"Справка\"",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        switch (commandValueArr[0].toLowerCase()) {
            case "яркость":
                brightnessFunction();
                break;

            case "wi-fi":
            case "вайфай":
            case "вай-фай":
                wifiFunction();
                break;

            case "bluetooth":
            case "блютуз":
                bluetoothFunction();
                break;

            case "camera":
            case "камера":
                cameraFunction();
                break;

            case "video":
            case "видео":
                videoFunction();
                break;

            case "позвонить":
            case "звонить":
                callFunction();
                break;

            case "sms":
            case "смс":
                smsFunction();
                break;

            case "будильник":
                alarmClockFunction();
                break;

            case "timer":
            case "таймер":
                timerFunction();
                break;

            default:
                break;
        }

        String command = commandValueArr[0] + " " + commandValueArr[1];
        switch (command) {
            // make a choise of three: volume of rington, media and alarmclock
            case "громкость ringtone":
            case "громкость рингтон":
            case "громкость рингтона":
                ringVolumeFunction();
                break;

            case "громкость media":
            case "громкость медиа":
                mediaVolumeFunction();
                break;

            case "громкость будильник":
            case "громкость будильника":
                alarmClockVolumeFunction();
                break;

            case "без звука":
                soundlessFunction();
                break;

            default:
                break;
        }
    }

    private void brightnessFunction() {
        if (commandValueArr[1].equals("авто")) {
            Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
            return;
        }
        int brightness = valueChecking(commandValueArr[1]);
        if (brightness == -1) {
            return;
        }
        brightness = (int) (brightness * 2.55);
        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightness);
    }

    private void wifiFunction() {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        switch (commandValueArr[1]) {
            case "включить":
                wifiManager.setWifiEnabled(true);
                break;
            case "выключить":
                wifiManager.setWifiEnabled(false);
                break;
            default:
                Toast.makeText(context, "Допустимые значения: включить/выключить", Toast.LENGTH_LONG).show();
                break;
        }
    }

    private void bluetoothFunction() {
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        switch (commandValueArr[1]) {
            case "включить":
                btAdapter.enable();
                break;
            case "выключить":
                btAdapter.disable();
                break;
            default:
                Toast.makeText(context, "Допустимые значения: включить/выключить", Toast.LENGTH_LONG).show();
                break;
        }
    }

    private void cameraFunction() {
        switch (commandValueArr[1]) {
            case "открыть":
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                context.startActivity(cameraIntent);
                break;
            default:
                Toast.makeText(context, "Допустимые значения: открыть", Toast.LENGTH_LONG).show();
                break;
        }
    }

    private void videoFunction() {
        switch (commandValueArr[1]) {
            case "открыть":
                Intent cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                context.startActivity(cameraIntent);
                break;
            default:
                Toast.makeText(context, "Допустимые значения: открыть", Toast.LENGTH_LONG).show();
                break;
        }
    }

    private void callFunction() {
        Map<String, String> map = new HashMap<>();
        /* Get contact name */
        StringBuilder contact = new StringBuilder();
        for (int i = 1; i < commandValueArr.length; i++) {
            contact.append(commandValueArr[i]).append(" ");
        }
        contact.deleteCharAt(contact.length() - 1);
        /*------------------*/
        /* Getting all contacts from phone */
        try (Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null)) {
            while (phones.moveToNext()) {
                String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)).toLowerCase();
                String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                map.put(name, phoneNumber);
            }
            /*---------------------------*/

            /* Check contact and make a call */
            if (map.containsKey(contact.toString())) {
                String phoneToCall = map.get(contact.toString());

                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneToCall));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "Неправильное имя контакта", Toast.LENGTH_SHORT).show();
            }
            /*-------------------------------*/
        }
    }

    private void smsFunction() {
        Map<String, String> mapForSms = new HashMap<>();
        /* Get contact name */
        StringBuilder contactForSms = new StringBuilder();
        StringBuilder smsText = new StringBuilder();

        for (int i = 1; i < commandValueArr.length; i++) {
            if (commandValueArr[i].equals("текст")) {
                if (commandValueArr.length > i + 1) {
                    for (int k = i + 1; k < commandValueArr.length; k++) {
                        smsText.append(commandValueArr[k]).append(" ");
                    }
                    smsText.deleteCharAt(smsText.length() - 1);
                    break;
                } else {
                    Toast.makeText(context, "Отсутствует текст смс", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            contactForSms.append(commandValueArr[i]).append(" ");
        }
        contactForSms.deleteCharAt(contactForSms.length() - 1);
        /*------------------*/

        /* Getting all contacts from phone */
        try (Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null)) {
            while (phones.moveToNext()) {
                String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)).toLowerCase();
                String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                mapForSms.put(name, phoneNumber);
            }
            /*---------------------------*/

            /* Check contact and feel sms fields*/
            if (mapForSms.containsKey(contactForSms.toString())) {
                String phoneToSms = mapForSms.get(contactForSms.toString());

                Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                smsIntent.setType("vnd.android-dir/mms-sms");
                smsIntent.putExtra("address", phoneToSms);
                smsIntent.putExtra("sms_body", smsText.toString());
                context.startActivity(smsIntent);
            } else {
                Toast.makeText(context, "Неправильное имя контакта", Toast.LENGTH_SHORT).show();
            }
            /*---------------------------*/
        }
    }

    private void alarmClockFunction() {
        int hours, minutes;

        String[] array;
        try {
            array = commandValueArr[1].split(":");
            hours = Integer.parseInt(array[0]);
            minutes = Integer.parseInt(array[1]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            Toast.makeText(context, "Неправильное время", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder message = new StringBuilder();
        if (commandValueArr.length > 2) {
            for (int i = 2; i < commandValueArr.length; i++) {
                message.append(commandValueArr[i]).append(" ");
            }
            message.deleteCharAt(message.length() - 1);
        } else {
            message.append("будильник");
        }
        message.replace(0, 1, String.valueOf(message.charAt(0)).toUpperCase());

        Intent alarmClock = new Intent(AlarmClock.ACTION_SET_ALARM);
        alarmClock.putExtra(AlarmClock.EXTRA_MESSAGE, message.toString());
        alarmClock.putExtra(AlarmClock.EXTRA_HOUR, hours);
        alarmClock.putExtra(AlarmClock.EXTRA_MINUTES, minutes);
        alarmClock.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
        context.startActivity(alarmClock);
    }

    private void timerFunction() {
        int minutes = 0;
        int seconds = 0;
        for (int i = 0; i < commandValueArr.length; i++) {
            try {
                if (commandValueArr[i].contains("минут")) {
                    minutes = Integer.parseInt(commandValueArr[i - 1]);
                }
                if (commandValueArr[i].contains("секунд")) {
                    seconds = Integer.parseInt(commandValueArr[i - 1]);
                }
            } catch (NumberFormatException e) {
                Toast.makeText(context, "Неправильное время", Toast.LENGTH_SHORT).show();
            }
        }
        int time = minutes * 60 + seconds;

        Intent timer = new Intent(AlarmClock.ACTION_SET_TIMER);
        timer.putExtra(AlarmClock.EXTRA_MESSAGE, "Время истекло!");
        timer.putExtra(AlarmClock.EXTRA_LENGTH, time);

        context.startActivity(timer);
    }

    private void ringVolumeFunction() {
        if (!lengthChecking()) {
            return;
        }
        int volumeRing = valueChecking(commandValueArr[2]);
        if (volumeRing == -1) {
            return;
        }
        volumeRing *= 0.15;
        AudioManager audioManagerRing = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManagerRing.setStreamVolume(AudioManager.STREAM_RING, volumeRing, 0);
    }

    private void mediaVolumeFunction() {
        if (!lengthChecking()) {
            return;
        }
        int volumeMedia = valueChecking(commandValueArr[2]);
        if (volumeMedia == -1) {
            return;
        }
        volumeMedia *= 0.15;
        AudioManager audioManagerMedia = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManagerMedia.setStreamVolume(AudioManager.STREAM_MUSIC, volumeMedia, 0);
    }

    private void alarmClockVolumeFunction() {
        if (!lengthChecking()) {
            return;
        }
        int volumeAlarmClock = valueChecking(commandValueArr[2]);
        if (volumeAlarmClock == -1) {
            return;
        }
        volumeAlarmClock *= 0.15;
        AudioManager audioManagerAlarmclock = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManagerAlarmclock.setStreamVolume(AudioManager.STREAM_ALARM, volumeAlarmClock, 0);
    }

    private void soundlessFunction() {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        switch (commandValueArr[2]) {
            case "включить":
                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                break;
            case "выключить":
                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                break;
            default:
                Toast.makeText(context, "Доступные значения: включить/выключить", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private int valueChecking(String str) {
        int a;
        try {
            a = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            Toast.makeText(context, "NumberFormatException Допустимые значения: [0;100]", Toast.LENGTH_SHORT).show();
            a = -1;
        } catch (ArrayIndexOutOfBoundsException e) {
            Toast.makeText(context, "ArrayOut Допустимые значения: [0;100]", Toast.LENGTH_SHORT).show();
            a = -1;
        }
        if (a < 0 || a > 100) {
            Toast.makeText(context, "Допустимые значения: [0;100]", Toast.LENGTH_SHORT).show();
            a = -1;
        }
        return a;
    }

    private boolean lengthChecking() {
        boolean lc;
        if (commandValueArr.length < 3) {
            Toast.makeText(context, "Неправильное значение громкости", Toast.LENGTH_SHORT).show();
            lc = false;
        } else {
            lc = true;
        }
        return lc;
    }
}
