package com.serge45.app.seats;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

public class MainActivity extends Activity {
    private enum SelectionMode {
        detail,
        swap
    };
    
    public class ButtonWithInformation {
        Button button;
        String name = "";
        int number = 0;
        boolean enabled;
        float rating = 5;
        String note = "";
    };
    
    static String TAG = "Seats";
    private String saveDir = "";
    private String jsonFileName = "seats.json";
    private String numToNameFileName = "names.json";
    private SparseArray<String> numToName;
    private TableLayout tableLayout;
    private Button restoreButton;
    private Button shuffleButton;
    private Button saveButton;
    private Button randomChooseButton;
    private ButtonWithInformation chosenButton;
    private int rowCount = 6;
    private int colCount = 7;
    private Map<Pair<Integer, Integer>, ButtonWithInformation> seatButtonsMap = new HashMap<Pair<Integer, Integer>, ButtonWithInformation>();
    private SelectionMode selectionMode = SelectionMode.detail;
    private List<ButtonWithInformation> swapPair = new ArrayList<ButtonWithInformation>();
    private Runnable randomChooseRunner;
    private int currentIterationCount = 0;
    private Handler handler;
    private long[] iteratorPeriods = new long[20];
    final private int maxRandomIterationCount = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initSeatButtons();
        initListeners();
        initJsonLoadPath();
        initRandomChooseTimer();
        try {
            loadJsonAndInit();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        generateNumToNameMap();
    }

    @Override
    public void onBackPressed() {
        if (selectionMode == SelectionMode.swap) {
            switchSelectionMode();
            return;
        } else {
            super.onBackPressed();
        }
    }
    
    private void initRandomChooseTimer() {
        for (int i = 0; i < iteratorPeriods.length; ++i) {
            iteratorPeriods[i] = 2 * (i + 1) * (i + 1); 
        }

        handler = new Handler();

        randomChooseRunner = new Runnable() {
            @Override
            public void run() {
                if (currentIterationCount < maxRandomIterationCount) {
                    randomChooseButton();
                    ++currentIterationCount;
                    handler.postDelayed(randomChooseRunner, iteratorPeriods[currentIterationCount - 1]);
                } else {
                    currentIterationCount = 0;
                    randomChooseButton.setEnabled(true);
                }
            }
        };
        
    }

    private void initJsonLoadPath() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            saveDir = Environment.getExternalStorageDirectory()
                             + File.separator 
                             + getString(R.string.app_name);
            File dir = new File(saveDir);

