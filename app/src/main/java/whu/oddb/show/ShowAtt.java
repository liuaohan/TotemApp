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
import whu.oddb.memory.table.AttributeTable;

public class ShowAtt extends AppCompatActivity implements Serializable {
    private final int W = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final int M = ViewGroup.LayoutParams.MATCH_PARENT;
    private TableLayout show_tab;
    //private ArrayList<String> objects = new ArrayList<String> ();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("ShowCla", "oncreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.print_result);

        Intent intent = getIntent();
        Bundle bundle0 = intent.getExtras();
        showAttTab((AttributeTable) bundle0.getSerializable("AttributeTable"));

    }

    private void showAttTab(AttributeTable AttrT) {
        int tabCol = 5;
        int tabH = AttrT.getAttributeTable().size();
        String stemp1, stemp2, stemp3, stemp4, stemp5;
        Object oj1, oj2, oj3, oj4, oj5;

        show_tab = findViewById(R.id.rst_tab);

        for (int i = 0; i <= tabH; i++) {
            TableRow tableRow = new TableRow(this);
            if (i == 0) {
                stemp1 = "classId";
                stemp2 = "attrId";
                stemp3 = "attrName";
                stemp4 = "attrType";
                stemp5 = "isDeputy";

            } else {
                oj1 = AttrT.getAttributeTable().get(i - 1).getClassId();
                oj2 = AttrT.getAttributeTable().get(i - 1).getAttrId();
                oj3 = AttrT.getAttributeTable().get(i - 1).getAttrName();
                oj4 = AttrT.getAttributeTable().get(i - 1).getClassId();
                oj5 = AttrT.getAttributeTable().get(i - 1).isDeputy();
                stemp1 = oj1.toString();
                stemp2 = oj2.toString();
                stemp3 = oj3.toString();
                stemp4 = oj4.toString();
                stemp5 = oj5.toString();
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
                    case 3:
                        tv.setText(stemp4);
                        break;
                    case 4:
                        tv.setText(stemp5);
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

