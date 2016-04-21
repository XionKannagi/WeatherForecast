package com.zack.weatherforecast;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class RecommendActivity extends AppCompatActivity {

    TextView weatherText;
    TextView minTempText;
    TextView recommendText;

    ImageView recommendImage;

    int NET;
    int minTmp;
    int maxTmp;
    String weather;
    String[] recommendStr = {
            "真夏日です。\n" +
                    "半袖がいいでしょう。\n" +
                    "サンダルやミュールが大活躍します。\n" +
                    "紫外線対策に帽子を被るのもよいです。\n" +
                    "こまめな水分補給が必要となりますので、飲み物を持ち歩くようにしましょう。\n",
            "半袖1枚で大丈夫です。\n" +
                    "ただし、最高気温が26度以上でも最低気温がそれ以下の場合は、夜に肌寒くなる可能性がありますので、夜まで外出する場合は、薄いカーディガンを１枚持っていくといいでしょう。",
            "晴れていて暖かい日差しが差し込んでいる場合は、半袖でもいいでしょう。\n" +
                    "しかし、曇りや雨で日差しがない場合は、長袖を着ていくことをお勧めします。\n" +
                    "半袖を着る場合も、念のため薄手の上着を着ておくと、少し寒くなっても安心です",
            "重ね着に最適な気温です。\n" +
                    "長袖のTシャツの上に、カーディガンを羽織るなど、少し暖かい恰好をするといいでしょう。\n" +
                    "ジャケットやストールもオススメです。",
            "冬服や春コートなどが活躍します。\n" +
                    "また、セーターなどのニット素材の服を着るのにも、最適な気温です。",
            "冬服の上にコートを着用するといいでしょう。\n" +
                    "また、足元が冷えてきますので、ブーツもお勧めします。",
            "冬服・コート・ブーツの上に、\n"+
                    "さらにマフラーや手袋を着用すると良いでしょう。\n" +
                    "インナーにヒートテックを着るのもオススメです。\n" +
                    "カイロを持っていくのもいいですね。",
            "真冬日です。\n" + "冬服の上にコートを着用するといいでしょう。 \n" +
                    "帽子とマフラー、手袋は必須アイテムです。\n" +
                    "帽子はできれば耳までかぶるニット帽の方がいいでしょう。"
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);

        weatherText = (TextView) findViewById(R.id.TextViewWeather);
        minTempText = (TextView) findViewById(R.id.TextViewMinTemp);
        recommendText = (TextView) findViewById(R.id.TextViewRecommend);

        recommendImage = (ImageView) findViewById(R.id.imageViewRecommend);

        SharedPreferences data = getSharedPreferences("saveData", MODE_PRIVATE);
        weather = data.getString("weather", "");
        NET = data.getInt("minNET", -273);
        minTmp = data.getInt("minTmp", -273);
        maxTmp = data.getInt("maxTmp", -273);

        minTempText.setText("コーディネート");
        weatherText.setText("天気：" + weather);

        if (maxTmp >= 30) {
            recommendText.setText(recommendStr[0]);
        } else if (maxTmp >= 26) {
            recommendText.setText(recommendStr[1]);
        } else if (maxTmp >= 21) {
            recommendText.setText(recommendStr[2]);
        } else if (maxTmp >= 16) {
            recommendText.setText(recommendStr[3]);
        } else if (maxTmp >= 12) {
            recommendText.setText(recommendStr[4]);
        } else if (maxTmp >= 7) {
            recommendText.setText(recommendStr[5]);
        } else if (maxTmp >= 0) {
            recommendText.setText(recommendStr[6]);
        } else if (maxTmp < 0 && maxTmp != -273) {
            recommendText.setText(recommendStr[7]);
        } else {
            recommendText.setText("※気温の値が不正です．");
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }




}
