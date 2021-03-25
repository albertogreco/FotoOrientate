package b4a.example;


import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class main extends Activity implements B4AActivity{
	public static main mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = false;
	public static final boolean includeTitle = true;
    public static WeakReference<Activity> previousOne;
    public static boolean dontPause;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mostCurrent = this;
		if (processBA == null) {
			processBA = new BA(this.getApplicationContext(), null, null, "b4a.example", "b4a.example.main");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (main).");
				p.finish();
			}
		}
        processBA.setActivityPaused(true);
        processBA.runHook("oncreate", this, null);
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
        WaitForLayout wl = new WaitForLayout();
        if (anywheresoftware.b4a.objects.ServiceHelper.StarterHelper.startFromActivity(this, processBA, wl, false))
		    BA.handler.postDelayed(wl, 5);

	}
	static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "b4a.example", "b4a.example.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "b4a.example.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (main) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (main) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
        try {
            if (processBA.subExists("activity_actionbarhomeclick")) {
                Class.forName("android.app.ActionBar").getMethod("setHomeButtonEnabled", boolean.class).invoke(
                    getClass().getMethod("getActionBar").invoke(this), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (processBA.runHook("oncreateoptionsmenu", this, new Object[] {menu}))
            return true;
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
        
		return true;
	}   
 @Override
 public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (item.getItemId() == 16908332) {
        processBA.raiseEvent(null, "activity_actionbarhomeclick");
        return true;
    }
    else
        return super.onOptionsItemSelected(item); 
}
@Override
 public boolean onPrepareOptionsMenu(android.view.Menu menu) {
    super.onPrepareOptionsMenu(menu);
    processBA.runHook("onprepareoptionsmenu", this, new Object[] {menu});
    return true;
    
 }
 protected void onStart() {
    super.onStart();
    processBA.runHook("onstart", this, null);
}
 protected void onStop() {
    super.onStop();
    processBA.runHook("onstop", this, null);
}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEventFromUI(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return main.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeydown", this, new Object[] {keyCode, event}))
            return true;
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeyup", this, new Object[] {keyCode, event}))
            return true;
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
		this.setIntent(intent);
        processBA.runHook("onnewintent", this, new Object[] {intent});
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null)
            return;
        if (this != mostCurrent)
			return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        if (!dontPause)
            BA.LogInfo("** Activity (main) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        else
            BA.LogInfo("** Activity (main) Pause event (activity is not paused). **");
        if (mostCurrent != null)
            processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        if (!dontPause) {
            processBA.setActivityPaused(true);
            mostCurrent = null;
        }

        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        processBA.runHook("onpause", this, null);
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
        processBA.runHook("ondestroy", this, null);
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
        processBA.runHook("onresume", this, null);
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
            main mc = mostCurrent;
			if (mc == null || mc != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (main) Resume **");
            if (mc != mostCurrent)
                return;
		    processBA.raiseEvent(mc._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
        processBA.runHook("onactivityresult", this, new Object[] {requestCode, resultCode});
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}
    public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {
        for (int i = 0;i < permissions.length;i++) {
            Object[] o = new Object[] {permissions[i], grantResults[i] == 0};
            processBA.raiseEventFromDifferentThread(null,null, 0, "activity_permissionresult", true, o);
        }
            
    }

public anywheresoftware.b4a.keywords.Common __c = null;
public static anywheresoftware.b4a.objects.Timer _fototimer = null;
public static int _numerofotoprese = 0;
public static String _myfolder = "";
public anywheresoftware.b4a.objects.RuntimePermissions _rp = null;
public de.donmanfred.CameraViewwrapper _cam = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnstart = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnstop = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnsnapshot = null;
public anywheresoftware.b4a.objects.LabelWrapper _label1 = null;
public static boolean _ciclofoto = false;
public static String _stringastato = "";
public anywheresoftware.b4a.objects.ButtonWrapper _btnhelp = null;
public anywheresoftware.b4a.sample.SensorExtender _pa = null;
public anywheresoftware.b4a.sample.SensorExtender _pm = null;
public static float[] _racc = null;
public static boolean _ready = false;
public static float[] _ori = null;
public b4a.example.starter _starter = null;
public b4a.example.help _help = null;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
vis = vis | (help.mostCurrent != null);
return vis;}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 45;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 47;BA.debugLine="Activity.LoadLayout(\"Layout\")";
mostCurrent._activity.LoadLayout("Layout",mostCurrent.activityBA);
 //BA.debugLineNum = 48;BA.debugLine="MyFolder = rp.GetSafeDirDefaultExternal(\"prove\")";
_myfolder = mostCurrent._rp.GetSafeDirDefaultExternal("prove");
 //BA.debugLineNum = 49;BA.debugLine="StringaStato=\"\"";
mostCurrent._stringastato = "";
 //BA.debugLineNum = 50;BA.debugLine="Label1.Text=StringaStato";
mostCurrent._label1.setText(BA.ObjectToCharSequence(mostCurrent._stringastato));
 //BA.debugLineNum = 52;BA.debugLine="cam.Facing = \"BACK\"		' oppure \"FRONT\"";
mostCurrent._cam.setFacing("BACK");
 //BA.debugLineNum = 53;BA.debugLine="cam.setSessionVideo";
mostCurrent._cam.setSessionVideo();
 //BA.debugLineNum = 54;BA.debugLine="FotoTimer.Initialize(\"FotoTimer\",1000)";
_fototimer.Initialize(processBA,"FotoTimer",(long) (1000));
 //BA.debugLineNum = 55;BA.debugLine="FotoTimer.Enabled=False";
_fototimer.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 56;BA.debugLine="CicloFoto=False";
_ciclofoto = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 58;BA.debugLine="pa.Initialize2(pa.TYPE_ACCELEROMETER,3)";
mostCurrent._pa.Initialize2(mostCurrent._pa.TYPE_ACCELEROMETER,(int) (3));
 //BA.debugLineNum = 59;BA.debugLine="pm.Initialize2(pa.TYPE_MAGNETIC_FIELD,3)";
mostCurrent._pm.Initialize2(mostCurrent._pa.TYPE_MAGNETIC_FIELD,(int) (3));
 //BA.debugLineNum = 61;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 126;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 127;BA.debugLine="If CicloFoto Then";
if (_ciclofoto) { 
 //BA.debugLineNum = 128;BA.debugLine="FotoTimer.Enabled=False";
_fototimer.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 130;BA.debugLine="cam.pause";
mostCurrent._cam.pause();
 //BA.debugLineNum = 132;BA.debugLine="pm.StopListening";
mostCurrent._pm.StopListening(processBA);
 //BA.debugLineNum = 133;BA.debugLine="pa.StopListening";
mostCurrent._pa.StopListening(processBA);
 //BA.debugLineNum = 134;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 114;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 116;BA.debugLine="cam.start";
mostCurrent._cam.start();
 //BA.debugLineNum = 117;BA.debugLine="If CicloFoto Then";
if (_ciclofoto) { 
 //BA.debugLineNum = 118;BA.debugLine="FotoTimer.Enabled=True";
_fototimer.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 121;BA.debugLine="pa.StartListening(\"MF\")";
mostCurrent._pa.StartListening(processBA,"MF");
 //BA.debugLineNum = 122;BA.debugLine="pm.StartListening(\"MF\")";
mostCurrent._pm.StartListening(processBA,"MF");
 //BA.debugLineNum = 123;BA.debugLine="Ready = False";
_ready = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 125;BA.debugLine="End Sub";
return "";
}
public static String  _attivaciclofoto() throws Exception{
 //BA.debugLineNum = 106;BA.debugLine="Sub AttivaCicloFoto()";
 //BA.debugLineNum = 107;BA.debugLine="CicloFoto=True";
_ciclofoto = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 108;BA.debugLine="FotoTimer.Enabled=True";
_fototimer.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 109;BA.debugLine="End Sub";
return "";
}
public static String  _btnhelp_click() throws Exception{
 //BA.debugLineNum = 184;BA.debugLine="Private Sub btnHelp_Click";
 //BA.debugLineNum = 185;BA.debugLine="If CicloFoto Then";
if (_ciclofoto) { 
 //BA.debugLineNum = 186;BA.debugLine="DisattivaCicloFoto";
_disattivaciclofoto();
 };
 //BA.debugLineNum = 188;BA.debugLine="StartActivity(\"Help\")";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)("Help"));
 //BA.debugLineNum = 189;BA.debugLine="End Sub";
return "";
}
public static String  _btnsnapshot_click() throws Exception{
 //BA.debugLineNum = 153;BA.debugLine="Sub btnSnapshot_Click";
 //BA.debugLineNum = 154;BA.debugLine="cam.setSessionPicture";
mostCurrent._cam.setSessionPicture();
 //BA.debugLineNum = 155;BA.debugLine="cam.captureSnapshot";
mostCurrent._cam.captureSnapshot();
 //BA.debugLineNum = 156;BA.debugLine="End Sub";
return "";
}
public static String  _btnstart_click() throws Exception{
 //BA.debugLineNum = 137;BA.debugLine="Sub btnStart_Click";
 //BA.debugLineNum = 139;BA.debugLine="AttivaCicloFoto";
_attivaciclofoto();
 //BA.debugLineNum = 143;BA.debugLine="End Sub";
return "";
}
public static String  _btnstop_click() throws Exception{
 //BA.debugLineNum = 145;BA.debugLine="Sub btnStop_Click";
 //BA.debugLineNum = 146;BA.debugLine="If cam.Started Then";
if (mostCurrent._cam.getStarted()) { 
 //BA.debugLineNum = 147;BA.debugLine="cam.stop";
mostCurrent._cam.stop();
 };
 //BA.debugLineNum = 149;BA.debugLine="DisattivaCicloFoto";
_disattivaciclofoto();
 //BA.debugLineNum = 151;BA.debugLine="End Sub";
return "";
}
public static String  _bytestofile(String _dir,String _filename,byte[] _data) throws Exception{
anywheresoftware.b4a.objects.streams.File.OutputStreamWrapper _out = null;
 //BA.debugLineNum = 174;BA.debugLine="Sub BytesToFile (Dir As String, FileName As String";
 //BA.debugLineNum = 175;BA.debugLine="Dim out As OutputStream = File.OpenOutput(Dir, Fi";
_out = new anywheresoftware.b4a.objects.streams.File.OutputStreamWrapper();
_out = anywheresoftware.b4a.keywords.Common.File.OpenOutput(_dir,_filename,anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 176;BA.debugLine="out.WriteBytes(Data, 0, Data.Length)";
_out.WriteBytes(_data,(int) (0),_data.length);
 //BA.debugLineNum = 177;BA.debugLine="out.Close";
_out.Close();
 //BA.debugLineNum = 178;BA.debugLine="End Sub";
return "";
}
public static anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper  _bytestoimage(byte[] _bytes) throws Exception{
anywheresoftware.b4a.objects.streams.File.InputStreamWrapper _in = null;
anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _bmp = null;
 //BA.debugLineNum = 166;BA.debugLine="Public Sub BytesToImage(bytes() As Byte) As Bitmap";
 //BA.debugLineNum = 167;BA.debugLine="Dim In As InputStream";
_in = new anywheresoftware.b4a.objects.streams.File.InputStreamWrapper();
 //BA.debugLineNum = 168;BA.debugLine="In.InitializeFromBytesArray(bytes, 0, bytes.Lengt";
_in.InitializeFromBytesArray(_bytes,(int) (0),_bytes.length);
 //BA.debugLineNum = 169;BA.debugLine="Dim bmp As Bitmap";
_bmp = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
 //BA.debugLineNum = 170;BA.debugLine="bmp.Initialize2(In)";
_bmp.Initialize2((java.io.InputStream)(_in.getObject()));
 //BA.debugLineNum = 171;BA.debugLine="Return bmp";
if (true) return _bmp;
 //BA.debugLineNum = 172;BA.debugLine="End Sub";
return null;
}
public static String  _cam_oncameraclosed() throws Exception{
 //BA.debugLineNum = 66;BA.debugLine="Sub Cam_onCameraClosed()";
 //BA.debugLineNum = 67;BA.debugLine="Log($\"Cam_onCameraClosed()\"$)";
anywheresoftware.b4a.keywords.Common.LogImpl("7262145",("Cam_onCameraClosed()"),0);
 //BA.debugLineNum = 68;BA.debugLine="End Sub";
return "";
}
public static String  _cam_oncameraopened(Object _options) throws Exception{
 //BA.debugLineNum = 69;BA.debugLine="Sub Cam_onCameraOpened(options As Object)";
 //BA.debugLineNum = 70;BA.debugLine="Log($\"Cam_onCameraOpened()\"$)";
anywheresoftware.b4a.keywords.Common.LogImpl("7327681",("Cam_onCameraOpened()"),0);
 //BA.debugLineNum = 71;BA.debugLine="End Sub";
return "";
}
public static String  _cam_onexposurecorrectionchanged(float _newvalue,float[] _bounds,Object[] _fingers) throws Exception{
 //BA.debugLineNum = 72;BA.debugLine="Sub Cam_onExposureCorrectionChanged(newValue As Fl";
 //BA.debugLineNum = 73;BA.debugLine="Log($\"Cam_onExposureCorrectionChanged(${newValue}";
anywheresoftware.b4a.keywords.Common.LogImpl("7393217",("Cam_onExposureCorrectionChanged("+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_newvalue))+","+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_bounds))+","+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_fingers))+")"),0);
 //BA.debugLineNum = 74;BA.debugLine="End Sub";
