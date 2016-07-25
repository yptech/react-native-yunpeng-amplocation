# react-native-yunpeng-amplocation

support basic location and background locating 

## Getting started

### Mostly automatic install
1. `npm install rnpm --global`
2. `npm install react-native-yunpeng-amplocation --save`
3. `rnpm link react-native-yunpeng-amplocation`

### Manual install

#### Android

1. `npm install react-native-yunpeng-amplocation --save`
2. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.yunpeng.amaplocation.YPAmapLocationPackage;` to the imports at the top of the file
  - Add `new YPAmapLocationPackage()` to the list returned by the `getPackages()` method
3. Append the following lines to `android/settings.gradle`:
  	
```
include ':react-native-share'
project(':react-native-share').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-yunpeng-amaplocation/android')
```
  	
4. Insert the following lines inside the dependencies block in `android/app/build.gradle`:

```
compile project(':react-native-yunpeng-amaplocation')
```
    	
5. add android permission
			
```
<!--用于进行网络定位-->
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"></uses-permission>
<!--用于访问GPS定位-->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"></uses-permission>
<!--获取运营商信息，用于支持提供运营商信息相关的接口-->
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"></uses-permission>
<!--用于访问wifi网络信息，wifi信息会用于进行网络定位-->
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE"></uses-permission>
<!--这个权限用于获取wifi的获取权限，wifi信息会用来进行网络定位-->
<uses-permission android:name="android.permission.CHANGE_WIFI_STATE"></uses-permission>
<!--用于访问网络，网络定位需要上网-->
<uses-permission android:name="android.permission.INTERNET"></uses-permission>
<!--用于读取手机当前的状态-->
<uses-permission android:name="android.permission.READ_PHONE_STATE"></uses-permission>
<!--写入扩展存储，向扩展卡写入数据，用于写入缓存定位数据-->
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"></uses-permission>
```
			
6. add amap key in application
				
```
<meta-data android:name="com.amap.api.v2.apikey" android:value="key"></meta-data>
```

## Usage

1. configure

```
componentWillMount() {
    YPAmapLocation.configure({
        notificationTitle: "xxx",
        notificationText: "xxx!",
        notificationIconColor: "#ccc",
        startForeground: true,
        interval: 5000,
        url: api.location,
        httpHeaders: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            // TODO 更新authdata
            'Authorization': 'Basic ' + rest.authdata
        }
    }).then((result) => {
        if(result){
            console.log('configure success!');
        }
    })
}
```

2. location
	
```
YPAmapLocation.getCurrentPosition({})
	.then((position) => {
	    self.setState({region: position.coords});
	}, (err) => {
	    console.log(err);
	})
```
	
3. background Observing
		
```
// start
YPAmapLocation.startObserving()
	.then(() => {
        	console.log('start observing success');
	});

// stop
YPAmapLocation.stopObserving();
```
