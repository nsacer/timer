package zhao.pary.timer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.tuesda.walker.circlerefresh.CircleRefreshLayout;

public class TestActivity extends BaseActivity {

    private CircleRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        String[] strs = {
                "The",
                "Canvas",
                "class",
                "holds",
                "the",
                "draw",
                "calls",
                ".",
                "To",
                "draw",
                "something,",
                "you",
                "need",
                "4 basic",
                "components",
                "Bitmap",
        };

        ListView listView = (ListView) findViewById(R.id.list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, strs);
        assert listView != null;
        listView.setAdapter(adapter);

        refreshLayout = (CircleRefreshLayout) findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(new CircleRefreshLayout.OnCircleRefreshListener() {
            @Override
            public void completeRefresh() {

            }

            @Override
            public void refreshing() {

            }
        });
    }
}
