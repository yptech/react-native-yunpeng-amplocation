import {
	NativeModules,
	Platform,
	DeviceEventEmitter,
} from 'react-native';

let Locating = {
	startObserving: function () {
		return NativeModules.YPAMapLocation.startObserving();
	},

	stopObserving: function () {
		return NativeModules.YPAMapLocation.stopObserving();
	},

	getCurrentPosition: function (options) {
		return NativeModules.YPAMapLocation.getCurrentPosition(options);
	},

	configure: function (options) {
		this.options = options;
		if (Platform.OS !== 'ios') {
			NativeModules.YPAMapLocation.configure(options);
		}
	}
	
}
export default Locating;