return "";
}
public static String  _cam_onfocusend(boolean _success,int _x,int _y) throws Exception{
 //BA.debugLineNum = 75;BA.debugLine="Sub Cam_onFocusEnd(success As Boolean, x As Int, y";
 //BA.debugLineNum = 76;BA.debugLine="Log($\"Cam_onFocusEnd(${success},${x},${y})\"$)";
anywheresoftware.b4a.keywords.Common.LogImpl("7458753",("Cam_onFocusEnd("+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_success))+","+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_x))+","+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_y))+")"),0);
 //BA.debugLineNum = 77;BA.debugLine="End Sub";
return "";
}
public static String  _cam_onorientationchanged(int _orientation) throws Exception{
 //BA.debugLineNum = 78;BA.debugLine="Sub Cam_onOrientationChanged(orientation As Int)";
 //BA.debugLineNum = 79;BA.debugLine="Log($\"Cam_onOrientationChanged(${orientation})\"$)";
anywheresoftware.b4a.keywords.Common.LogImpl("7524289",("Cam_onOrientationChanged("+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_orientation))+")"),0);
 //BA.debugLineNum = 80;BA.debugLine="End Sub";
return "";
}
public static String  _cam_onpicturetaken(byte[] _jpeg) throws Exception{
String _filename = "";
anywheresoftware.b4a.objects.streams.File.TextWriterWrapper _writer = null;
String _riga = "";
 //BA.debugLineNum = 81;BA.debugLine="Sub Cam_onPictureTaken(jpeg() As Byte)";
 //BA.debugLineNum = 82;BA.debugLine="NumeroFotoPrese=NumeroFotoPrese+1";
_numerofotoprese = (int) (_numerofotoprese+1);
 //BA.debugLineNum = 83;BA.debugLine="Log($\"Cam_onPictureTaken(): \"$& NumeroFotoPrese)";
anywheresoftware.b4a.keywords.Common.LogImpl("7589826",("Cam_onPictureTaken(): ")+BA.NumberToString(_numerofotoprese),0);
 //BA.debugLineNum = 84;BA.debugLine="Dim filename As String";
_filename = "";
 //BA.debugLineNum = 85;BA.debugLine="DateTime.DateFormat=\"yyyy-MM-dd\"";
anywheresoftware.b4a.keywords.Common.DateTime.setDateFormat("yyyy-MM-dd");
 //BA.debugLineNum = 86;BA.debugLine="filename = \"test\" & DateTime.Date(DateTime.Now) &";
_filename = "test"+anywheresoftware.b4a.keywords.Common.DateTime.Date(anywheresoftware.b4a.keywords.Common.DateTime.getNow())+" "+anywheresoftware.b4a.keywords.Common.DateTime.Time(anywheresoftware.b4a.keywords.Common.DateTime.getNow())+".jpg";
 //BA.debugLineNum = 87;BA.debugLine="Log($\"Nome file: ${filename}\"$)";
anywheresoftware.b4a.keywords.Common.LogImpl("7589830",("Nome file: "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_filename))+""),0);
 //BA.debugLineNum = 88;BA.debugLine="BytesToFile(MyFolder,filename,jpeg)";
