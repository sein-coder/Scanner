package com.example.scanner;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CustomListViewAdapter extends BaseAdapter {

    private ArrayList<ListViewItem> listItems = new ArrayList<>();

    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public Object getItem(int position) {
        return listItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        CustomViewHolder holder;
        if(convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_items, null, false);

            holder = new CustomViewHolder();
            holder.prod_name = (TextView)convertView.findViewById(R.id.prod_name);
            holder.price = (TextView)convertView.findViewById(R.id.price);
            holder.img_btn = (ImageButton)convertView.findViewById(R.id.close_img);
            convertView.setTag(holder);
        }
        else {
            holder = (CustomViewHolder) convertView.getTag();
        }

        final ListViewItem item = listItems.get(position);

        holder.price.setText(new DecimalFormat("###,###").format(item.getPrice())+" 원");
        holder.prod_name.setText(item.getProd_name());


        holder.img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogEvent(v, item.getProd_name(), item.getPrice(), position, parent);
            }
        });
        return convertView;

    }

    class CustomViewHolder {
        TextView prod_name;
        TextView price;
        ImageButton img_btn;
    }

    public void addItem(ListViewItem item) {
        listItems.add(item);
    }

    /* 결제버튼 클릭시 다이얼로그 이벤트 */
    public void dialogEvent(final View view, final String prod_name, final int price, final int index, final ViewGroup parent) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("상품 취소").setMessage("상품 "+prod_name+"("+new DecimalFormat("###,###").format(price)+")을"+"취소 하시겠습니까?");
        builder.setPositiveButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                /* 아무런 처리 안함 */
            }
        });
        builder.setNegativeButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                ((MainActivity)MainActivity.context_main).tot_price -= price;
                /* adapter 리스트 요소 삭제*/
                listItems.remove(index);
                /* static 리스트 요소 삭제 */
                ((MainActivity)MainActivity.context_main).items.remove(index);

                ((CustomScannerActivity)CustomScannerActivity.context_scanner).refresh();
                Toast.makeText(parent.getContext(), "상품 : " +prod_name+"가 취소되었습니다.", Toast.LENGTH_LONG).show();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void clear() {
        listItems.clear();
    }
}
