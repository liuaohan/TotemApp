package whu.oddb.show;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.Serializable;

import whu.oddb.R;
import whu.oddb.memory.table.DeputyRuleTable;

public class ShowDepRule extends AppCompatActivity implements Serializable {
    private final int W = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final int M = ViewGroup.LayoutParams.MATCH_PARENT;
    private TableLayout show_tab;
    //private ArrayList<String> objects = new ArrayList<String> ();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("ShowDepRule", "oncreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.print_result);

        Intent intent = getIntent();
        Bundle bundle0 = intent.getExtras();
        showDepRuleTab((DeputyRuleTable) bundle0.getSerializable("DeputyRuleTable"));

    }

    private void showDepRuleTab(DeputyRuleTable deputyrulet) {
        int tabCol = 2;
        int tabH = deputyrulet.getDeputyRuleTable().size();
        Object oj1,oj2;
        String stemp1, stemp2;

        show_tab = findViewById(R.id.rst_tab);

        for (int i = 0; i <= tabH; i++) {
            TableRow tableRow = new TableRow(this);
            if (i == 0) {
                stemp1 = "deputyruleid";
                stemp2 = "deputyrule";

            } else {
                oj1 = deputyrulet.getDeputyRuleTable().get(i-1).getDeputyRuleId();
                oj2 = deputyrulet.getDeputyRuleTable().get(i-1).getRule();
                stemp1 = oj1.toString();
                stemp2 = oj2.toString();
            }
            for (int j = 0; j < tabCol; j++) {
                TextView tv = new TextView(this);
                switch (j) {
                    case 0:
                        tv.setText(stemp1);
                        break;
                    case 1:
                        tv.setText(stemp2);
                        break;

                }
                tv.setGravity(Gravity.CENTER);
                tv.setBackgroundResource(R.drawable.tab_bg);
                tv.setTextSize(28);
                tableRow.addView(tv);
            }
            show_tab.addView(tableRow, new TableLayout.LayoutParams(M, W));

        }

    }

}

