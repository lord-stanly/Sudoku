package ru.mail.fizikello.sudoku;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;

public class PuzzleView extends View {
    private static final String TAG = "Sudoku";
    private final Game game;
    public PuzzleView(Context context){
        super(context);
        this.game = (Game) context;
        setFocusable(true);
        setFocusableInTouchMode(true);
    }
    private float width; // ширина одного тайла - элемент игрового изображения
    private float height; // высота одного тайла
    private int selX; //координата х выделенной области
    private int selY; //координата y выделенной области
    private final Rect selRect = new Rect(); // прямоугольник
    @Override
    protected void onSizeChanged(int w, int h, int oidw, int oldh){
        width = w / 9f;
        height = h / 9f;
        getRect(selX, selY, selRect);
        Log.d(TAG, "onSizeChanged: width " + width + ", height" + height);
        super.onSizeChanged(w, h, oldh, oldh);
    }
    private void getRect(int x, int y, Rect rect){
        rect.set((int) (x * width),
                 (int) (y * height),
                 (int)(x * width + width),
                 (int) (y * height + height)
        );
    }
    @Override
    protected void onDraw(Canvas canvas){
        //риссование фона
        Paint background = new Paint();
        background.setColor(getResources().getColor(R.color.puzzle_background));
        canvas.drawRect(0,0,getWidth(),getHeight(),background);
        //Рисование игровой доски
        //Определение цветов для линий решетки
        Paint dark = new Paint();
        dark.setColor(getResources().getColor(R.color.puzzle_dark));
        Paint hilite = new Paint();
        dark.setColor(getResources().getColor(R.color.puzzle_hilite));
        Paint light = new Paint();
        dark.setColor(getResources().getColor(R.color.puzzle_light));
        //рисование вспомогательных линий решетки
        for(int i = 0; i < 9; i++){
            canvas.drawLine(0, i * height, getWidth(), i * height, light);
            canvas.drawLine(0, i * height + 1, getWidth(), i * height, hilite);
            canvas.drawLine(i * width, 0, i * width, getHeight(), light);
            canvas.drawLine(i * width + 1, 0, i * width, getHeight(), hilite);
        }
        //рисование основных линий решетки
        for(int i =0; i <9; i++){
            if(i % 3 != 0)
                continue;
            canvas.drawLine(0, i * height, getWidth(), i * height, dark);
            canvas.drawLine(0, i * height + 1, getWidth(), i * height + 1, hilite);
            canvas.drawLine(i * width, 0, i * width, getHeight(), dark);
            canvas.drawLine(i * width +1, 0, i * width + 1, getHeight(), hilite);
        }
        //Рисование чисел
        //Определение цвета и стиля для чисел
        Paint foreground = new Paint(Paint.ANTI_ALIAS_FLAG);
        foreground.setColor(getResources().getColor(R.color.puzzle_foreground));
        foreground.setStyle(Style.FILL);
        foreground.setTextSize(height * 0.75f);
        foreground.setTextScaleX(width / height);
        foreground.setTextAlign(Paint.Align.CENTER);
        //Рисование числа в центре тайла
        FontMetrics fm = foreground.getFontMetrics();
        //Центровка по оси X: использование выравнивания (и координаты центральной точки)
        float x = width / 2;
        //Центровка по оси Y: сначала измеряем повышение/понижение
        float y = height / 2 - (fm.ascent + fm.descent) / 2;
        for(int i = 0; i < 9; i ++){
            for(int j =0; j < 9; j++){
                canvas.drawText(this.game.getTileString(i, j), i * width + x, j * height + y, foreground);
            }
        }
        //Рисование подсказок
        // Выбрать цвет подсказки, основываясь на количестве оставшихся ходов
        Paint hint = new Paint();
        int c[] = { getResources().getColor(R.color.puzzle_hint_0),
                    getResources().getColor(R.color.puzzle_hint_1),
                    getResources().getColor(R.color.puzzle_hint_2) };
        Rect r = new Rect();
        for(int i = 0; i < 9; i++){
            for(int j = 0; j < 9; j++){
                int movesleft = 9 - game.getUsedTitles(i, j).length;
                if(movesleft < c.length){
                    getRect(i, j, r);
                    hint.setColor(c[movesleft]);
                    canvas.drawRect(r, hint);
                }
            }
        }
        //Рисование выделения
        Log.d(TAG, "selRect =" + selRect);
        Paint selected = new Paint();
        selected.setColor(getResources().getColor(R.color.puzzle_selected));
        canvas.drawRect(selRect, selected);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        Log.d(TAG,"onKeyDown: keycode=" + keyCode + ", event=" + event);
        switch(keyCode){
            case KeyEvent.KEYCODE_0:
            case KeyEvent.KEYCODE_SPACE: setSelectedTitle(0); break;
            case KeyEvent.KEYCODE_1: setSelectedTitle(1); break;
            case KeyEvent.KEYCODE_2: setSelectedTitle(2); break;
            case KeyEvent.KEYCODE_3: setSelectedTitle(3); break;
            case KeyEvent.KEYCODE_4: setSelectedTitle(4); break;
            case KeyEvent.KEYCODE_5: setSelectedTitle(5); break;
            case KeyEvent.KEYCODE_6: setSelectedTitle(6); break;
            case KeyEvent.KEYCODE_7: setSelectedTitle(7); break;
            case KeyEvent.KEYCODE_8: setSelectedTitle(8); break;
            case KeyEvent.KEYCODE_9: setSelectedTitle(9); break;
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_DPAD_CENTER:
                game.showKeypadOrError(selX, selY);
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                select(selX, selY -1);
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                select(selX, selY +1);
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                select(selX -1, selY);
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                select(selX + 1, selY);
                break;
            default:
                return super.onKeyDown(keyCode, event);

        }
        return true;

    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        if(event.getAction() != MotionEvent.ACTION_DOWN)
            return super.onTouchEvent(event);
        select((int)(event.getX() / width), (int)(event.getY() / height));
        game.showKeypadOrError(selX, selY);
        Log.d(TAG, "onTouchEvent: x " + selX + ", y " + selY);
        return true;
    }

    private void select(int x, int y){
        invalidate(selRect);
        selX = Math.min(Math.max(x, 0), 8);
        selY = Math.min(Math.max(y, 0), 8);
        getRect(selX, selY, selRect);
        invalidate(selRect);
    }
//вызов метода setSelectedTile() для изменения
//числа в ячейке:
    public void setSelectedTitle(int tile){
        if(game.setTileIfValid(selX, selY, tile)){
            invalidate();// можно изминить подсказки
        }else{
            //Число не подходит для этого тайла
            Log.d(TAG, "setSelectedTile: invalid: " + tile);
            startAnimation(AnimationUtils.loadAnimation(game, R.anim.shake));
        }
    }

}