_bytestofile(_myfolder,_filename,_jpeg);
 //BA.debugLineNum = 89;BA.debugLine="Dim writer As TextWriter";
_writer = new anywheresoftware.b4a.objects.streams.File.TextWriterWrapper();
 //BA.debugLineNum = 90;BA.debugLine="writer.Initialize(File.OpenOutput(MyFolder,\"elenc";
_writer.Initialize((java.io.OutputStream)(anywheresoftware.b4a.keywords.Common.File.OpenOutput(_myfolder,"elenco-foto.txt",anywheresoftware.b4a.keywords.Common.True).getObject()));
 //BA.debugLineNum = 91;BA.debugLine="Dim riga As String";
_riga = "";
 //BA.debugLineNum = 92;BA.debugLine="riga=filename &\",\"& NumeroFotoPrese &\",\"  & Numbe";
_riga = _filename+","+BA.NumberToString(_numerofotoprese)+","+anywheresoftware.b4a.keywords.Common.NumberFormat(_ori[(int) (0)],(int) (0),(int) (2))+", "+anywheresoftware.b4a.keywords.Common.NumberFormat(_ori[(int) (1)],(int) (0),(int) (2))+", "+anywheresoftware.b4a.keywords.Common.NumberFormat(_ori[(int) (2)],(int) (0),(int) (2));
 //BA.debugLineNum = 93;BA.debugLine="writer.WriteLine(riga)";
