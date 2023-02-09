package com.vuzix.blade.devkit.storage_sample;

import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.widget.EditText;

import com.vuzix.hud.actionmenu.ActionMenuActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;

/**
 * Main Activity that extend ActionMenuActivity.
 * This main class provide the basic information for write to storage.
 * For more information please reference:
 * https://developer.android.com/training/data-storage/files.
 * Used Android API Classes:
 * https://developer.android.com/reference/android/os/Environment
 */
public class MainActivity extends ActionMenuActivity {

    private final String TAG = "VuzixBDK-Storage_Sample";
    private File sampleFile;
    private EditText textArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textArea = (EditText)findViewById(R.id.editText);

        /*

        It is always best to use available methods to get external storage information like path, context, and other attributes. Many of these methods are available in the
        android.os.Environment class.

        In order to access external storage, the Android Manifest for your application must specify appropriate permissions. See this projects' AndroidManifest.xml file
        for more information.

        For more information on storage types (Internal, External, SharedPreferences, etc.) please see https://developer.android.com/guide/topics/data/data-storage.

         */
        sampleFile = new File(Environment.getExternalStorageDirectory() + "/sample-file.txt");



    }

    @Override
    protected void onResume() {
        super.onResume();

        logStorageInfo();

        // It's recommended to make sure the external storage is available and can be written to before writing to it.
        if (isExternalStorageWritable()) {

            textArea.append("Attempting to Write to storage \n");
            this.writeFileToExternalStorage();

        } else {

            Log.e(TAG, "Cannot write to external storage");
            textArea.setText("");
            textArea.append("ERROR: \n");
            textArea.append("Cannot write to external storage");

        }

        // It's recommended to make sure the external storage is available and can be read from before reading from it.
        if (isExternalStorageReadable()) {

            textArea.append("Attempting to READ from storage \n");
            this.readFileFromExternalStorage();

        }

        else {

            Log.e(TAG, "Cannot read from external storage");
            textArea.setText("");
            textArea.append("ERROR: \n");
            textArea.append("Cannot read from external storage");

        }
    }

    /*

        Simple attributes on the external storage directory

         */
    private void logStorageInfo() {
        Log.d(TAG, "Location: " + Environment.getExternalStorageDirectory());
        Log.d(TAG, "State: " + Environment.getExternalStorageState());
        Log.d(TAG, "Removable: " + Environment.isExternalStorageRemovable(sampleFile));
        Log.d(TAG, "Emulated: " + Environment.isExternalStorageEmulated(sampleFile));
        Log.d(TAG, "Path: " + Environment.getExternalStorageDirectory().getAbsolutePath());
        Log.d(TAG, "SD mount state: " + isSdMounted());
        Log.d(TAG, "Internal Space: " + getAvailableSpaceInMBInternal() + " MB");
        if (isSdMounted()) {
            Log.d(TAG, "SD card free space: " + getAvailableSpaceInMbSD() + " MB");
        }

        textArea.append("Location: " + Environment.getExternalStorageDirectory() + "\n");
        textArea.append("State: " + Environment.getExternalStorageState() + "\n");
        textArea.append("Removable: " + Environment.isExternalStorageRemovable(sampleFile)  + "\n");
        textArea.append("Emulated: " + Environment.isExternalStorageEmulated(sampleFile)  + "\n");
        textArea.append("Internal Space: " + getAvailableSpaceInMBInternal() + " MB"  + "\n");
        textArea.append("SD card mounted: " +  isSdMounted() + "\n");
        if (isSdMounted()) {
            textArea.append("SD card free space: " + getAvailableSpaceInMbSD() + " MB" + "\n");
        }
    }
    /*

    Gets space of internal storage in MB

    */
    public static long getAvailableSpaceInMBInternal(){
        final long SIZE_KB = 1024L;
        final long SIZE_MB = SIZE_KB * SIZE_KB;
        long availableSpace = -1L;
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        availableSpace = stat.getAvailableBlocksLong() * stat.getBlockSizeLong();
        return availableSpace/SIZE_MB;
    }
    /*

    Gets space of SD card storage in MB

    */
    public static long getAvailableSpaceInMbSD(){
        final long SIZE_KB = 1024L;
        final long SIZE_MB = SIZE_KB * SIZE_KB;
        long availableSpace = -1L;
        StatFs stat = new StatFs(Environment.getRootDirectory().getParent() + "/storage/external/");
        availableSpace = stat.getAvailableBlocksLong() * stat.getBlockSizeLong();
        return availableSpace/SIZE_MB;
    }
    /*

    Checks if an sd card is mounted returns true or false

    */
    private boolean isSdMounted(){
        File file = new File("/storage/external/"); //hardcoded path to external storage as getExternalStorageDirectory() returns emulated storage.
        return android.os.Environment.getExternalStorageState(file).equals(android.os.Environment.MEDIA_MOUNTED) &&
                android.os.Environment.isExternalStorageRemovable(file);
    }

    /*

    Checks if external storage is available for read and write

    */
    private boolean isExternalStorageWritable() {

        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());

    }

    /*

    Checks if external storage is available to at least read

    */
    private boolean isExternalStorageReadable() {

        String state = Environment.getExternalStorageState();

        return (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state));

    }


    private void writeFileToExternalStorage() {

        try {

            sampleFile.createNewFile();

            FileOutputStream outputStream = new FileOutputStream(sampleFile);
            OutputStreamWriter streamWriter = new OutputStreamWriter(outputStream);
            streamWriter.append("VUZIX BLADE");
            streamWriter.close();

            outputStream.flush();
            outputStream.close();

        } catch (Exception ex) {

            Log.e(TAG, "Error writing to file: " + ex.toString());
            textArea.setText("");
            textArea.append("ERROR: \n");
            textArea.append("Error writing to file: " + ex.toString());

        }

    }

    private void readFileFromExternalStorage() {

        String fileContent = "";

        if (sampleFile.exists()) {

            BufferedReader reader;

            try {

                reader = new BufferedReader(new FileReader(sampleFile));

                String line;

                while ((line = reader.readLine()) != null) {

                    fileContent += line;

                }

                reader.close();

            } catch (Exception ex) {

                Log.e(TAG, "Error reading from file: " + ex.toString());
                textArea.setText("");
                textArea.append("ERROR: \n");
                textArea.append("Error reading from file: " + ex.toString());
            }
        }

        Log.d(TAG, "File contents: " + fileContent);
        textArea.append("File contents: " + fileContent + "\n");

    }


}
