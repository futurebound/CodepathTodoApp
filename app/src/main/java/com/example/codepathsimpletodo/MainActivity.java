package com.example.codepathsimpletodo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the source code for our application
 */
public class MainActivity extends AppCompatActivity {

    List<String> items;

    // for EACH element in activity_main.xml (Button, TextField, View)
    Button buttonAdd;
    EditText editTextItem;
    RecyclerView rvItems;

    ItemsAdapter itemsAdapter;

    /**
     * onCreate() called by Android system an informs us that the application
     * MainActivity has been created. basically a constructor
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // this targets the activity_main as the content
                                                // our our activity, which is how we see the UI
                                                // when running the app

        // allows us to actually find the elements we created in the design portion of the XML file
        buttonAdd = findViewById(R.id.buttonAdd);
        editTextItem = findViewById(R.id.editTextItem);
        rvItems = findViewById(R.id.rvItems);

        // load old data or empty list if can't access
        loadItems();

        ItemsAdapter.OnLongClickListener onLongClickListener = new ItemsAdapter.OnLongClickListener() {
            @Override
            public void onItemLongClicked(int position) {
                // Delete item from the model
                items.remove(position);

                // notify the adapter of delete for render
                itemsAdapter.notifyItemRemoved(position);
                Toast.makeText(getApplicationContext(), "Item removed!", Toast.LENGTH_SHORT).show();
                saveItems();
            }
        };

        itemsAdapter = new ItemsAdapter(items, onLongClickListener);
        rvItems.setAdapter(itemsAdapter);
        rvItems.setLayoutManager(new LinearLayoutManager(this));

        // sets up event listener so that we can trigger adding items
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // someone has tapped the ADD button
                // get the text they typed into the editTextItem
                String todoItem = editTextItem.getText().toString();

                // generate a new item and add it to the model
                items.add(todoItem);

                // notify adapter that an item has been inserted
                itemsAdapter.notifyItemInserted(items.size() - 1);

                // clear text window once it's been added
                editTextItem.setText("");

                // notify user they have successfully added item
                // Toast --> an ephemeral notification that shows up and disappears
                // "Item added!" --> the text that shows up
                // LENGTH_SHORT --> how long it stays up before disappearing
                // .show() --> needed to actually trigger it appearing to the user
                Toast.makeText(getApplicationContext(), "Item added!", Toast.LENGTH_SHORT).show();
                saveItems();
            }
        });
    }

    /**
     * Get the File that stores our list of to-do items
     * @return
     */
    private File getDataFile() {
        // getFilesDir() --> gets directory of this file
        // "data.txt" --> filename
        return new File(getFilesDir(), "data.txt");
    }

    // load items by reading every line of the data file
    private void loadItems() {
        try {
            items = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            Log.e("MainActivity", "Error reading items", e);
            items = new ArrayList<>(); // incase of error, should start from empty list
        }
    }

    // saves items by writing them into the data file
    // should be called whenever we make a change (add or remove items)
    private void saveItems() {
        try {
            FileUtils.writeLines(getDataFile(), items);
        } catch (IOException e) {
            Log.e("MainActivity", "Error writing items", e);
        }
    }
}