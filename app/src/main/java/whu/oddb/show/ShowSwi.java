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
import whu.oddb.memory.table.SwitchingRuleTable;
import whu.oddb.memory.table.SwitchingTable;

public class ShowSwi extends AppCompatActivity implements Serializable {
    private final int W = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final int M = ViewGroup.LayoutParams.MATCH_PARENT;
    private TableLayout show_tab;
    //private ArrayList<String> objects = new ArrayList<String> ();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("ShowSwi", "oncreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.print_result);

        Intent intent = getIntent();
        Bundle bundle0 = intent.getExtras();
        showSwiTab((SwitchingRuleTable) bundle0.getSerializable("SwitchingRuleTable"));

    }

    private void showSwiTab(SwitchingRuleTable switchingT) {
        int tabCol = 2;
        int tabH = switchingT == null || switchingT.getSwitchingRuleTable() == null ? 0 : switchingT.getSwitchingRuleTable().size();
        //String stemp1, stemp2, stemp3, stemp4;
        String s1, s2;

        show_tab = findViewById(R.id.rst_tab);

        for (int i = 0; i <= tabH; i++) {
            TableRow tableRow = new TableRow(this);
            if (i == 0) {
                /*stemp1 = "originClassId";
                stemp2 = "deputyClassId";
                stemp3 = "originAttrId";
                stemp4 = "deputyAttrId";*/
                s1 = "switchRuleId";
                s2 = "rule";
            } else {
                /*stemp1 = String.valueOf(switchingT.getSwitchingTable().get(i - 1).getOriginClassId());
                stemp2 = String.valueOf(switchingT.getSwitchingTable().get(i - 1).getDeputyClassId());
                stemp3 = String.valueOf(switchingT.getSwitchingTable().get(i - 1).getOriginAttrId());
                stemp4 = String.valueOf(switchingT.getSwitchingTable().get(i - 1).getDeputyAttrId());*/
                s1 = String.valueOf(switchingT.getSwitchingRuleTable().get(i - 1).getSwitchRuleId());
                s2 = String.valueOf(switchingT.getSwitchingRuleTable().get(i - 1).getRule());
            }
            for (int j = 0; j < tabCol; j++) {
                TextView tv = new TextView(this);
                switch (j) {
                    case 0:
                        tv.setText(s1);
                        break;
                    case 1:
                        tv.setText(s2);
                        break;
                    /*case 2:
                        tv.setText(stemp3);
                        break;
                    case 3:
                        tv.setText(stemp4);
                        break;
*/
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
