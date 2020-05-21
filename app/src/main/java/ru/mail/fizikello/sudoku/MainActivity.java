package ru.mail.fizikello.sudoku;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Обработчик нажатий для всех кнопок
        View continueButton = findViewById(R.id.continue_button);
        continueButton.setOnClickListener(this);

        View newButton = findViewById(R.id.new_button);
        newButton.setOnClickListener(this);

        View aboutButton = findViewById(R.id.about_button);
        aboutButton.setOnClickListener(this);

        View exitButton = findViewById(R.id.exit_button);
        exitButton.setOnClickListener(this);
    }

//оброботчик нажатий кнопки
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.about_button:
                Intent i = new Intent(this, About.class);
                startActivity(i);
                break;
            case R.id.new_button:
                openNewGameDialog();
                break;
            case R.id.exit_button:
                finish();
                break;
        }
    }
//рисуем меню
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
//оброботчик нажатий на вкладку меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.settings:
                startActivity(new Intent(this, Prefs.class));
                return true;
        }
        return false;
    }
//Метод openNewGameDialog() занимается созданием пользовательского интерфейса для списка уровней сложности:
    private static final String TAG = "Sudoku";
    private void openNewGameDialog(){
        new AlertDialog.Builder(this)
                .setTitle(R.string.new_game_title)
                    .setItems(R.array.difficulty,
            new DialogInterface.OnClickListener(){
                 public void onClick(DialogInterface dialogInterface, int i)
                 {
                    startGame(i);
                 }
            }).show();
    }
    private void startGame(int i){
        Log.d(TAG, "clicked on " + i);
        //Запуск игры
        Intent intent = new Intent(this, Game.class);
        intent.putExtra(Game.KEY_DIFFICULTY, i);
        startActivity(intent);
    }
}
