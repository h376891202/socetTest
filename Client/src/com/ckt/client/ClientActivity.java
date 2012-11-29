package com.ckt.client;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ckt.client.ClientService.ClienServiceListener;
import com.ckt.client.ClientService.ClientBinder;
import com.ckt.client.ServerService.ServerBinder;
import com.ckt.client.ServerService.ServerServiceListener;

public class ClientActivity extends Activity implements ClienServiceListener,OnClickListener,ServerServiceListener{
	private static final int MSG_CONECT_SUCCESS = 1;
	private static final int MSG_DISCONECT_SUCCESS = 2;
	private static final int MSG_GET_COMMAND = 3;
	private static final String TAG = "ClientActivity";
	
	private Button mConnectBtn;
	private EditText mHostIp;
	private ClientService mClientService;
	private ServerService mServerService;
	private LinearLayout layout;
	private boolean mIsClientServiceConnect;
	private boolean mIsServerServiceStarted;
	private ImageView mImage;
	
	public TextWatcher watcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {}
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {}
		@Override
		public void afterTextChanged(Editable s) {
			String sarr [] = s.toString().split("\\.");
			if (sarr.length == 4) {
				mConnectBtn.setEnabled(true);
			}else{
				mConnectBtn.setEnabled(false);
			}
		}
	};
	
	public ServiceConnection mConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
		}
			
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			if(ClientService.class.getName().equals(name.getClassName())){
				mClientService = ((ClientBinder) service).getService();
				mClientService.rejestListener(ClientActivity.this);
			}else if(ServerService.class.getName().equals(name.getClassName())){
				mServerService = ((ServerBinder)service).getService();
				mServerService.registeLisener(ClientActivity.this);
			}
		}
	};
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set no title and fullscreen
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.main);
		mConnectBtn = (Button) findViewById(R.id.connetct);
		mConnectBtn.setOnClickListener(this);
		mHostIp = (EditText) findViewById(R.id.hostinput);
		layout = (LinearLayout) findViewById(R.id.parent);
		layout.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
		mHostIp.addTextChangedListener(watcher);
		mImage = (ImageView) findViewById(R.id.imageView);
		mImage.setOnTouchListener(new ImageTouchListener());
		Intent intent = new Intent(this,ClientService.class);
		bindService(intent,mConnection,Context.BIND_AUTO_CREATE);
		Intent serverIntent = new Intent(ClientActivity.this,ServerService.class);
		bindService(serverIntent, mConnection, Context.BIND_AUTO_CREATE);
    }
    
    
    public Handler mHandler = new Handler(){
    	public void handleMessage(Message msg) {
    		switch (msg.what) {
			case MSG_CONECT_SUCCESS:
				onConnectSuccessOnUI();
				break;
			case MSG_DISCONECT_SUCCESS:
				onDisConnectOnUI();
				break;
			case MSG_GET_COMMAND:
				int positions [] = (int []) msg.obj;
				changePosition(positions);
				break;
			default:
				break;
			}
    	};
    };
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	getMenuInflater().inflate(R.menu.client_activity_menu, menu);
    	return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	menu.findItem(R.id.menu_disconnect).setVisible(mIsClientServiceConnect);
    	//if server is started , we should not show the start server item.
    	menu.findItem(R.id.menu_start_server).setVisible(!mIsServerServiceStarted);
    	//server is not started , need not show the end item.
    	menu.findItem(R.id.menu_end_server).setVisible(mIsServerServiceStarted);
    	
    	return super.onPrepareOptionsMenu(menu);
    }
    
    
	@Override
	public void onClick(View v) {
		if (v.equals(mConnectBtn) && null != mClientService) {
			mClientService.startConnect(mHostIp.getText().toString());
		}
		
	}
	public void onItemClick(MenuItem item){
		Log.d(TAG, "on menu item click item = "+item.getItemId());
		switch (item.getItemId()) {
		case R.id.menu_disconnect:
			if(null != mClientService){
				mClientService.disConnect();
			}
			break;
		case R.id.menu_client_config:
			

			break;
		case R.id.menu_start_server:
			mServerService.startServer();
			Toast.makeText(ClientActivity.this, R.string.server_started, Toast.LENGTH_SHORT).show();
			mIsServerServiceStarted = true;
			break;
		case R.id.menu_end_server:
			mServerService.init();
			mIsServerServiceStarted = false;
			break;
		default:
			Log.e(TAG, "have no this item id!!!");
			break;
		}
		
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mServerService != null)
			mServerService.unbindService(mConnection);
		if(mClientService != null)
			mClientService.unbindService(mConnection);
	}
	
	
	@Override
	public void getCommand(String command) {
		System.out.println("Command is ==============="+command);
		String [] positionStr = command.split("\\|");
		int [] positions = new int[positionStr.length];
		for(int index = 0;index < positionStr.length;index++){
			positions[index] = Integer.parseInt(positionStr[index]);
		}
		Message message = new Message();
		message.what = MSG_GET_COMMAND;
		message.obj = positions;
		mHandler.sendMessage(message);
	}
	public void onDisConnectOnUI(){
		mHostIp.setVisibility(View.VISIBLE);
		mHostIp.setText(null);
		mConnectBtn.setVisibility(View.VISIBLE);
		layout.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
		mImage.setVisibility(View.GONE);
	}
	
	@Override
	public void onDisconnect() {
		Message message = new Message();
		message.what = MSG_DISCONECT_SUCCESS;
		mHandler.sendMessage(message);
		mIsClientServiceConnect = false;
	}
	
	private void onConnectSuccessOnUI(){
		mHostIp.setVisibility(View.GONE);
		mConnectBtn.setVisibility(View.GONE);
		mImage.setVisibility(View.VISIBLE);
		layout.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
	}
	@Override
	public void onConnectSuccess() {
		Message message = new Message();
		message.what = MSG_CONECT_SUCCESS;
		mHandler.sendMessage(message);
		mIsClientServiceConnect = true;
		InputMethodManager m=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
		m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);  
	}

	@Override
	public void onConnectFail(String string) {
		// TODO Auto-generated method stub
		
	}
	
	public void changePosition(int positions[]){
		mImage.layout(positions[0], positions[1], positions[2], positions[3]);
	}
	
	class ImageTouchListener implements OnTouchListener{
		private float startX = 0;
		private float startY = 0;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			  switch (event.getAction())
			  {
			   case MotionEvent.ACTION_DOWN:
			   {
			    startX = event.getRawX();
			    startY = event.getRawY();
			    break;
			   }
			   case MotionEvent.ACTION_MOVE:
			   {
			    // 计算偏移量
			    int dx = (int) (event.getRawX() - startX);
			    int dy = (int) (event.getRawY() - startY);
				 System.out.println("onTouched dx = "+dx+" dy = "+dy);
			    // 计算控件的区域
			    int left = v.getLeft() + dx;
			    int right = v.getRight() + dx;
			    int top = v.getTop() + dy;
			    int bottom = v.getBottom() + dy;
			    v.layout(left, top, right, bottom);
			    startX = event.getRawX();
			    startY = event.getRawY();
			    mClientService.sendMessage(left+"|"+ top+"|"+right+"|"+bottom);
			    System.out.println("yadong left = "+left+" top = "+top+" right = "+right+" bottom="+bottom);
			    break;
			   }
			  }
			  return false;
		}
		
	}

	@Override
	public void onNewClientConnect(String ipAddress) {

	}

	@Override
	public void onClientDisconnect(String ipAddress) {
		// TODO Auto-generated method stub
		
	}

}