package com.jbjust.fileloader;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static com.jbjust.fileloader.R.layout.*;

public class FileLoader extends Activity {
    protected static final int TIMER_RUNTIME = 10000; // in ms --> 10s

    protected boolean mbActive;
    protected ProgressBar mProgressBar;
    public final String filename = "files.txt";
    static final int READ_BLOCK_SIZE = 100;
    ListView listView;
    public ListView mainListView ;
    public ArrayAdapter<String> listAdapter ;

    /**
     * This method lists what needs to be done when the app is run.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_file_loader);
        mProgressBar = (ProgressBar)findViewById(R.id.adprogress_progressBar);
        mainListView = (ListView) findViewById(R.id.list);
        System.out.println(mProgressBar.getMax());
        listView=(ListView)findViewById(R.id.list);
    }

    /**
     * This method specifies the destructor
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * This method updates the progress bar according to the percent passed into it.
     * @param percent the percentage of the progress bar that should be full
     */
    public void updateProgress(final int percent) {
        if(null != mProgressBar) {
            final int progress = percent;
            mProgressBar.setProgress(progress);
        }
    }

    public void onContinue() {

    }

    /**
     * This method is called when the "create" button is pushed and creates a file that has numbers
     * 1-10 stored in it, with each number on its own line.
     * @param view
     */
    public void create(View view) {
        Thread cThread = new Thread(new Runnable() {
            @Override
            public void run() {
                String string = "";
                int prog = 0;
                int maxNum = 10;
                updateProgress(0);
                FileOutputStream outputStream;
                try {
                    outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                    for (int i = 1; i <= maxNum; i++) {
                        string = i + "\n";
                        outputStream.write(string.getBytes());
                        prog += 100 / maxNum;
                        updateProgress(prog);
                        Thread.sleep(250);
                    } outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        cThread.start();
    }

    /**
     * This method is called when the "load" button is pushed and reads in the file that was just
     * written and then pushes the read-in file to the ListView
     * @param view
     */
    public void load(View view) {
        Thread lThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FileInputStream inputStream = openFileInput(filename);
                    InputStreamReader InputRead = new InputStreamReader(inputStream);
                    char[] inputBuffer = new char[READ_BLOCK_SIZE];
                    String s = "";
                    int charRead;
                    int prog = 0;
                    updateProgress(prog);
                    while ((charRead = InputRead.read(inputBuffer)) > 0) {
                        String readstring = String.copyValueOf(inputBuffer, 0, charRead);
                        s += readstring;
                    } InputRead.close();
                    final ArrayList numList = new ArrayList();
                    final String[] nums = s.split("\n");
                    listAdapter = new ArrayAdapter<String>(FileLoader.this, com.jbjust.fileloader.R.layout.simplerow, numList);
                    for (int i = 0; i < nums.length; i++) {
                        numList.add(nums[i]);
                        prog += 100 / nums.length;
                        updateProgress(prog);
                        Thread.sleep(250);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mainListView.setAdapter(listAdapter);
                                listAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        lThread.start();
    }

    /**
     * This method is called when the "clear" button is pushed and clears the progress bar and the
     * ListView of all data.
     * @param view
     */
    public void clear(View view) {
        listAdapter.clear();
        listAdapter.notifyDataSetChanged();
        updateProgress(0);
    }
}