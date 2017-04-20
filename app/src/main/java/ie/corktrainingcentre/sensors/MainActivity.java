package ie.corktrainingcentre.sensors;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Util.setActivity(this); // needed for resource handling

       Sensor.loadData(this);  // loads all the sensors

        SensorView v = new SensorView();
        v.bindView(this);


    }


}

/*
    The model
 */
class Sensor {

    static String[] sensorNames;
    static String[] fileNames;  // images and details are in these files
    static Sensor[] sensors;
    static int sensorCount;

    String name;
    String fileName;
    String details;
    String imageName;


    static Activity appAct;

    static void loadData(Activity act) {
        appAct=act;
        Resources res = appAct.getResources();
        // read the strings from the res arrays
        sensorNames = res.getStringArray(R.array.sensorNames);
        fileNames = res.getStringArray(R.array.fileNames);
        // make space for the Sensor objects
        sensorCount=sensorNames.length;
        sensors=new Sensor[sensorCount];

        for (int i = 0; i < sensorCount ; i++) {
            sensors[i]=new Sensor(i);
        }

    }

    // the index gives access to the file names etc
    public Sensor(int index) {

        name = sensorNames[index];
        fileName=fileNames[index];
        details = Util.readResourceFile(fileName);
        imageName=fileName+".jpg";

    }
    /*
        should define a set of getters
     */

}


/*
    bind to the various controls representing the values
 */
class SensorView {

    Activity appAct;
    TextView sensorName;
    TextView sensorDetails;
    ImageView sensorImage;

    /*
        takes a view, and "binds" / stores the controls in the view that match for the data
     */
    public void bindView(View v) {
        // we need IDs for each named Control

        // appAct.findViewById(R.id.tvName);   // easy way
        int id = Util.getResourceId("tvName","id");
        sensorName = (TextView) v.findViewById(id);

        sensorDetails = (TextView) v.findViewById(R.id.tvDetails);
        sensorImage = (ImageView) v.findViewById(R.id.ivImage);

    }

    public void bindView(Activity act) {
        appAct = act;
        View v = appAct.findViewById(R.id.layout_sensor_linear);
        bindView(v);
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
    public void setSensorImage(String imageName) {
        sensorImage.setImageResource( Util.getResourceId(imageName,"drawable"));
    }

    void bindData(Sensor sensor) {

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





