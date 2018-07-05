package com.miturtow.randomGeneratedNames;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONArray;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final Random random = new Random();
    private static boolean female = false;
    private TextView singleView;
    private GridView batchView;
    private GridView historyView;
    private TextView seekBarText;
    private ArrayList<String> names = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;
    private ConstraintSet current = new ConstraintSet();
    private ConstraintSet single = new ConstraintSet();
    private ConstraintSet batch = new ConstraintSet();
    private ConstraintSet history = new ConstraintSet();
    private ConstraintLayout layout;
    private LinkedHashSet<String> tempHistory = new LinkedHashSet<>();
    private LinkedList<String> historyList = new LinkedList<>();
    private ArrayAdapter<String> historyAdapter;
    private Toast clipboardToast;

    public ConstraintSet getCurrent() {
        return current;
    }

    public void setCurrent(ConstraintSet current) {
        this.current = current;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single);
        layout = findViewById(R.id.single_layout);
        single.clone(this, R.layout.single);
        current.clone(single);
        batch.clone(this, R.layout.batch);
        history.clone(this, R.layout.history);
        historyList = readHistory("history");
        clipboardToast = Toast.makeText(getApplicationContext(), "Copied to clipboard!",
                Toast.LENGTH_SHORT);
        seekBarText = findViewById(R.id.seekBarText);
        SeekBar seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarText.setText(String.valueOf(progress + 3));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        batchView = findViewById(R.id.gridView);
        batchView.setScrollbarFadingEnabled(false);
        batchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String text = parent.getItemAtPosition(position).toString();
                ClipboardManager clipboard = (ClipboardManager)
                        getSystemService(Context.CLIPBOARD_SERVICE);
                if (!text.isEmpty()) {
                    tempHistory.add(text);
                }
                ClipData clipData = ClipData.newPlainText("Copied text", text);
                assert clipboard != null;
                clipboard.setPrimaryClip(clipData);
                clipboardToast.show();
            }
        });
        singleView = findViewById(R.id.text_window);
        singleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = singleView.getText().toString();
                ClipboardManager clipboard = (ClipboardManager)
                        getSystemService(Context.CLIPBOARD_SERVICE);
                tempHistory.add(text);
                ClipData clipData = ClipData.newPlainText("Copied text", text);
                assert clipboard != null;
                clipboard.setPrimaryClip(clipData);
                clipboardToast.show();
            }
        });
        historyView = findViewById(R.id.historyGrid);
        historyView.setScrollbarFadingEnabled(false);
        historyView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String text = parent.getItemAtPosition(position).toString();
                ClipboardManager clipboard = (ClipboardManager)
                        getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("Copied text", text);
                assert clipboard != null;
                clipboard.setPrimaryClip(clipData);
                clipboardToast.show();
            }
        });
        historyView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                historyList.remove(position);
                historyView.setAdapter(historyAdapter);
                return true;
            }
        });
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names);
        historyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, historyList);
        //Buttons
        final ToggleButton singleBatchToggle = findViewById(R.id.singleBatchToggle);
        singleBatchToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (singleBatchToggle.isChecked()) {
                    toggleView(batch);
                    setCurrent(batch);
                } else {
                    toggleView(single);
                    setCurrent(single);
                }
            }
        });
        final ToggleButton historyToggle = findViewById(R.id.historyToggle);
        historyToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (historyToggle.isChecked()) {
                    toggleView(history);
                    if (!tempHistory.isEmpty()) {
                        historyList.addAll(tempHistory);
                        tempHistory.clear();
                    }
                    historyView.setAdapter(historyAdapter);
                } else toggleView(getCurrent());
                persistHistory(historyList, "history");
            }
        });
        Button generate = findViewById(R.id.generate);
        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int length = Integer.parseInt(seekBarText.getText().toString());
                if (singleBatchToggle.isChecked()) {
                    if (names.size() > 0) names.clear();
                    if (female) {
                        genFemale(length, 50);
                    } else {
                        genMale(length, 50);
                    }
                } else {
                    if (female) {
                        singleView.setText(genFemale(length));
                    } else singleView.setText(genMale(length));
                }
            }
        });
        Button cleanHistory = findViewById(R.id.cleanHistory);
        cleanHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (historyList.size() > 0) {
                    historyList.clear();
                    historyView.setAdapter(historyAdapter);
                }
            }
        });
        RadioButton femaleRB = findViewById(R.id.female);
        femaleRB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                female = isChecked;
            }
        });
    }

    private LinkedList<String> readHistory(String fileName) {
        LinkedList<String> result = new LinkedList<>();
        File file = new File(getApplicationContext().getFilesDir(), fileName);
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            result = (LinkedList<String>) ois.readObject();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void persistHistory(LinkedList<String> list, String fileName) {
        try {
            File file = new File(getApplicationContext().getFilesDir(), fileName);
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(list);
            oos.flush();
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void toggleView(ConstraintSet set) {
        Transition transition = new ChangeBounds();
        transition.setInterpolator(new AnticipateOvershootInterpolator(1.0f));
        TransitionManager.beginDelayedTransition(layout, transition);
        set.applyTo(layout);
    }

    private String genMale(int length) {
        StringBuilder name = new StringBuilder();
        List<String> usedPairs = new ArrayList<>();
        ArrayList<String> allowedMaleSuffixes = new ArrayList<>();
        allowedMaleSuffixes.add("o");
        String newPair;
        String previousLetter;
        String newLetter;
        if (chance(30)) {
            if (chance(1)) {
                newPair = Vocabulary.getVowelPair();
                name.append(newPair);
                usedPairs.add(newPair);
            } else {
                name.append(Vocabulary.getVowel());
            }
        } else {
            if (chance(20)) {
                name.append(Vocabulary.getConsonantPair());
            } else {
                name.append(Vocabulary.getConsonant());
            }
        }
        while (name.length() < length && name.length() > 0) {
            previousLetter = name.substring(name.length() - 1);
            if (Vocabulary.isVowelLast(previousLetter)) {
                if (chance(20) && length - name.length() > 1) {
                    name.append(Vocabulary.getConsonantPair());
                } else {
                    newLetter = Vocabulary.getConsonant();
                    if (Vocabulary.notInBlackList(newLetter, previousLetter)) {
                        name.append(newLetter);
                    }
                }
            } else {
                if (chance(5) && length - name.length() > 1) {
                    newPair = Vocabulary.getVowelPair();
                    if (!usedPairs.contains(newPair)) {
                        name.append(newPair);
                        usedPairs.add(newPair);
                    }
                } else {
                    if (length - name.length() == 1) {
                        name.append(Vocabulary.getAllowedVowel(allowedMaleSuffixes));
                    } else {
                        newLetter = Vocabulary.getVowel();
                        if (Vocabulary.notInBlackList(newLetter, previousLetter)) {
                            name.append(newLetter);
                        }
                    }
                }
            }
        }
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    private void genMale(int length, int quantity) {
        while (quantity > 0) {
            names.add(genMale(length));
            quantity--;
        }
        batchView.setAdapter(arrayAdapter);
    }

    private String genFemale(int length) {
        LinkedList<String> name = new LinkedList<>();
        int nameSize = 1;
        List<String> usedPairs = new ArrayList<>();
        ArrayList<String> allowedFemaleSuffixes = new ArrayList<>();
        allowedFemaleSuffixes.add("a");
        allowedFemaleSuffixes.add("i");
        String previousLetter;
        String newLetter;
        String newPair;
        name.add(Vocabulary.getAllowedVowel(allowedFemaleSuffixes));
        while (nameSize < length) {
            previousLetter = name.getLast();
            if (Vocabulary.isVowelLast(previousLetter)) {
                if (chance(20) && length - nameSize > 1) {
                    name.add(Vocabulary.getConsonantPair());
                    nameSize += 2;
                } else {
                    newLetter = Vocabulary.getConsonant();
                    if (Vocabulary.notInBlackList(newLetter, previousLetter)/* && !newLetter.equals("")*/) {
                        name.add(newLetter);
                        nameSize++;
                    }
                }
            } else {
                if (chance(5) && length - nameSize > 1) {
                    newPair = Vocabulary.getVowelPair();
                    if (!usedPairs.contains(newPair)) {
                        name.add(newPair);
                        nameSize += 2;
                        usedPairs.add(newPair);
                    }
                } else {
                    newLetter = Vocabulary.getVowel();
                    if (Vocabulary.notInBlackList(newLetter, previousLetter)/* && !newLetter.equals("")*/) {
                        name.add(newLetter);
                        nameSize++;
                    }
                }
            }
        }
        Iterator iterator = name.descendingIterator();
        StringBuilder result = new StringBuilder();
        while (iterator.hasNext()) {
            result.append(iterator.next());
        }
        return result.substring(0, 1).toUpperCase() + result.substring(1);
    }

    private void genFemale(int length, int quantity) {
        while (quantity > 0) {
            names.add(genFemale(length));
            quantity--;
        }
        batchView.setAdapter(arrayAdapter);
    }

    private boolean chance(int percentage) {
        int roll = random.nextInt(100);
        return roll < percentage;
    }
}