_writer.WriteLine(_riga);
 //BA.debugLineNum = 94;BA.debugLine="writer.Close";
_writer.Close();
 //BA.debugLineNum = 95;BA.debugLine="Label1.Text=StringaStato &\"Foto n:\"& NumeroFotoPr";
mostCurrent._label1.setText(BA.ObjectToCharSequence(mostCurrent._stringastato+"Foto n:"+BA.NumberToString(_numerofotoprese)));
 //BA.debugLineNum = 96;BA.debugLine="cam.start";
mostCurrent._cam.start();
 //BA.debugLineNum = 97;BA.debugLine="End Sub";
return "";
}
public static String  _cam_onvideotaken(String _path,String _filename) throws Exception{
 //BA.debugLineNum = 98;BA.debugLine="Sub Cam_onVideoTaken(path As String, filename As S";
 //BA.debugLineNum = 99;BA.debugLine="Log($\"Cam_onVideoTaken(${path},${filename})\"$)";
anywheresoftware.b4a.keywords.Common.LogImpl("7655361",("Cam_onVideoTaken("+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_path))+","+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_filename))+")"),0);
 //BA.debugLineNum = 100;BA.debugLine="cam.start";
mostCurrent._cam.start();
 //BA.debugLineNum = 101;BA.debugLine="End Sub";
