package com.example.loginfirebaseee;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

public class listview_inputdata extends ArrayAdapter {
    private Activity context;
    List<InputData> list_inputdata;
    public listview_inputdata(Activity context, List<InputData> inputDataArray){
        super(context, R.layout.layout_listview, inputDataArray);
        this.context = context;
        this.list_inputdata = inputDataArray;
    }

    public View getView(int position, View concertView, ViewGroup parent){
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_listview, null, true);
        TextView textViewData = listViewItem.findViewById(R.id.textViewMeter);
        TextView textViewData2 = listViewItem.findViewById(R.id.textViewWaktu);
        TextView textViewData3 = listViewItem.findViewById(R.id.textViewEmail);
        TextView textViewLatitude = listViewItem.findViewById(R.id.textViewLatitude);
        TextView textViewLongitude = listViewItem.findViewById(R.id.textViewLongitude);
        InputData inputData = list_inputdata.get(position);
        textViewData.setText("Data (meter) :  " + inputData.getData_Data());
        textViewData2.setText("Timestamp : "+ inputData.getFormattedTimestamp());
        textViewData3.setText("User : "+ inputData.getUserId());
        textViewLatitude.setText("Latitude : "+String.valueOf(inputData.getLatitude()));
        textViewLongitude.setText("Latitude : "+ String.valueOf(inputData.getLongitude()));
        return listViewItem;
    }

}
