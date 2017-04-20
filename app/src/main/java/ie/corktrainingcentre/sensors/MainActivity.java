package ie.corktrainingcentre.sensors;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // need to setup Util for this activity
        Util.setActivity(this);         // needed for resource handling
        SensorView.setActivity(this);   // needed for resource handling
        SensorData.setActivity(this);   // needed for resource handling

        // load the data
        SensorData.loadData();  // loads all the sensors// //

        // render a single object in the main view
        // SensorView.renderView(SensorData.sensors[0]);

        // render all the data using the inflater
        SensorView.renderViews(SensorData.sensors);

    }


}

/*
    The model
 */
class SensorData {

    static String[] sensorNames;
    static String[] fileNames;  // images and details are in these files
    static SensorData[] sensors;
    static int sensorCount;
    /*

        thiese are for each sensor

     */
    String name;
    String fileName;
    String details;
    String imageName;
    int imageResourceId;


    static Activity appAct;

    static public void setActivity(Activity act) {
        appAct=act;
    }

    static void loadData() {

        Resources res = appAct.getResources();
        // read the strings from the res arrays
        sensorNames = res.getStringArray(R.array.sensorNames);
        fileNames = res.getStringArray(R.array.fileNames);
        // make space for the Sensor objects
        sensorCount=sensorNames.length;
        sensors=new SensorData[sensorCount];

        for (int i = 0; i < sensorCount ; i++) {
            sensors[i]=new SensorData(i);
        }

    }

    // the index gives access to the file names etc
    public SensorData(int index) {

        name = sensorNames[index];
        fileName=fileNames[index];
        details = Util.readResourceFile(fileName);
        imageName=fileName; // no extension required for resource +".png";
        imageResourceId = Util.getResourceId(imageName,"drawable"); // repeated here - use only one or other

    }
    /*
        should define a set of getters
     */

}


/*
    bind to the various controls representing the values
 */
class SensorView {

    static Activity appAct;

    TextView sensorName;
    TextView sensorDetails;
    ImageView sensorImage;
    int layoutResourceId;   // this is for inflater
    int parentViewId;
    ViewGroup parentView;   // ViewGroup is the parent of Layouts, and has the AadView() method
    View childView;

    static public void setActivity(Activity act) {
        appAct=act;
    }
    /*
        this uses the inflater to render all the data
     */
    static void renderViews(SensorData[] sensorData) {

        for (int i = 0; i < sensorData.length ; i++) {

            // now load the SensorView
            SensorView sensorView = new SensorView(appAct,R.id.activity_main);
            //sensorView.setLayoutResourceId(R.layout.layout_sensor_linear);
            //v.bindView(this);
            //View v = sensorView.inflate();
            sensorView.bindControls();
            sensorView.bindData(SensorData.sensors[0]);
            //sensorView.addChildViewToParent();

        }

    }
    /*
        this renders one view, in the main_layout (assumes the template view has been "included")
     */
    static void renderView(SensorData sensorData) {

        SensorView sensorView = new SensorView(appAct,R.id.activity_main);
        //sensorView.setLayoutResourceId(R.layout.layout_sensor_linear);
        //v.bindView(this);
        //View v = sensorView.inflate();
        sensorView.bindControls();
        sensorView.bindData(SensorData.sensors[0]);

    }

    SensorView(Activity act) {
        appAct = act;
    }

    SensorView(Activity act, int parentViewId) {
        appAct = act;
        this.parentViewId=parentViewId;
        parentView=(ViewGroup) appAct.findViewById(this.parentViewId);
        // by default, assume the template is already loaded, therefore seach in the mainlayout for
        // the controls to bind to the SensorView
        childView = parentView;
    }
    /*
        this is the template.xml resource id = ie the R.layout.file_name
     */
    public void setLayoutResourceId(int layoutResourceId) {
        this.layoutResourceId = layoutResourceId;
    }

    /*
        takes a view, and "binds" / stores the controls in the view that match for the data
        this can be the main view or the template
        this version needs to View to be already loaded ie avail by id
     */
    public void bindControls() {
        bindControls(childView);
    }

    public void bindControls(View v) {
        // we need IDs for each named Control

        // appAct.findViewById(R.id.tvName);   // easy way
        int id = Util.getResourceId("tvName","id");
        sensorName = (TextView) v.findViewById(id);

        sensorDetails = (TextView) v.findViewById(R.id.tvDetails);
        sensorImage = (ImageView) v.findViewById(R.id.ivImage);

    }

    public void addChildViewToParent() {
        parentView.addView(childView);
    }


    public View inflate() {

        LayoutInflater inflater = (LayoutInflater) appAct.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        childView = inflater.inflate(layoutResourceId, null);

        return childView;
    }

    public void setContentLayout(int layoutId) {
        View v = appAct.findViewById(R.id.layout_sensor_linear);
        // bindView(v);
    }
    /*
        these are the setter methods - no getters required
     */
    public void setSensorName(String name) {
        sensorName.setText(name);
    }
    public void setSensorDetails(String details) {
        sensorDetails.setText(details);
    }
    /*
        set image by name or id
     */
    public void setSensorImage(String imageName) {
        sensorImage.setImageResource( Util.getResourceId(imageName,"drawable"));
    }
    public void setSensorImage(int imageResourceId) {
        sensorImage.setImageResource(imageResourceId);
    }

    void bindData(SensorData sensor) {

        setSensorName(sensor.name);     // should really use sensor getters
        setSensorDetails(sensor.details);
        setSensorImage(sensor.imageName);

    }


}

/*

    A utility Class - needs to be bound to the App Activity to access resources

 */
class Util {
    static Activity appAct;

    static public void setActivity(Activity act) {
        appAct=act;
    }
    static public String readResourceFile(String fileName) {        // read the dog breed details
        String text="";
        String resourceType = "raw";
        String packageName = appAct.getApplicationContext().getPackageName();
        Resources res = appAct.getResources();
        int identifier = res.getIdentifier( fileName, resourceType, packageName );
        InputStream file = null;
        try {
            file = res.openRawResource(identifier);
            byte[] buffer = new byte[file.available()];
            file.read(buffer, 0, buffer.length);
            // put the dog breed details into the TextView
            text = new String(buffer, "UTF-8");
            file.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return text;
    }

    /*
        resourceGroup is name space for the identifier, such as string, id, coolor...
     */
    static public int getResourceId(String resourceName, String resourceGroup ) {

        int resId = appAct.getResources().getIdentifier(resourceName, resourceGroup, appAct.getPackageName());
        return resId;
    }
}





