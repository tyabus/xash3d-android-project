package in.celest.xash3d;

import java.util.ArrayList;
import java.util.List;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.net.*;
import android.os.*;
import android.text.*;
import android.text.method.*;
import android.text.style.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import android.Manifest;
import android.content.pm.PackageManager;

import in.celest.xash3d.hl.*;
import java.io.*;
import java.net.*;
import org.json.*;
import android.preference.*;
import su.xash.fwgslib.*;

public class LauncherActivity extends Activity 
{
	public final static int sdk = FWGSLib.sdk;
	static SharedPreferences mPref;

	static EditText cmdArgs, resPath, writePath, resScale, resWidth, resHeight;
	static ToggleButton useVolume, resizeWorkaround, useRoDir;
	static CheckBox	immersiveMode, useRoDirAuto;
	static TextView tvResPath, resResult;
	static RadioButton radioScale, radioCustom;
	static RadioGroup scaleGroup;
	static CheckBox resolution;
	static Spinner pixelSpinner;
	static LinearLayout rodirSettings; // to easy show/hide
	
	static int mEngineWidth, mEngineHeight;

	final static int REQUEST_PERMISSIONS = 42;

	public void onRequestPermissionsResult( int requestCode, String[] permissions, int[] grantResults )
	{
		boolean bEssentialPermissionsGranted = true;

		if( requestCode != REQUEST_PERMISSIONS )
			return;

		for( int i = 0; i < grantResults.length; i++ )
		{
			String permission = permissions[i];
			int grantResult = grantResults[i];

			if( permission.equals( Manifest.permission.READ_EXTERNAL_STORAGE ) || permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE ) )
			{
				if( grantResult != PackageManager.PERMISSION_GRANTED )
				{
					 bEssentialPermissionsGranted = false;
				}
			}
		}