            if (!dir.exists()) {
                dir.mkdirs();
            }

        }
        
    }

    private void loadJsonAndInit() throws IOException, JSONException {
        String filePath = saveDir + File.separator + jsonFileName;
        
        File file = new File(filePath);

        if (!file.exists()) {
            AssetManager assetManager = getAssets();
            InputStream assetIn = assetManager.open(jsonFileName);
            FileOutputStream out = new FileOutputStream(filePath);
            /*Copy asset to sd card.*/
            byte[] buffer = new byte[1024];
            int n = -1;
            
            while ((n = assetIn.read(buffer)) != -1) {
                out.write(buffer);
            }
            assetIn.close();
            out.close();
            buffer = null;
        }
        
        file = null;
        
        file = new File(filePath);
        StringBuilder result = new StringBuilder();
        BufferedReader reader = null;
        
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            reader.close();
        } catch (IOException e) {
            if (reader != null) {
                reader.close();
            }
        }

        JSONArray src = new JSONArray(result.toString());
        
        for (int i = 0; i < src.length(); ++i) {
            JSONObject obj = (JSONObject) src.get(i);
            int r = obj.getInt("row");
            int c = obj.getInt("col");
            String name = obj.getString("name");
            int num = obj.getInt("num");
            
            float rating = 5;
            String note = "";
            
            try {
                rating = (float) obj.getDouble("rating");
                String tmpNote = obj.getString("note");
                note = tmpNote;
            } catch (JSONException e) {
                note = "";
                e.printStackTrace();
            }
            
            ButtonWithInformation btn = seatButtonsMap.get(Pair.create(r, c));
            btn.button.setText(name);
            btn.name = name;
            btn.number = num;
            btn.rating = rating;
            btn.note = note;
            
            if (name.length() > 0) {
                btn.enabled = true;
                btn.button.setEnabled(true);
            }
        }
    }
    
    private void generateNumToNameMap() {
        numToName = new SparseArray<String>();
        
        for (Map.Entry<Pair<Integer, Integer>, ButtonWithInformation> entry : seatButtonsMap.entrySet()) {
            numToName.put(entry.getValue().number,
                          entry.getValue().name
                          );
        }
    }

    protected void initViews() {
        tableLayout = (TableLayout) findViewById(R.id.seats_table_layout);
        tableLayout.setStretchAllColumns(true);
        
        restoreButton = (Button) findViewById(R.id.restore_button); 
        shuffleButton = (Button) findViewById(R.id.shuffle_button); 
        saveButton = (Button) findViewById(R.id.save_button); 
        randomChooseButton = (Button) findViewById(R.id.random_choose_button); 
    }
    
    protected void initListeners() {
        saveButton.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                try {
                    saveAsJson();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        
        restoreButton.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if (chosenButton != null) {
                    chosenButton.button.setBackgroundResource(android.R.drawable.btn_default);
                }

                try {
                    loadJsonAndInit();
                    generateNumToNameMap();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        
        randomChooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                randomChooseButton.setEnabled(false);
                handler.postDelayed(randomChooseRunner, iteratorPeriods[currentIterationCount]);
            }
        });
        
        shuffleButton.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                shuffleSeatButtons();
            }
        });
        
        for (final Map.Entry<Pair<Integer, Integer>, ButtonWithInformation> entry : seatButtonsMap.entrySet()) {
            Button btn = entry.getValue().button;

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectionMode == SelectionMode.swap) {
                        swapPair.add(entry.getValue());
                        swapPair.get(swapPair.size() - 1).button.setBackgroundColor(Color.YELLOW);

                        if (swapPair.size() == 2) {
                            String tmp = swapPair.get(0).name;
                            swapPair.get(0).button.setText(swapPair.get(1).name);
                            swapPair.get(0).name = swapPair.get(1).name;
                            swapPair.get(1).name = tmp;
                            swapPair.get(1).button.setText(tmp);
                            String tmpNote;
                            tmpNote = swapPair.get(0).note;
                            swapPair.get(0).note = swapPair.get(1).note;
                            swapPair.get(1).note = tmpNote;
                            float tmpRating = 5.f;
                            tmpRating = swapPair.get(0).rating;
                            swapPair.get(0).rating = swapPair.get(1).rating;
                            swapPair.get(1).rating = tmpRating;

                            for (ButtonWithInformation b : swapPair) {
                                b.button.setBackgroundResource(android.R.drawable.btn_default);
                            }
                            swapPair.clear();
                        }
                    } else if (selectionMode == SelectionMode.detail) {
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        Fragment prev = getFragmentManager().findFragmentByTag("detail");
                        if (prev != null) {
                            ft.remove(prev);
                        }
                        ft.addToBackStack(null);
                        Button b = (Button) v; 
                        String name = b.getText().toString();
                        int num = 0;
                        
                        for (int i = 0; i < numToName.size(); ++i) {
                            if (numToName.valueAt(i).equals(name)) {
                                num = numToName.keyAt(i);
                                break;
                            }
                        }

                        StudentDetailDialog.numToName = numToName;
                        StudentDetailDialog newFragment = StudentDetailDialog.newInstance(name, num);
                        newFragment.setTarget(entry.getValue());
                        newFragment.show(ft, "detail");
                    }
                }
            });
        }
    }
    
    private void initSeatButtons() {
        for (int i = 0; i < rowCount; ++i) {
            TableRow row = new TableRow(this);

            for (int j = 0; j < colCount; ++j) { 
                Button b = new Button(this);
                b.setText("");
                b.setEnabled(false);
                b.setOnLongClickListener(new View.OnLongClickListener() {
                    
                    @Override
                    public boolean onLongClick(View v) {
                        switchSelectionMode();
                        tableLayout.requestLayout();
                        Log.d(TAG, "Long pressed");
                        return true;
                    }
                });

                row.addView(b, new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                                                         TableLayout.LayoutParams.MATCH_PARENT, 
                                                         1.f)
                );
                ButtonWithInformation btn = new ButtonWithInformation();
                btn.button = b;
                btn.enabled = false;
                btn.name = "";
                btn.number = 0;
                
                seatButtonsMap.put(Pair.create(i, j), btn);
            }
            tableLayout.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, 
                                                                  TableLayout.LayoutParams.MATCH_PARENT,
                                                                  1.f)
            );
        }
    }
    
    private void saveAsJson() throws JSONException, IOException {

        JSONArray target = new JSONArray();
        
        for (Map.Entry<Pair<Integer, Integer>, ButtonWithInformation> entry : seatButtonsMap.entrySet()) {
            Pair<Integer, Integer> key = entry.getKey();
            ButtonWithInformation value = entry.getValue();
            JSONObject obj = new JSONObject();
            obj.put("name", value.name);
            obj.put("note", value.note);
            obj.put("num", value.number);
            obj.put("state", value.enabled);
            obj.put("rating", value.rating);
            obj.put("row", key.first);
            obj.put("col", key.second);
            target.put(obj);
        }
    
        try {
            FileOutputStream output = new FileOutputStream(saveDir + File.separator + jsonFileName);
            output.write(target.toString().getBytes(Charset.forName("UTF-8")));
            output.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    private void randomChooseButton() {
        if (chosenButton != null) {
            chosenButton.button.setBackgroundResource(android.R.drawable.btn_default);
        }

        Random r = new Random();
        
        chosenButton = null;
        
        while (chosenButton == null || chosenButton.button.isEnabled() == false) {
            Pair<Integer, Integer> key = Pair.create(r.nextInt(rowCount),
                                                     r.nextInt(colCount)
                                                     );
            chosenButton = seatButtonsMap.get(key);
        }

        chosenButton.button.setBackgroundColor(Color.GREEN);
    }
    
    private void shuffleSeatButtons() {
        final List<Integer> numList = new ArrayList<Integer>();
        
        for (int i = 0; i < numToName.size(); ++i) {
            Integer key = numToName.keyAt(i);
            if (numToName.valueAt(i).length() > 1) {
                numList.add(key);
            }
        }
        
        Collections.shuffle(numList);
        
        int k = 0;

        for (int i = 0; i < rowCount; ++i) {
            for (int j = 0; j < colCount; ++j) {
                ButtonWithInformation b = seatButtonsMap.get(Pair.create(i, j));
                if (b != null && b.enabled) {
                    b.number = numList.get(k);
                    b.name = numToName.get(b.number);
                    b.button.setText(b.name);
                    ++k;
                }
            }
        }
    }
    
    private void switchSelectionMode() {
        if (selectionMode == SelectionMode.detail) {
            selectionMode = SelectionMode.swap;
            Random random = new Random();
            
            for (Map.Entry<Pair<Integer, Integer>, ButtonWithInformation> entry : seatButtonsMap.entrySet()) {
                if (entry.getValue().enabled == false) {
                    continue;
                }

                Button btn = entry.getValue().button;
                int offset = random.nextInt(50);
                Animation animation = AnimationUtils.loadAnimation(this, R.anim.shake);
                animation.setRepeatCount(Animation.INFINITE);
                animation.setStartOffset(offset);
                btn.startAnimation(animation);
            }
        } else if (selectionMode == SelectionMode.swap) {
            selectionMode = SelectionMode.detail;

            for (Map.Entry<Pair<Integer, Integer>, ButtonWithInformation> entry : seatButtonsMap.entrySet()) {
                Button btn = entry.getValue().button;
                Animation animation = btn.getAnimation();

                if (animation != null) {
                    animation.cancel();
                    btn.clearAnimation();
                }
            }
        }
    }

}
