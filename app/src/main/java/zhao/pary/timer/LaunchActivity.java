package zhao.pary.timer;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 启动页
 */
public class LaunchActivity extends BaseActivity implements View.OnClickListener {

    private static final int DELAY_SECONDS = 5000;

    private Button btnClickGo;
    private TextView tvOneWord;
    private static final String KEY_ONE_WORD = "one_word";
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            doMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_launch);

        init();
    }

    /**
     * init
     */
    private void init() {

        btnClickGo = (Button) findViewById(R.id.btn_click_go);

        tvOneWord = (TextView) findViewById(R.id.tv_one_word);

        TextView tvJump = (TextView) findViewById(R.id.tv_jump);
        assert tvJump != null;
        tvJump.setOnClickListener(this);
//        setWallpaper((ImageView) findViewById(R.id.iv_wallpaper));

        getOneWord();

        delayedToMainActivity();
    }

    /**
     * 点击进入主页按钮
     */
    public void goMainActivity(View view) {

        startActivity(new Intent(LaunchActivity.this, MainActivity.class));
        this.finish();
    }

    /**
     * 下载图片并设置到ImageView
     */
    private void setWallpaper(final ImageView imageView) {

        String path = "http://211.155.84.158/?build=4.2.0&appVersion=503&ch=qq&openudid=357139053131293&screen=720x1280&device=android&limit=11&method=uranus.items.get&geo=22.546162%2C114.070908&offset=0";
        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        final StringRequest request = new StringRequest(com.android.volley.Request.Method.GET, path, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String url = getImagePath(response);
                Picasso.with(LaunchActivity.this)
                        .load(url)
                        .placeholder(R.mipmap.bg_launch_logo)
                        .error(R.mipmap.bg_launch_logo)
                        .into(imageView);

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                requestQueue.add(request);
            }
        }, DELAY_SECONDS / 4);

    }

    /**
     * 解析json数据,返回图片下载地址
     */
    public static String getImagePath(String json) {
        String url_l = null;
        try {
            JSONObject object = new JSONObject(json);
            JSONArray array = object.getJSONArray("item");
//            int random = (int) (Math.random() * 10);
            int random = 0;
            JSONObject obj_item = array.getJSONObject(random);
            JSONObject obj_icon = obj_item.getJSONObject("icon");
            url_l = obj_icon.optString("url_l");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return url_l;
    }

    //从one接口解析出一句话的方法
    private void getOneWord() {
        //接口格式：http://rest.wufazhuce.com/OneForWeb/one/getHp_N?strDate=2015-11-10&strRow=1
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        String date = sdf.format(new Date(System.currentTimeMillis()));
        String json = "http://rest.wufazhuce.com/OneForWeb/one/getHp_N?strDate=" + date + "&strRow=1";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(StringRequest.Method.GET, json, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    JSONObject obj_h = object.getJSONObject("hpEntity");
                    String content = obj_h.getString("strContent");
                    Bundle bundle = new Bundle();
                    bundle.putString(KEY_ONE_WORD, content);
                    Message msg = handler.obtainMessage();
                    msg.what = TimerSetting.HANDLER_ONE_WORD;
                    msg.setData(bundle);
                    handler.sendMessage(msg);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue.add(request);

    }

    /**
     * 接收到Handler信息进行处理
     */
    private void doMessage(Message message) {

        Bundle bundle = message.getData();

        switch (message.what) {

            case TimerSetting.HANDLER_ONE_WORD:

                tvOneWord.setText(bundle.getString(KEY_ONE_WORD));
                animateBtnClickGo();
                break;

            default:
                break;
        }
    }

    /**
     * Animate Btn ClickGo
     */
    private void animateBtnClickGo() {

        btnClickGo.setVisibility(View.VISIBLE);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(btnClickGo, "scaleX", 0f, 1f),
                ObjectAnimator.ofFloat(btnClickGo, "scaleY", 0f, 1f),
                ObjectAnimator.ofFloat(btnClickGo, "alpha", 0f, 1f));

        //设置动画时间
        set.setDuration(500);
        set.setInterpolator(new OvershootInterpolator());
        set.start();
    }

    /**
     * 延时跳转到首页(如果OneWord没有加载出来的话)
     */
    public void delayedToMainActivity() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                if (btnClickGo.getVisibility() == View.VISIBLE)
                    return;

                startActivity(new Intent(LaunchActivity.this, MainActivity.class));
                LaunchActivity.this.finish();
            }
        };

        handler.postDelayed(runnable, DELAY_SECONDS);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.tv_jump:

                startActivity(new Intent(this, MainActivity.class));
                break;

            default:
                break;
        }
    }
}