		if( !bEssentialPermissionsGranted )
		{
			Toast.makeText( this, "Storage permissions are required!", Toast.LENGTH_LONG ).show();
			finish();
		}
	}

	public void applyPermissions( final String[] permissions, final int code )
	{
		List< String > requestPermissions = new ArrayList<>();
		for( String permission : permissions )
		{
			if( checkSelfPermission( permission ) != PackageManager.PERMISSION_GRANTED )
				requestPermissions.add( permission );
		}

		if( !requestPermissions.isEmpty() )
		{
			String[] requestPermissionsArray = new String[ requestPermissions.size() ];
			for( int i = 0; i < requestPermissions.size(); i++ )
				requestPermissionsArray[ i ] = requestPermissions.get( i );
			requestPermissions( requestPermissionsArray, code );
		}
	}

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		//super.setTheme( 0x01030005 );
		if ( sdk >= 21 )
			super.setTheme( 0x01030224 );
		else super.setTheme( 0x01030005 );

		setContentView(R.layout.activity_launcher);

		TabHost tabHost = (TabHost) findViewById(R.id.tabhost);

		tabHost.setup();
		
		TabHost.TabSpec tabSpec;
		tabSpec = tabHost.newTabSpec("tabtag1");
		tabSpec.setIndicator(getString(R.string.text_tab1));
		tabSpec.setContent(R.id.tab1);
		tabHost.addTab(tabSpec);

		tabSpec = tabHost.newTabSpec("tabtag2");
		tabSpec.setIndicator(getString(R.string.text_tab2));
		tabSpec.setContent(R.id.tab2);
		tabHost.addTab(tabSpec);
		if( sdk < 21 )
		{
			try
			{
				tabHost.invalidate();
				for(int i = 0; i < tabHost.getTabWidget().getChildCount(); i++)
				{
					tabHost.getTabWidget().getChildAt(i).getBackground().setAlpha(255);
					tabHost.getTabWidget().getChildAt(i).getLayoutParams().height = (int) (40 * getResources().getDisplayMetrics().density);
				}
			}
			catch(Exception e){}
		}
		

		mPref        = getSharedPreferences("engine", 0);
		cmdArgs      = (EditText) findViewById(R.id.cmdArgs);
		useVolume    = (ToggleButton) findViewById( R.id.useVolume );
		resPath      = (EditText) findViewById( R.id.cmd_path );
		pixelSpinner = (Spinner) findViewById( R.id.pixelSpinner );
		resizeWorkaround = (ToggleButton) findViewById( R.id.enableResizeWorkaround );
		tvResPath    = (TextView) findViewById( R.id.textView_path );
		immersiveMode = (CheckBox) findViewById( R.id.immersive_mode );
		resolution = (CheckBox) findViewById(R.id.resolution);
		resWidth = (EditText) findViewById(R.id.resolution_width);
		resHeight = (EditText) findViewById(R.id.resolution_height);
		resScale = (EditText) findViewById(R.id.resolution_scale);
		radioCustom = (RadioButton) findViewById(R.id.resolution_custom_r);
		radioScale = (RadioButton) findViewById(R.id.resolution_scale_r);
		scaleGroup = (RadioGroup) findViewById( R.id.scale_group );
		resResult = (TextView) findViewById( R.id.resolution_result );
		writePath = (EditText) findViewById( R.id.cmd_path_rw );
		useRoDir = (ToggleButton) findViewById( R.id.use_rodir );
		useRoDirAuto = (CheckBox) findViewById( R.id.use_rodir_auto );
		rodirSettings = (LinearLayout) findViewById( R.id.rodir_settings );
		
		final String[] list = {
			"32 bit (RGBA8888)",
			"24 bit (RGB888)",
			"16 bit (RGB565)",
			"16 bit (RGBA5551)",
			"16 bit (RGBA4444)",
			"8 bit (RGB332)"
		};
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, list);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		pixelSpinner.setAdapter(adapter);
		Button selectFolderButton = ( Button ) findViewById( R.id.button_select );
		selectFolderButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				selectFolder(v);
			}
		});
		((Button)findViewById( R.id.button_launch )).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				startXash(v);
			}
		});
		((Button)findViewById( R.id.button_shortcut )).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				createShortcut(v);
			}
		});
		((Button)findViewById( R.id.button_about )).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				aboutXash(v);
			}
		});
		useVolume.setChecked(mPref.getBoolean("usevolume",true));
		updatePath(mPref.getString("basedir", FWGSLib.getDefaultXashPath() ) );
		cmdArgs.setText(mPref.getString("argv","-dev 3 -log"));
		pixelSpinner.setSelection(mPref.getInt("pixelformat", 0));
		resizeWorkaround.setChecked(mPref.getBoolean("enableResizeWorkaround", true));
		useRoDir.setChecked( mPref.getBoolean("use_rodir", false) );
		useRoDirAuto.setChecked( mPref.getBoolean("use_rodir_auto", true) );
		writePath.setText(mPref.getString("writedir", FWGSLib.getExternalFilesDir(this)));
		resolution.setChecked( mPref.getBoolean("resolution_fixed", false ) );
		
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		
		// Swap resolution here, because engine is always(should be always) run in landscape mode
		if( FWGSLib.isLandscapeOrientation( this ) )
		{
			mEngineWidth = metrics.widthPixels;
			mEngineHeight = metrics.heightPixels;
		}
		else
		{
			mEngineWidth = metrics.heightPixels;
			mEngineHeight = metrics.widthPixels;
		}		
		
		resWidth.setText(String.valueOf(mPref.getInt("resolution_width", mEngineWidth )));
		resHeight.setText(String.valueOf(mPref.getInt("resolution_height", mEngineHeight )));
		resScale.setText(String.valueOf(mPref.getFloat("resolution_scale", 2.0f)));
		
		resWidth.addTextChangedListener( resWidthTextChangeWatcher );
		resHeight.addTextChangedListener( resTextChangeWatcher );
		resScale.addTextChangedListener( resTextChangeWatcher );
		
		if( mPref.getBoolean("resolution_custom", false) )
			radioCustom.setChecked(true);
		else radioScale.setChecked(true);
		
		radioCustom.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged( CompoundButton v, boolean isChecked )
			{
				updateResolutionResult();
				toggleResolutionFields();
			}
		} );
		resolution.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged( CompoundButton v, boolean isChecked )
			{
				hideResolutionSettings( !isChecked );
			}
		});
		
		useRoDir.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged( CompoundButton v, boolean isChecked )
			{
				hideRodirSettings( !isChecked );
			}
		});
		
		
		if( sdk >= 19 )
		{
			immersiveMode.setChecked(mPref.getBoolean("immersive_mode", true));
		}
		else
		{
			immersiveMode.setVisibility(View.GONE); // not available
		}
		
		resPath.setOnFocusChangeListener( new View.OnFocusChangeListener()
		{
			@Override
			public void onFocusChange(View v, boolean hasFocus)
			{
				updatePath( resPath.getText().toString() );
				
				// I know what I am doing, so don't ask me about folder!
				XashActivity.setFolderAsk( LauncherActivity.this, false );
			}
		} );
		
		useRoDirAuto.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged( CompoundButton b, boolean isChecked )
			{
				if( isChecked )
				{
					writePath.setText( FWGSLib.getExternalFilesDir( LauncherActivity.this ) );
				}
				writePath.setEnabled( !isChecked );
			}
		});

		FWGSLib.changeButtonsStyle((ViewGroup)tabHost.getParent());
		hideResolutionSettings( !resolution.isChecked() );
		hideRodirSettings( !useRoDir.isChecked() );
		updateResolutionResult();
		toggleResolutionFields();

		// Android 6 target requires this to be requested at a runtime
		applyPermissions( new String[] { Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE }, REQUEST_PERMISSIONS );

		if( !mPref.getBoolean("successfulRun",false) )
			showFirstRun();
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		useRoDir.setChecked( mPref.getBoolean("use_rodir", false) );
		useRoDirAuto.setChecked( mPref.getBoolean("use_rodir_auto", true) );
		writePath.setText(mPref.getString("writedir", FWGSLib.getExternalFilesDir(this)));
		
		hideRodirSettings( !useRoDir.isChecked() );
	}

	void updatePath( String text )
	{
		tvResPath.setText(getString(R.string.text_res_path) + ":\n" + text );
		resPath.setText(text);
	}
	
	void hideResolutionSettings( boolean hide )
	{
		scaleGroup.setVisibility( hide ? View.GONE : View.VISIBLE );
	}
	
	void hideRodirSettings( boolean hide )
	{
		rodirSettings.setVisibility( hide ? View.GONE : View.VISIBLE );
	}
		
	TextWatcher resWidthTextChangeWatcher = new TextWatcher()
	{
		@Override
		public void afterTextChanged(Editable s){}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after){}
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count)
		{
			int h = (int)((float)mEngineHeight / mEngineWidth * getCustomEngineWidth());
			resHeight.setText(String.valueOf(h));
			updateResolutionResult();
		}
	};

	TextWatcher resTextChangeWatcher = new TextWatcher()
	{
		@Override
		public void afterTextChanged(Editable s){}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after){}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count)
		{
			updateResolutionResult();
		}
	};
	
	void updateResolutionResult( )
	{
		int w, h;
		if( radioCustom.isChecked() )
		{
			w = getCustomEngineWidth();
			h = getCustomEngineHeight();

			// some fool-proof
			if( Math.abs((float)w/(float)h - 4.0/3.0) < 0.001 )
			{
				w = (int)((float)mEngineWidth / mEngineHeight * h+0.5);
				resWidth.setText(String.valueOf(w));
			}
		}
		else
		{
			final float scale = getResolutionScale();
			w = (int)((float)mEngineWidth / scale);
			h = (int)((float)mEngineHeight / scale);
		}
		
		resResult.setText( getString( R.string.resolution_result ) + w + "x" + h );
	}
	
	void toggleResolutionFields()
	{
		boolean isChecked = radioCustom.isChecked();
		resWidth.setEnabled( isChecked );
		resHeight.setEnabled( isChecked );
		resScale.setEnabled( !isChecked );
	}
	
	float getResolutionScale()
	{
		return FWGSLib.atof( resScale.getText().toString(), 1.0f );
	}
	
	int getCustomEngineHeight()
	{
		return FWGSLib.atoi( resHeight.getText().toString(), mEngineHeight );
	}
	
	int getCustomEngineWidth()
	{
		return FWGSLib.atoi( resWidth.getText().toString(), mEngineWidth );
	}
	
    public void startXash(View view)
    {
		Intent intent = new Intent(this, XashActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		SharedPreferences.Editor editor = mPref.edit();
		editor.putString("argv", cmdArgs.getText().toString());
		editor.putBoolean("usevolume",useVolume.isChecked());
		editor.putBoolean("use_rodir", useRoDir.isChecked() );
		editor.putBoolean("use_rodir_auto", useRoDirAuto.isChecked() );
		editor.putString("writedir", writePath.getText().toString());
		editor.putString("basedir", resPath.getText().toString());
		editor.putInt("pixelformat", pixelSpinner.getSelectedItemPosition());
		editor.putBoolean("enableResizeWorkaround",resizeWorkaround.isChecked());
		editor.putBoolean("resolution_fixed", resolution.isChecked());
		editor.putBoolean("resolution_custom", radioCustom.isChecked());
		editor.putFloat("resolution_scale", getResolutionScale() );
		editor.putInt("resolution_width", getCustomEngineWidth() );
		editor.putInt("resolution_height", getCustomEngineHeight() );
		
		if( sdk >= 19 )
			editor.putBoolean("immersive_mode", immersiveMode.isChecked());
		else
			editor.putBoolean("immersive_mode", false); // just in case...
		editor.commit();
		startActivity(intent);
    }

	public void aboutXash(View view)
	{
		final Activity a = this;
		this.runOnUiThread(new Runnable() 
		{
			public void run()
			{
				final Dialog dialog = new Dialog(a);
				dialog.setContentView(R.layout.about);
				dialog.setCancelable(true);
				dialog.show();
				TextView tView6 = (TextView) dialog.findViewById(R.id.textView6);
				tView6.setMovementMethod(LinkMovementMethod.getInstance());
				((Button)dialog.findViewById( R.id.button_about_ok )).setOnClickListener(new View.OnClickListener(){
	       			@Override
	    			public void onClick(View v) {
	    				dialog.cancel();
					}
				});
				((Button)dialog.findViewById( R.id.show_firstrun )).setOnClickListener(new View.OnClickListener(){
						@Override
						public void onClick(View v) {
							dialog.cancel();
							Intent intent = new Intent(a, XashTutorialActivity.class);
							startActivity(intent);
						}
					});
				FWGSLib.changeButtonsStyle((ViewGroup)dialog.findViewById( R.id.show_firstrun ).getParent());

			}
		});
	}

	int m_iFirstRunCounter = 0;
	public void showFirstRun()
	{
		startActivity(new Intent(this, in.celest.xash3d.XashTutorialActivity.class));
	}

	public static final int ID_SELECT_FOLDER = 42, ID_SELECT_RW_FOLDER = 43;

	public void selectFolder(View view)
	{
		Intent intent = new Intent(this, in.celest.xash3d.FPicker.class);
		startActivityForResult(intent, ID_SELECT_FOLDER);
		resPath.setEnabled(false);
		XashActivity.setFolderAsk( this, false );
	}
	
	public void selectRwFolder(View view)
	{
		Intent intent = new Intent(this, in.celest.xash3d.FPicker.class);
		startActivityForResult(intent, ID_SELECT_RW_FOLDER);
		writePath.setEnabled(false);
		XashActivity.setFolderAsk( this, false );
	}


	public void onActivityResult(int requestCode, int resultCode, Intent resultData) 
	{
		switch(requestCode)
		{
		case ID_SELECT_FOLDER:
		{
			if (resultCode == RESULT_OK) 
			{
				try	
				{
					if( resPath == null )
						return;
					updatePath(resultData.getStringExtra("GetPath"));
					resPath.setEnabled( true );
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			resPath.setEnabled(true);
			break;
		}
		case ID_SELECT_RW_FOLDER:
		{
			if (resultCode == RESULT_OK) 
			{
				try	
				{
					if( writePath == null )
						return;
					writePath.setText(resultData.getStringExtra("GetPath"));
					writePath.setEnabled( true );
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			writePath.setEnabled(true);
			break;
		}
		}
	}

	public void createShortcut(View view)
	{
		Intent intent = new Intent(this, ShortcutActivity.class);
		intent.putExtra( "basedir", resPath.getText().toString() );
		intent.putExtra( "name", "Xash3D" );
		intent.putExtra( "argv", cmdArgs.getText().toString() );
		startActivity(intent);
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_launcher, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }
}