return "";
}
public static String  _cam_onzoomchanged(float _newvalue,float[] _bounds,Object[] _fingers) throws Exception{
 //BA.debugLineNum = 102;BA.debugLine="Sub Cam_onZoomChanged(newValue As Float, bounds()";
 //BA.debugLineNum = 103;BA.debugLine="Log($\"Cam_onZoomchanged(${newValue},${bounds},${f";
anywheresoftware.b4a.keywords.Common.LogImpl("7720897",("Cam_onZoomchanged("+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_newvalue))+","+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_bounds))+","+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_fingers))+")"),0);
 //BA.debugLineNum = 104;BA.debugLine="End Sub";
return "";
}
public static String  _disattivaciclofoto() throws Exception{
 //BA.debugLineNum = 110;BA.debugLine="Sub DisattivaCicloFoto()";
 //BA.debugLineNum = 111;BA.debugLine="FotoTimer.Enabled=False";
_fototimer.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 112;BA.debugLine="CicloFoto=False";
_ciclofoto = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 113;BA.debugLine="End Sub";
return "";
}
public static byte[]  _filetobytes(String _dir,String _filename) throws Exception{
 //BA.debugLineNum = 180;BA.debugLine="Sub FileToBytes (Dir As String, FileName As String";
 //BA.debugLineNum = 181;BA.debugLine="Return Bit.InputStreamToBytes(File.OpenInput(Dir,";
if (true) return anywheresoftware.b4a.keywords.Common.Bit.InputStreamToBytes((java.io.InputStream)(anywheresoftware.b4a.keywords.Common.File.OpenInput(_dir,_filename).getObject()));
 //BA.debugLineNum = 182;BA.debugLine="End Sub";
return null;
}
public static String  _fototimer_tick() throws Exception{
 //BA.debugLineNum = 62;BA.debugLine="Sub FotoTimer_Tick";
 //BA.debugLineNum = 64;BA.debugLine="btnSnapshot_Click";
_btnsnapshot_click();
 //BA.debugLineNum = 65;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 23;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 27;BA.debugLine="Private rp As RuntimePermissions";
mostCurrent._rp = new anywheresoftware.b4a.objects.RuntimePermissions();
 //BA.debugLineNum = 28;BA.debugLine="Private cam As CameraView";
mostCurrent._cam = new de.donmanfred.CameraViewwrapper();
 //BA.debugLineNum = 29;BA.debugLine="Private btnStart As Button";
mostCurrent._btnstart = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 30;BA.debugLine="Private btnStop As Button";
mostCurrent._btnstop = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 31;BA.debugLine="Private btnSnapshot As Button";
mostCurrent._btnsnapshot = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 32;BA.debugLine="Private Label1 As Label";
mostCurrent._label1 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 33;BA.debugLine="Private CicloFoto As Boolean";
_ciclofoto = false;
 //BA.debugLineNum = 34;BA.debugLine="Private StringaStato As String";
mostCurrent._stringastato = "";
 //BA.debugLineNum = 35;BA.debugLine="Private btnHelp As Button";
mostCurrent._btnhelp = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 37;BA.debugLine="Dim pa, pm As SensorExtender";
mostCurrent._pa = new anywheresoftware.b4a.sample.SensorExtender();
mostCurrent._pm = new anywheresoftware.b4a.sample.SensorExtender();
 //BA.debugLineNum = 38;BA.debugLine="Dim rACC(3) As Float";
_racc = new float[(int) (3)];
;
 //BA.debugLineNum = 39;BA.debugLine="Dim Ready As Boolean";
_ready = false;
 //BA.debugLineNum = 41;BA.debugLine="Dim ORi(3) As Float";
_ori = new float[(int) (3)];
;
 //BA.debugLineNum = 43;BA.debugLine="End Sub";
return "";
}
public static byte[]  _imagetobytes(anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _image) throws Exception{
anywheresoftware.b4a.objects.streams.File.OutputStreamWrapper _out = null;
 //BA.debugLineNum = 158;BA.debugLine="Public Sub ImageToBytes(Image As Bitmap) As Byte()";
 //BA.debugLineNum = 159;BA.debugLine="Dim out As OutputStream";
_out = new anywheresoftware.b4a.objects.streams.File.OutputStreamWrapper();
 //BA.debugLineNum = 160;BA.debugLine="out.InitializeToBytesArray(0)";
_out.InitializeToBytesArray((int) (0));
 //BA.debugLineNum = 161;BA.debugLine="Image.WriteToStream(out, 100, \"JPEG\")";
_image.WriteToStream((java.io.OutputStream)(_out.getObject()),(int) (100),BA.getEnumFromString(android.graphics.Bitmap.CompressFormat.class,"JPEG"));
 //BA.debugLineNum = 162;BA.debugLine="out.Close";
_out.Close();
 //BA.debugLineNum = 163;BA.debugLine="Return out.ToBytesArray";
if (true) return _out.ToBytesArray();
 //BA.debugLineNum = 164;BA.debugLine="End Sub";
return null;
}
public static String  _mf_accuracychanged(int _newaccuracy) throws Exception{
anywheresoftware.b4a.sample.SensorExtender _se = null;
 //BA.debugLineNum = 220;BA.debugLine="Sub MF_accuracychanged(NewAccuracy As Int)";
 //BA.debugLineNum = 221;BA.debugLine="Dim se As SensorExtender";
_se = new anywheresoftware.b4a.sample.SensorExtender();
 //BA.debugLineNum = 222;BA.debugLine="se = Sender";
_se = (anywheresoftware.b4a.sample.SensorExtender)(anywheresoftware.b4a.keywords.Common.Sender(mostCurrent.activityBA));
 //BA.debugLineNum = 224;BA.debugLine="End Sub";
return "";
}
public static String  _mf_sensorchanged(float[] _values) throws Exception{
anywheresoftware.b4a.sample.SensorExtender _se = null;
float[] _r = null;
float[] _i = null;
 //BA.debugLineNum = 191;BA.debugLine="Sub MF_SensorChanged(Values() As Float)";
 //BA.debugLineNum = 192;BA.debugLine="Dim se As SensorExtender";
_se = new anywheresoftware.b4a.sample.SensorExtender();
 //BA.debugLineNum = 193;BA.debugLine="se = Sender";
_se = (anywheresoftware.b4a.sample.SensorExtender)(anywheresoftware.b4a.keywords.Common.Sender(mostCurrent.activityBA));
 //BA.debugLineNum = 198;BA.debugLine="Select se.GetType";
switch (BA.switchObjectToInt(_se.GetType(),(float) (_se.TYPE_ACCELEROMETER),(float) (_se.TYPE_MAGNETIC_FIELD))) {
case 0: {
 //BA.debugLineNum = 201;BA.debugLine="rACC = se.LowPassFilter(Values,rACC,se.FILTERING";
_racc = _se.LowPassFilter(_values,_racc,_se.FILTERING_FACTOR_Recommended);
 //BA.debugLineNum = 202;BA.debugLine="Ready = True";
_ready = anywheresoftware.b4a.keywords.Common.True;
 break; }
case 1: {
 //BA.debugLineNum = 205;BA.debugLine="If Ready Then";
if (_ready) { 
 //BA.debugLineNum = 206;BA.debugLine="Ready = False";
_ready = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 207;BA.debugLine="Dim R(16) As Float";
_r = new float[(int) (16)];
;
 //BA.debugLineNum = 208;BA.debugLine="Dim i(16) As Float";
_i = new float[(int) (16)];
;
 //BA.debugLineNum = 209;BA.debugLine="If se.GetRotationMatrix(R,I,Values,rACC) Then";
if (_se.GetRotationMatrix(_r,_i,_values,_racc)) { 
 //BA.debugLineNum = 210;BA.debugLine="ORi = se.GetOrientation(R)";
_ori = _se.GetOrientation(_r);
 //BA.debugLineNum = 211;BA.debugLine="Label1.Text = \"Orientamento in radianti:\" & CR";
mostCurrent._label1.setText(BA.ObjectToCharSequence("Orientamento in radianti:"+anywheresoftware.b4a.keywords.Common.CRLF+anywheresoftware.b4a.keywords.Common.NumberFormat(_ori[(int) (0)],(int) (0),(int) (2))+", "+anywheresoftware.b4a.keywords.Common.NumberFormat(_ori[(int) (1)],(int) (0),(int) (2))+", "+anywheresoftware.b4a.keywords.Common.NumberFormat(_ori[(int) (2)],(int) (0),(int) (2))));
 }else {
 //BA.debugLineNum = 213;BA.debugLine="Label1.Text = \"Errore: non riesco a leggere l'";
mostCurrent._label1.setText(BA.ObjectToCharSequence("Errore: non riesco a leggere l'orientamento!"));
 };
 };
 break; }
}
;
 //BA.debugLineNum = 217;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        main._process_globals();
starter._process_globals();
help._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 15;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 18;BA.debugLine="Private FotoTimer As Timer";
_fototimer = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 19;BA.debugLine="Public NumeroFotoPrese As Int";
_numerofotoprese = 0;
 //BA.debugLineNum = 20;BA.debugLine="Public MyFolder As String";
_myfolder = "";
 //BA.debugLineNum = 21;BA.debugLine="End Sub";
return "";
}
}
