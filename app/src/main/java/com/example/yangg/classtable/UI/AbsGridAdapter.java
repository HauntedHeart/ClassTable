package com.example.yangg.classtable.UI;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yangg.classtable.EditCourse;
import com.example.yangg.classtable.Login;
import com.example.yangg.classtable.MainActivity;
import com.example.yangg.classtable.R;
import com.example.yangg.classtable.Register;

import static android.content.Context.VIBRATOR_SERVICE;
/*GridView的适配器*/
public class AbsGridAdapter extends BaseAdapter {
    private Context mContext;
    private String[][] contents;
    private String  cur_name;
    private int rowTotal;
    private int columnTotal;
    private int positionTotal;
    Vibrator vibrator;
    public AbsGridAdapter(Context context) {
        this.mContext = context;
    }

    public int getCount() {
        return positionTotal;
    }

    /*设置每个块的ID*/
    public long getItemId(int position) {
        return position;
    }

    /*获得内容*/
    public Object getItem(int position) {
        //求余得到二维索引
        int column = position % columnTotal;
        //求商得到二维索引
        int row = position / columnTotal;
        return contents[column][row];
    }

    /*设置每个块的内容及每个块的点击事件*/
    public View getView(final int position, View convertView, ViewGroup parent) {
        if( convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.grib_item, null);
        }
        TextView textView = (TextView)convertView.findViewById(R.id.text);
        //如果有课,那么添加数据
        if( !getItem(position).equals("")) {
            textView.setText((String)getItem(position));
            textView.setTextColor(Color.WHITE);
            //变换颜色
            int randomnum = (int) (Math.random() * 7);
            switch( randomnum ) {
                case 0:
                    textView.setBackgroundResource(R.drawable.grid_item_bg);
                    break;
                case 1:
                    textView.setBackgroundResource(R.drawable.bg_12);
                    break;
                case 2:
                    textView.setBackgroundResource(R.drawable.bg_13);
                    break;
                case 3:
                    textView.setBackgroundResource(R.drawable.bg_14);
                    break;
                case 4:
                    textView.setBackgroundResource(R.drawable.bg_15);
                    break;
                case 5:
                    textView.setBackgroundResource(R.drawable.bg_16);
                    break;
                case 6:
                    textView.setBackgroundResource(R.drawable.bg_17);
                    break;
                case 7:
                    textView.setBackgroundResource(R.drawable.bg_18);
                    break;
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int row = position / columnTotal;
                    int column = position % columnTotal;
                    String con =  contents[column][row] ;
                    Toast.makeText(mContext, con, Toast.LENGTH_SHORT).show();
                }
            });

            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int row = position / columnTotal;
                    int column = position % columnTotal;

                    vibrator = (Vibrator)mContext.getSystemService(VIBRATOR_SERVICE);
                    vibrator.vibrate(new long[]{0,100}, -1);
                    Intent intent = new Intent();
                    intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("class_column", column+1+"");
                    intent.putExtra("class_row", row+1+"");
                    intent.putExtra("user_name",cur_name);

                    intent.setClass(mContext, EditCourse.class);
                    mContext.startActivity(intent);
                 return false;
                }
            });
        }
        return convertView;
    }

    /*设置内容、行数、列数 、当前用户名*/
    public void setContent(String[][] contents, int row, int column,String cur_name) {
        this.contents = contents;
        this.rowTotal = row;
        this.columnTotal = column;
        this.cur_name = cur_name;
        positionTotal = rowTotal * columnTotal;
    }

}
