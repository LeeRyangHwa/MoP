package cosmoslab.example.com.ClubMultiTracking.Activity;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import cosmoslab.example.com.ClubMultiTracking.Etc.AddNewClub;
import cosmoslab.example.com.ClubMultiTracking.R;

public class MMAdapter extends BaseAdapter {
    private ArrayList<AddNewClub> arrlist = new ArrayList<>();
    private ArrayList<AddNewClub> datalist;
    private Context context;

    final static int NAME_ASC = 0;
    final static int TYPE_ASC = 1;

    Boolean CHECK_STATUS = false;

    private Boolean INIT = false;

    Comparator<AddNewClub> nameAsc = new Comparator<AddNewClub>() {
        @Override
        public int compare(AddNewClub restaurant, AddNewClub t1) {
            return restaurant.getName().compareTo(t1.getName());
        }
    };
    Comparator<AddNewClub> dateAsc = new Comparator<AddNewClub>() {
        @Override
        public int compare(AddNewClub restaurant, AddNewClub t1) {
            return (restaurant.getDate() + "").compareTo(t1.getDate());
        }
    };

    public MMAdapter(ArrayList<AddNewClub> datalist, Context context) {
        this.datalist = datalist;
        this.context = context;
    }


    @Override
    public int getCount() {
        return datalist.size();
    }

    @Override
    public Object getItem(int i) {
        return datalist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.activity_list_item, null);
        }
        TextView txt1 = (TextView) view.findViewById(R.id.textName);
        TextView txt2 = (TextView) view.findViewById(R.id.textDate);

        final int position = i;

        txt1.setText(datalist.get(i).getName());
        txt2.setText(datalist.get(i).getDate());
        return view;
    }

    public void setSort(int sortType) {
        if (sortType == NAME_ASC) {
            Collections.sort(datalist, nameAsc);
        } else {
            Collections.sort(datalist, dateAsc);
        }
        this.notifyDataSetChanged();
    }

    public void searchList(String s) {
        if (!INIT) {
            arrlist.addAll(datalist);
            INIT = true;
        }

        datalist.clear();
        if (s.length() == 0) {
            datalist.addAll(arrlist);
        } else {
            for (int i = 0; i < arrlist.size(); i++) {
                if (arrlist.get(i).getName().contains(s)) {
                    datalist.add(arrlist.get(i));
                }
            }
        }
        this.notifyDataSetChanged();
    }




    /*public void fillter(String searchText) {
        displayListItem.clear();
        if(searchText.length() == 0)
        {
            displayListItem.addAll(listItem);
        }
        else
        {
            for( BookListItem item : listItem)
            {
                if(item.getBookName().contains(searchText))
                {
                    displayListItem.add(item);
                }
            }
        }

        //리스트뷰 갱신을 알려주는 메소드
        notifyDataSetChanged();
    }
*/

}
