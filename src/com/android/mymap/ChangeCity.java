package com.android.mymap;

import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.GeoPoint;

import android.app.Activity;  
import android.content.Intent;
import android.os.Bundle;  
import android.view.Gravity;  
import android.view.View;  
import android.view.ViewGroup;  
import android.widget.AbsListView;  
import android.widget.BaseExpandableListAdapter;  
import android.widget.ExpandableListView;  
import android.widget.ExpandableListView.OnChildClickListener;  
import android.widget.ExpandableListView.OnGroupCollapseListener;  
import android.widget.ExpandableListView.OnGroupExpandListener;  
import android.widget.Toast;  
import android.widget.ExpandableListView.OnGroupClickListener;  
import android.widget.TextView;  
  

public class ChangeCity extends Activity {  
	
	private ArrayList<ArrayList<GeoPoint>> citygeo=new ArrayList<ArrayList<GeoPoint>>();
    private List<String> groupData;  
    private List<List<String>> childrenData;  
    private void loadData() {  
        groupData = new ArrayList<String>();  
        groupData.add("����ʡ");  
        groupData.add("�㽭ʡ");  
        groupData.add("�㶫ʡ");  
  
        childrenData = new ArrayList<List<String>>();  
        List<String> sub1 = new ArrayList<String>();  
        sub1.add("�Ͼ���");  
        sub1.add("������");  
        childrenData.add(sub1);  
        List<String> sub2 = new ArrayList<String>();  
        sub2.add("������");  
        sub2.add("������");    
        childrenData.add(sub2);  
        List<String> sub3 = new ArrayList<String>();  
        sub3.add("������");  
        sub3.add("������");  
        sub3.add("��ɽ��");   
        childrenData.add(sub3);  
    }  
  
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.changecity);  
        //����ʡ�ݺͳ�������  
        loadData();  
        //��ʼ����������
        setgeo();
          
        ExpandableListView expandableListView = (ExpandableListView)findViewById(R.id.expandableListView1);  
        expandableListView.setAdapter(new ExpandableAdapter());  
        expandableListView.setOnGroupClickListener(new OnGroupClickListener() {  
            public boolean onGroupClick(ExpandableListView parent, View clickedView, int groupPosition, long groupId) {  
                //showMessage("���Group: " + ((TextView)clickedView).getText());  
                return false;//����true��ʾ���¼��ڴ˱�������  
            }  
        });  
        expandableListView.setOnChildClickListener(new OnChildClickListener() {  
            public boolean onChildClick(ExpandableListView expandablelistview,  
                    View clickedView, int groupPosition, int childPosition, long childId) {  
                //showMessage("���Child: " + ((TextView)clickedView).getText());
            	Intent intent=new Intent();
            	intent.setClass(ChangeCity.this, MyMapActivity.class);
            	Bundle bundle=new Bundle();
            	bundle.putSerializable("citygeo", new SGeoPoint(citygeo.get(groupPosition).get(childPosition)));
            	intent.putExtras(bundle);
            	startActivity(intent);
                return false;//����true��ʾ���¼��ڴ˱�������  
            }  
        });  
        expandableListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {  
            public void onGroupCollapse(int groupPosition) {  
                //showMessage("��£Group: " + (groupPosition + 1));  
            }  
        });  
        expandableListView.setOnGroupExpandListener(new OnGroupExpandListener() {  
            public void onGroupExpand(int groupPosition) {  
                //showMessage("չ��Group: " + (groupPosition + 1));  
            }  
        });  
    }  
      
    private class ExpandableAdapter extends BaseExpandableListAdapter {  
  
        public Object getChild(int groupPosition, int childPosition) {  
            return childrenData.get(groupPosition).get(childPosition);  
        }  
  
        public long getChildId(int groupPosition, int childPosition) {  
            return 0;  
        }  
  
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {  
            TextView text = null;  
            if (convertView != null) {  
                text = (TextView)convertView;  
                text.setText(childrenData.get(groupPosition).get(childPosition));  
            } else {  
                text = createView(childrenData.get(groupPosition).get(childPosition));  
            }  
            return text;  
        }  
  
        public int getChildrenCount(int groupPosition) {  
            return childrenData.get(groupPosition).size();  
        }  
  
        public Object getGroup(int groupPosition) {  
            return groupData.get(groupPosition);  
        }  
  
        public int getGroupCount() {  
            return groupData.size();  
        }  
  
        public long getGroupId(int groupPosition) {  
            return 0;  
        }  
  
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {  
            TextView text = null;  
            if (convertView != null) {  
                text = (TextView)convertView;  
                text.setText(groupData.get(groupPosition));  
            } else {  
                text = createView(groupData.get(groupPosition));  
            }  
            return text;  
        }  
  
        public boolean hasStableIds() {  
            return false;  
        }  
  
        public boolean isChildSelectable(int groupPosition, int childPosition) {  
            return true;  
        }  
          
        private TextView createView(String content) {  
            AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(    
                    ViewGroup.LayoutParams.FILL_PARENT, 38);    
            TextView text = new TextView(ChangeCity.this);    
            text.setLayoutParams(layoutParams);    
            text.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);    
            text.setPadding(40, 0, 0, 0);    
            text.setText(content);  
            return text;  
        }  
    }  
    
    //��ʼ��ÿ����������
    public void setgeo(){
    	ArrayList<GeoPoint> jsprovince=new ArrayList<GeoPoint>();
    	jsprovince.add(new GeoPoint((int)(32.03*1e6), (int)(118.46*1e6)));
    	jsprovince.add(new GeoPoint((int)(31.47*1e6), (int)(119.58*1e6)));
    	ArrayList<GeoPoint> zjprovince=new ArrayList<GeoPoint>();
    	zjprovince.add(new GeoPoint((int)(30.16*1e6), (int)(120.10*1e6)));
    	zjprovince.add(new GeoPoint((int)(29.18*1e6), (int)(120.04*1e6)));
    	ArrayList<GeoPoint> gdprovince=new ArrayList<GeoPoint>();
    	gdprovince.add(new GeoPoint((int)(23.08*1e6), (int)(113.14*1e6)));
    	gdprovince.add(new GeoPoint((int)(22.33*1e6), (int)(114.07*1e6)));
    	gdprovince.add(new GeoPoint((int)(23.02*1e6), (int)(113.06*1e6)));
    	citygeo.add(jsprovince);
    	citygeo.add(zjprovince);
    	citygeo.add(gdprovince);
    }
      

    
}