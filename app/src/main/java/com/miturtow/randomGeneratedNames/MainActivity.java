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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.miturtow.android.random_generated_names.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final Random random = new Random();
    private static boolean female = false;
    private static boolean toggle = false;
    private TextView singleView;
    private GridView batchView;
    private TextView seekBarText;
    private ArrayList<String> names = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;
    private String copiedText = "";
    private ConstraintSet single = new ConstraintSet();
    private ConstraintSet batch = new ConstraintSet();
    private ConstraintLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single);
        layout = findViewById(R.id.single_layout);
        single.clone(this, R.layout.single);
        batch.clone(this, R.layout.batch);
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
        singleView = findViewById(R.id.text_window);
        singleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                copiedText = singleView.getText().toString();
                ClipData clipData = ClipData.newPlainText("Copied text", copiedText);
                assert clipboard != null;
                clipboard.setPrimaryClip(clipData);
                Toast.makeText(getApplicationContext(), "Copied to clipboard!", Toast.LENGTH_SHORT).show();
            }
        });
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names);
        ToggleButton toggleButton = findViewById(R.id.toggleButton);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggle = isChecked;
            }
        });
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toggle) {
                    toggleView(batch);
                } else toggleView(single);
            }
        });
        Button generate = findViewById(R.id.generate);
        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int length = Integer.parseInt(seekBarText.getText().toString());
                if (toggle) {
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
        RadioButton femaleRB = findViewById(R.id.female);
        femaleRB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                female = isChecked;
            }
        });
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
