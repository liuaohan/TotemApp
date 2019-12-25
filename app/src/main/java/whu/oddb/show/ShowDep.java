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
import whu.oddb.memory.table.DeputyTable;


public class ShowDep extends AppCompatActivity implements Serializable {
    private final int W = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final int M = ViewGroup.LayoutParams.MATCH_PARENT;
    private TableLayout show_tab;
    //private ArrayList<String> objects = new ArrayList<String> ();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("ShowDep", "oncreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.print_result);

        Intent intent = getIntent();
        Bundle bundle0 = intent.getExtras();
        showDepTab((DeputyTable) bundle0.getSerializable("DeputyTable"));

    }

    private void showDepTab(DeputyTable deputyt) {
        int tabCol = 3;
        int tabH = deputyt.getDeputyTable().size();
        Object oj1, oj2, oj3;
        String stemp1, stemp2, stemp3;

        show_tab = findViewById(R.id.rst_tab);

        for (int i = 0; i <= tabH; i++) {
            TableRow tableRow = new TableRow(this);
            if (i == 0) {
                stemp1 = "originid";
                stemp2 = "deputyid";
                stemp3 = "deputyrule";

            } else {
                oj1 = deputyt.getDeputyTable().get(i - 1).getOriginClassId()    ;
                oj2 = deputyt.getDeputyTable().get(i - 1).getDeputyClassId();
                oj3 = deputyt.getDeputyTable().get(i - 1).getDeputyRuleId();
                stemp1 = oj1.toString();
                stemp2 = oj2.toString();
                stemp3 = oj3.toString();
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
                    case 2:
                        tv.setText(stemp3);
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

